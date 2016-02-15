package com.github.kindrat.radagovuasdk.client;

import com.github.kindrat.radagovuasdk.exceptions.RestClientException;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class ApacheRadaClient implements RadaClient {

    private final CloseableHttpClient httpClient;

    public ApacheRadaClient(ClientConfiguration configuration) {
        httpClient = HttpClientBuilder.create().build();
    }

    @Override
    public List<ListItem> listAllCategories() {
        return null;
    }

    @Override
    public List<ListItem> listCategory(String category) {
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
