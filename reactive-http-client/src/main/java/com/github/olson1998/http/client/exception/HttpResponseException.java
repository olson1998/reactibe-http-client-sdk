package com.github.olson1998.http.client.exception;

import com.github.olson1998.http.HttpHeaders;
import lombok.Getter;

@Getter
public class HttpResponseException extends RuntimeException {

    private static final String MESSAGE = "Http Request failed, received status code: %s, reason: %s";

    private final int statusCode;

    private final HttpHeaders httpHeaders;

    public HttpResponseException(String message, int statusCode, HttpHeaders httpHeaders) {
        super(MESSAGE.formatted(statusCode, message));
        this.statusCode = statusCode;
        this.httpHeaders = httpHeaders;
    }

    public HttpResponseException(String message, Throwable cause, int statusCode, HttpHeaders httpHeaders) {
        super(MESSAGE.formatted(statusCode, message), cause);
        this.statusCode = statusCode;
        this.httpHeaders = httpHeaders;
    }
}
