package com.github.olson1998.http.serialization.exception;

import lombok.Getter;
import org.apache.http.entity.ContentType;

import java.util.Optional;

@Getter
public class ContentDeserializationException extends RuntimeException {

    private static final String MESSAGE = "Failed to deserialize content of type: '%s', content length: %s";

    private final byte[] content;

    private final ContentType contentType;

    public ContentDeserializationException(String message, byte[] content, ContentType contentType) {
        super(writeMessage(content, contentType, message));
        this.content = content;
        this.contentType = contentType;
    }

    public ContentDeserializationException(String message, Throwable cause, byte[] content, ContentType contentType) {
        super(writeMessage(content, contentType, message), cause);
        this.content = content;
        this.contentType = contentType;
    }

    public ContentDeserializationException(Throwable cause, String message, byte[] content, ContentType contentType) {
        super(writeMessage(content, contentType, message), cause);
        this.content = content;
        this.contentType = contentType;
    }

    private static String writeMessage(byte[] content, ContentType contentType, String message) {
        var messageBuilder = new StringBuilder();
        var length = Optional.ofNullable(content)
                .map(bytes -> bytes.length)
                .map(len -> len + " bytes")
                .orElse("null");
        var baseMessage = MESSAGE.formatted(contentType, length);
        messageBuilder.append(baseMessage);
        Optional.ofNullable(message).ifPresent(additionalMsg -> messageBuilder.append(", reason: ").append(additionalMsg));
        return messageBuilder.toString();
    }
}
