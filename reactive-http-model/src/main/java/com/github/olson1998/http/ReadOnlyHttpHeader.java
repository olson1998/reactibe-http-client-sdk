package com.github.olson1998.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class ReadOnlyHttpHeader implements HttpHeader {

    private final String key;

    private final String value;

    @Override
    public String setValue(String value) {
        return this.value;
    }

    @Override
    public HttpHeader readOnly() {
        return this;
    }

    @Override
    public String toString() {
        var httpHeader = new StringBuilder(key);
        Optional.ofNullable(value).ifPresent(headerValue -> httpHeader.append(": ").append(value));
        return httpHeader.toString();
    }

}
