package com.bankingsdk.docker.exceptions;

public class ApiCallException extends Exception {
    public ApiCallException() {
        super();
    }

    public ApiCallException(String message) {
        super(message);
    }

    public ApiCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiCallException(Throwable cause) {
        super(cause);
    }

    protected ApiCallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}