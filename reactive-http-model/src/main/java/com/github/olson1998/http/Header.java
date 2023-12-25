package com.github.olson1998.http;

import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

@Getter
public class Header implements HttpHeader {

    private final String key;

    private String value;

    public Header(@NonNull String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String setValue(String value) {
        var oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    @Override
    public String toString() {
        var httpHeader = new StringBuilder(key);
        Optional.ofNullable(value).ifPresent(headerValue -> httpHeader.append(": ").append(value));
        return httpHeader.toString();
    }
}
