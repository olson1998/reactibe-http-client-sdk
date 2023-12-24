package com.github.olson1998.http.contract;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.entity.ContentType;

import java.net.URI;
import java.time.Duration;
import java.util.*;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public record ClientHttpRequest(URI uri, String httpMethod, Map<String, List<String>> httpHeaders, Object body, Duration timeoutDuration) implements WebRequest {

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

        private String httpMethod;

        private Duration timeoutDuration;

        private final Map<String, List<String>> httpHeaders = new HashMap<>();

        private Object body;

        @Override
        public WebRequest.Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        @Override
        public WebRequest.Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        @Override
        public WebRequest.Builder addHttpHeader(String httpHeader, String httpHeaderValue) {
            if(this.httpHeaders.containsKey(httpHeader)){
                var httpHeaderValues = this.httpHeaders.get(httpHeader);
                httpHeaderValues.add(httpHeader);
            }else {
                var httpHeaderValues = new ArrayList<String>();
                httpHeaderValues.add(httpHeaderValue);
                this.httpHeaders.put(httpHeader, httpHeaderValues);
            }
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
        public WebRequest.Builder contentType(ContentType contentType) {
            addHttpHeader(CONTENT_TYPE, contentType.toString());
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
