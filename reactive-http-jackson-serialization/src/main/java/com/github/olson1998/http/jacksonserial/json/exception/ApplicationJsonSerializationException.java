package com.github.olson1998.http.jacksonserial.json.exception;

import com.github.olson1998.http.serialization.exception.ContentSerializationException;
import org.apache.http.entity.ContentType;

public class ApplicationJsonSerializationException extends ContentSerializationException {

    public ApplicationJsonSerializationException(byte[] content, ContentType contentType) {
        super(null, content, contentType);
    }

    public ApplicationJsonSerializationException(Throwable cause, byte[] content, ContentType contentType) {
        super(null, cause, content, contentType);
    }
}
