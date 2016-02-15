package com.github.kindrat.radagovuasdk.exceptions;

import java.net.URI;

import static com.github.kindrat.radagovuasdk.utils.StringUtils.format;

public class RestClientException extends SdkGenericException {
    public RestClientException(Throwable cause) {
        super(cause);
    }

    public RestClientException(String message) {
        super(message);
    }

    public RestClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestClientException(String method, URI uri, int statusCode, String reason) {
        super(format("[{}] {} [{}] : {}", method, uri, statusCode, reason));
    }
}
