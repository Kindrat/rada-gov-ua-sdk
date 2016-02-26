package com.github.kindrat.radagovuasdk.client;

import com.github.kindrat.radagovuasdk.client.dto.ListItem;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.github.kindrat.radagovuasdk.Patterns.*;
import static com.github.kindrat.radagovuasdk.util.ParsingUtil.streamFromMatcher;

@Slf4j
public class ApacheRadaClient implements RadaClient {
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
    public List<ListItem> listCategory(ListItem category) {
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
                        return new ListItem(title, uri);
                    }).collect(Collectors.toList());
        });
    }

    @Override
    public String getEntryContent(ListItem categoryEntry) {
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
