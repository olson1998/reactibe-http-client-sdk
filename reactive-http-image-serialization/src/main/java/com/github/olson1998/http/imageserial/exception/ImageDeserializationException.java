package com.github.olson1998.http.imageserial.exception;

import com.github.olson1998.http.serialization.exception.ContentDeserializationException;
import org.apache.http.entity.ContentType;

public class ImageDeserializationException extends ContentDeserializationException {

    public ImageDeserializationException(String message, byte[] content, ContentType contentType) {
        super(message, content, contentType);
    }

    public ImageDeserializationException(String message, Throwable cause, byte[] content, ContentType contentType) {
        super(message, cause, content, contentType);
    }

    public ImageDeserializationException(Throwable cause, String message, byte[] content, ContentType contentType) {
        super(cause, message, content, contentType);
    }
}
