package com.github.olson1998.http.exception;

import lombok.Getter;
import org.apache.http.entity.ContentType;

import java.util.Optional;

@Getter
public class ContentSerializationException extends RuntimeException{

    private static final String MESSAGE = "Failed to serialize content of type: '%s'";

    private final Object content;

    private final ContentType contentType;

    public ContentSerializationException(String message, Object content, ContentType contentType) {
        super(writeMessage(contentType, message));
        this.content = content;
        this.contentType = contentType;
    }

    public ContentSerializationException(String message, Throwable cause,Object content,  ContentType contentType) {
        super(writeMessage(contentType, message), cause);
        this.content = content;
        this.contentType = contentType;
    }

    private static String writeMessage(ContentType contentType, String message){
        var messageBuilder = new StringBuilder();
        var baseMessage = MESSAGE.formatted(contentType);
        messageBuilder.append(baseMessage);
        Optional.ofNullable(message)
                .ifPresent(extendedMessage -> messageBuilder.append(", reason: ").append(extendedMessage));
        return messageBuilder.toString();
    }
}
