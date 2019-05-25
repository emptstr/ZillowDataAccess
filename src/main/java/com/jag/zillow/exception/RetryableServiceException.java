package com.jag.zillow.exception;

public class RetryableServiceException extends RuntimeException {

    public RetryableServiceException(String message) {
        super(message);
    }
}
