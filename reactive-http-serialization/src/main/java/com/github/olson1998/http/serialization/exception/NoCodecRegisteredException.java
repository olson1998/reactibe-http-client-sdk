package com.github.olson1998.http.serialization.exception;

import org.apache.http.entity.ContentType;

public class NoCodecRegisteredException extends RuntimeException{

    private static final String MESSAGE = "Codec registry doesn't have registered codec for content type: '%s'";

    public NoCodecRegisteredException(ContentType contentType) {
        super(MESSAGE.formatted(contentType));
    }
}
