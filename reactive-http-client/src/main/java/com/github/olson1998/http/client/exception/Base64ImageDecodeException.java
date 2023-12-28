package com.github.olson1998.http.client.exception;

public class Base64ImageDecodeException extends RuntimeException {

    private static final String MESSAGE = "Failed to decode base64 image";

    public Base64ImageDecodeException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
