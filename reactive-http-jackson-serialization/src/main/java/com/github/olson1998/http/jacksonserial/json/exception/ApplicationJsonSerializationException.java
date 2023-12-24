package com.github.olson1998.http.jacksonserial.json.exception;

public class ApplicationJsonSerializationException extends RuntimeException {

    private static final String MESSAGE = "Failed to serialize: 'application/json'";

    public ApplicationJsonSerializationException(Throwable e) {
        super(MESSAGE, e);
    }

}
