package com.github.kindrat.radagovuasdk.exceptions;

public class SdkGenericException extends RuntimeException {
    public SdkGenericException(Throwable cause) {
        super(cause);
    }

    public SdkGenericException(String message) {
        super(message);
    }

    public SdkGenericException(String message, Throwable cause) {
        super(message, cause);
    }
}
