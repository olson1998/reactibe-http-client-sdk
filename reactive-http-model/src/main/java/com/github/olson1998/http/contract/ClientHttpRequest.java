package com.github.olson1998.http.contract;

import com.github.olson1998.http.Headers;
import com.github.olson1998.http.HttpHeader;
import com.github.olson1998.http.HttpHeaders;
import com.github.olson1998.http.HttpMethod;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.entity.ContentType;

import java.net.URI;
import java.time.Duration;
import java.util.*;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public record ClientHttpRequest(URI uri, HttpMethod httpMethod, HttpHeaders httpHeaders, Object body, Duration timeoutDuration) implements WebRequest {

    @Override
    public Optional<ContentType> findContentType() {
        if(httpHeaders.containsKey(CONTENT_TYPE)){
            return httpHeaders.get(CONTENT_TYPE).stream()
                    .map(ContentType::parse)
                    .findFirst();
        }else {
            return Optional.empty();
        }
    }

    public static Builder builder(){
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.MODULE)
    static class Builder implements WebRequest.Builder{

        private URI uri;

        private HttpMethod httpMethod;

        private Duration timeoutDuration;

        private final HttpHeaders httpHeaders = new Headers();

        private Object body;

        @Override
        public WebRequest.Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        @Override
        public WebRequest.Builder httpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        @Override
        public WebRequest.Builder addHttpHeader(HttpHeader httpHeader) {
            httpHeaders.appendHttpHeader(httpHeader);
            return this;
        }

        @Override
        public WebRequest.Builder addHttpHeader(String httpHeader, String httpHeaderValue) {
            httpHeaders.appendHttpHeader(httpHeader, httpHeaderValue);
            return this;
        }

        @Override
        public WebRequest.Builder addHttpHeaders(String httpHeader, Iterable<String> headerValues) {
            if(this.httpHeaders.containsKey(httpHeader)){
                var httpHeaderValues = this.httpHeaders.get(httpHeader);
                headerValues.forEach(httpHeaderValues::add);
            }else {
                var httpHeaderValues = new ArrayList<String>();
                headerValues.forEach(httpHeaderValues::add);
                this.httpHeaders.put(httpHeader, httpHeaderValues);
            }
            return this;
        }

        @Override
        public WebRequest.Builder addHttpHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders.putAll(httpHeaders);
            return this;
        }

        @Override
        public WebRequest.Builder contentType(ContentType contentType) {
            addHttpHeader(CONTENT_TYPE, contentType.getMimeType());
            return this;
        }

        @Override
        public WebRequest.Builder body(Object body) {
            this.body = body;
            return this;
        }

        @Override
        public WebRequest.Builder timeoutDuration(Duration duration) {
            this.timeoutDuration = duration;
            return this;
        }


        @Override
        public WebRequest build() {
            Objects.requireNonNull(uri, "Http request URI has not been specified");
            Objects.requireNonNull(httpMethod, "Http method has not been specified");
            return new ClientHttpRequest(uri, httpMethod, httpHeaders, body, timeoutDuration);
        }

    }
}
