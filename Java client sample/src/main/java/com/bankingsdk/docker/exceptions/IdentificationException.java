package com.bankingsdk.docker.exceptions;

public class IdentificationException extends Exception {
    public IdentificationException() {
        super();
    }

    public IdentificationException(String message) {
        super(message);
    }

    public IdentificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentificationException(Throwable cause) {
        super(cause);
    }

    protected IdentificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
