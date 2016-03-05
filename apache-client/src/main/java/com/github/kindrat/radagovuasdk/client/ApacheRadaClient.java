package com.github.kindrat.radagovuasdk.client;

import com.github.kindrat.radagovuasdk.client.dto.*;
import com.github.kindrat.radagovuasdk.client.dto.DocumentCard.DocumentCardBuilder;
import com.github.kindrat.radagovuasdk.client.dto.DocumentMetadata.DocumentMetadataBuilder;
import com.github.kindrat.radagovuasdk.exceptions.RestClientException;
import com.github.kindrat.radagovuasdk.util.ParsingUtil;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.github.kindrat.radagovuasdk.Patterns.*;
import static com.github.kindrat.radagovuasdk.util.ParsingUtil.streamFromMatcher;
import static com.github.kindrat.radagovuasdk.utils.EnumUtils.parseEnum;
import static com.google.common.collect.Maps.newHashMap;

/**
 * This client should allow retrieve different sensitive gov data in the most convenient and effective way.
 * That could be the most complex regex ever seen or anything else, but not dom parsing (I believe). But Jsoup is
 * a good point to start with and test the whole functionality
 * FIXME optimize after completion
 */
@Slf4j
public class ApacheRadaClient implements RadaClient {
    private static final Map<Predicate<String>, BiConsumer<String, DocumentCardBuilder>> CARD_FUNCTIONS = newHashMap();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.mm.yyyy");
    private static final String CARD_URL = "card2#Card";

    static {
        CARD_FUNCTIONS.put(s -> s.contains("Види"), (s, builder) -> builder.type(parseEnum(Type.class, s)));
        CARD_FUNCTIONS.put(s -> s.contains("Видавники"), (s, builder) -> builder.publisher(s));
        CARD_FUNCTIONS.put(s -> s.contains("Дати"), (s, builder) -> {
            Matcher date = DATE_PATTERN.matcher(s);
            if (date.find()) {
                try {
                    builder.date(DATE_FORMAT.parse(date.group()));
                } catch (ParseException e) {
                    e.printStackTrace(); //FIXME
                }
            }
        });
        CARD_FUNCTIONS.put(s -> s.contains("Номери"), (s, builder) -> builder.regNumber(s));
        CARD_FUNCTIONS.put(s -> s.contains("Стан"), (s, builder) -> builder.state(parseEnum(State.class, s)));
        CARD_FUNCTIONS.put(s -> s.contains("Ідентифікатор"), (s, builder) -> builder.uid(s));
    }

    private final CloseableHttpClient httpClient;
    private final ClientConfiguration configuration;

    public ApacheRadaClient(ClientConfiguration configuration) {
        this.configuration = configuration;
        httpClient = HttpClientBuilder.create().build();
    }

    @Override
    public List<ListItem> listAllCategories() {
        HttpGet get = new HttpGet(new UriSupplier(configuration.getBaseUri(), configuration.getRootMenu()).get());
        return doRequestAndParseResponse(get, entity -> {
            Document document = ParsingUtil.wrapHttpEntity(entity);
            Element menu = document.getElementsByTag("table").stream()
                    .filter(element -> element.hasClass("num") && element.attributes().get("bgcolor").equals("white"))
                    .findAny()
                    .orElseThrow(() -> new RestClientException("Could not find main menu"));
            String script = menu.select("script").first().html();
            return streamFromMatcher(MENU_LINE_PATTERN.matcher(script)).map(string -> {
                Matcher uriMatcher = MENU_URL_PATTERN.matcher(string);
                Matcher titleMatcher = MENU_TITLE_PATTERN.matcher(string);
                String uri = uriMatcher.find() ? uriMatcher.group() : "";
                String title = titleMatcher.find() ? titleMatcher.group(1) : "";
                return new ListItem(title, uri);
            }).collect(Collectors.toList());
        });
    }

    @Override
    public List<DocumentEntry> listCategory(ListItem category) {
        HttpGet get = new HttpGet(new UriSupplier(configuration.getBaseUri(), category.getUri()).get());
        return doRequestAndParseResponse(get, entity -> {
            Document document = ParsingUtil.wrapHttpEntity(entity);
            return document.getElementsByClass("nam").stream()
                    .flatMap(element -> element.children().stream())
                    .flatMap(element -> element.children().stream())
                    .flatMap(element -> element.children().stream())
                    .map(Element::html)
                    .map(string -> {
                        log.debug("Parsing category element : {}", string);
                        Matcher uriMatcher = LINK_PATTERN.matcher(string);
                        if (!uriMatcher.find()) {
                            throw new RestClientException("Could not parse category content entry " + string);
                        }
                        String uri = Optional.ofNullable(uriMatcher.group(2)).orElse("");
                        String title = Optional.ofNullable(uriMatcher.group(5)).orElse("");
                        return new DocumentEntry(title, uri);
                    }).collect(Collectors.toList());
        });
    }

    @Override
    public DocumentMetadata getDocumentMetadata(DocumentEntry documentEntry) {
        HttpGet get = new HttpGet(new UriSupplier(configuration.getBaseUri(), documentEntry.getUri(), CARD_URL).get());
        return doRequestAndParseResponse(get, entity -> {
            Document document = ParsingUtil.wrapHttpEntity(entity);
            Element holder = document.getElementsByClass("txt").stream()
                    .findAny().orElseThrow(() -> new RestClientException("Could not find metadata holder"));
            Element card = holder.getElementsByClass("nam").stream()
                    .findAny().orElseThrow(() -> new RestClientException("Could not find card holder"));

            DocumentMetadataBuilder metadataBuilder = DocumentMetadata.builder();
            DocumentCardBuilder cardBuilder = DocumentCard.builder();

            card.getAllElements().stream().filter(element -> element.hasClass("num") && element.tagName().equals("dt"))
                    .forEachOrdered(element -> {
                        String delimiterText = element.text();
                        BiConsumer<String, DocumentCardBuilder> rawDataConsumer = CARD_FUNCTIONS.entrySet().stream()
                                .filter(entry -> entry.getKey().test(delimiterText)).findAny()
                                .map(Map.Entry::getValue)
                                .orElseThrow(() ->
                                        new RestClientException("Could not parse card entry " + delimiterText));

                        for (Element nextSibling = element.nextElementSibling();
                             nextSibling != null && nextSibling.tagName().equals("dd");
                             nextSibling = nextSibling.nextElementSibling()) {
                            rawDataConsumer.accept(nextSibling.text(), cardBuilder);
                        }
                    });
            metadataBuilder.documentCard(cardBuilder.build());
            return metadataBuilder.build();
        });
    }

    @Override
    public String getEntryContent(DocumentMetadata metadata) {
        return null;
    }

    @Override
    public DocumentCard getEntryCard(DocumentMetadata metadata) {
        return null;
    }

    @Override
    public RelatedDocsLink getLinkToRelatedDocs(DocumentMetadata metadata) {
        return null;
    }


    @Override
    public List<DocumentEntry> listEntryFiles(RelatedDocsLink relatedDocsLink) {
        return null;
    }

    @Override
    public List<HistoryEntry> listEntryHistory(DocumentMetadata metadata) {
        return null;
    }

    private <T> T doRequestAndParseResponse(HttpUriRequest request, Function<HttpEntity, T> parseFunction) throws RestClientException {
        try (CloseableHttpResponse response = executeRequest(request)) {
            checkResponseWasSuccessful(request, response);
            return parseFunction.apply(response.getEntity());
        } catch (IOException e) {
            throw new RestClientException(e);
        }
    }

    private CloseableHttpResponse executeRequest(HttpUriRequest entity) throws IOException {
        log.debug("Executing {} request to {}", entity.getMethod(), entity.getURI().normalize().toASCIIString());
        return httpClient.execute(entity);
    }

    private void checkResponseWasSuccessful(HttpUriRequest entity, CloseableHttpResponse response) throws RestClientException {
        StatusLine statusLine = response.getStatusLine();
        if (isNotResponseSuccessful(statusLine)) {
            String reason = extractReason(response).orElse(statusLine.getReasonPhrase());
            throw new RestClientException(entity.getMethod(), entity.getURI(), statusLine.getStatusCode(), reason);
        }
    }

    private boolean isNotResponseSuccessful(StatusLine statusLine) {
        return statusLine.getStatusCode() < HttpStatus.SC_OK || statusLine.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES;
    }

    private Optional<String> extractReason(CloseableHttpResponse response) {
        return Optional.ofNullable(response.getEntity()).map(entity -> {
            try {
                return IOUtils.toString(entity.getContent(), Charsets.UTF_8.name());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        });
    }
}
