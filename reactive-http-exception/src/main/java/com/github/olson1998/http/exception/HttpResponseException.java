package com.github.olson1998.http.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class HttpResponseException extends RuntimeException {

    private static final String MESSAGE = "Http Request failed, received status code: %s, reason: %s";

    private final int statusCode;

    private final Map<String, List<String>> httpHeaders;

    public HttpResponseException(String message, int statusCode, Map<String, List<String>> httpHeaders) {
        super(MESSAGE.formatted(statusCode, message));
        this.statusCode = statusCode;
        this.httpHeaders = httpHeaders;
    }

    public HttpResponseException(String message, Throwable cause, int statusCode, Map<String, List<String>> httpHeaders) {
        super(MESSAGE.formatted(statusCode, message), cause);
        this.statusCode = statusCode;
        this.httpHeaders = httpHeaders;
    }
}
