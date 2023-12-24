package com.github.olson1998.http.jacksonserial.json.exception;

import com.github.olson1998.http.exception.ContentDeserializationException;
import org.apache.http.entity.ContentType;

public class ApplicationJsonDeserializationException extends ContentDeserializationException {

    public ApplicationJsonDeserializationException(byte[] content, ContentType contentType) {
        super(null, content, contentType);
    }

    public ApplicationJsonDeserializationException(Throwable cause, byte[] content, ContentType contentType) {
        super(null, cause, content, contentType);
    }

}
