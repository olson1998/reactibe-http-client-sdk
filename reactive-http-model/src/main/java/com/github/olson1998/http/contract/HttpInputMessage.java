package com.github.olson1998.http.contract;

import org.reactivestreams.Publisher;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface HttpInputMessage {

    URI uri();

    String httpMethod();

    Map<String, List<String>> httpHeaders();

    Publisher<?> body();

    static Builder builder(){
        return new HttpRequestMessage.Builder();
    }

    interface Builder{

        Builder uri(URI uri);

        Builder httpMethod(String httpMethod);

        Builder addHttpHeader(String httpHeader, String httpHeaderValue);

        Builder addHttpHeaders(String httpHeader, Iterable<String> headerValues);

        Builder body(Publisher<?> body);

        HttpInputMessage build();
    }
}
