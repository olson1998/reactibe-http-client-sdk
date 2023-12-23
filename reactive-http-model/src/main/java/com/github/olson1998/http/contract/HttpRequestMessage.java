package com.github.olson1998.http.contract;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public record HttpRequestMessage(URI uri, String httpMethod, Map<String, List<String>> httpHeaders, byte[] body) implements HttpInputMessage {

    public static Builder builder(){
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.MODULE)
    static class Builder implements HttpInputMessage.Builder{

        private URI uri;

        private String httpMethod;

        private byte[] body;

        private final Map<String, List<String>> httpHeaders = new HashMap<>();

        @Override
        public HttpInputMessage.Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        @Override
        public HttpInputMessage.Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        @Override
        public HttpInputMessage.Builder addHttpHeader(String httpHeader, String httpHeaderValue) {
            if(this.httpHeaders.containsKey(httpHeader)){
                var httpHeaderValues = this.httpHeaders.get(httpHeader);
                httpHeaderValues.add(httpHeader);
            }else {
                var httpHeaderValues = new ArrayList<String>();
                httpHeaderValues.add(httpHeader);
                this.httpHeaders.put(httpHeader, httpHeaderValues);
            }
            return this;
        }

        @Override
        public HttpInputMessage.Builder addHttpHeaders(String httpHeader, Iterable<String> headerValues) {
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
        public HttpInputMessage.Builder body(byte[] requestBody) {
            this.body = requestBody;
            return this;
        }

        @Override
        public HttpInputMessage build() {
            Objects.requireNonNull(uri, "Http request URI has not been specified");
            Objects.requireNonNull(httpMethod, "Http method has not been specified");
            var readOnlyHttpHeaders = writeReadOnlyHttpHeaders();
            return new HttpRequestMessage(uri, httpMethod, readOnlyHttpHeaders, body);
        }

        private Map<String, List<String>> writeReadOnlyHttpHeaders(){
            var httpHeadersEntries = new ArrayList<Map.Entry<String, List<String>>>();
            httpHeaders.forEach((httpHeader, httpHeaderValues) ->{
                var readOnlyHeaderValues = httpHeaderValues.stream().toList();
                httpHeadersEntries.add(entry(httpHeader, readOnlyHeaderValues));
            });
            return httpHeadersEntries.stream()
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }
}
