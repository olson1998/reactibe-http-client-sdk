package com.github.olson1998.http.imageserial.exception;

import com.github.olson1998.http.serialization.exception.ContentSerializationException;
import org.apache.http.entity.ContentType;

public class ImageSerializationException extends ContentSerializationException {

    public ImageSerializationException(String message, Object content, ContentType contentType) {
        super(message, content, contentType);
    }

    public ImageSerializationException(String message, Throwable cause, Object content, ContentType contentType) {
        super(message, cause, content, contentType);
    }
}
