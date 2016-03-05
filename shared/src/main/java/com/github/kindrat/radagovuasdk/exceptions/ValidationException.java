package com.github.kindrat.radagovuasdk.exceptions;

public class ValidationException extends SdkGenericException {
    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
