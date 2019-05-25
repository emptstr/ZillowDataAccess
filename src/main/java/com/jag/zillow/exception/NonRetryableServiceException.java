package com.jag.zillow.exception;

public class NonRetryableServiceException extends RuntimeException{
    public NonRetryableServiceException(String message) {
        super(message);
    }
}
