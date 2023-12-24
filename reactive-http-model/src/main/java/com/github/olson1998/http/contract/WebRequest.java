package com.github.olson1998.http.contract;

import org.apache.http.entity.ContentType;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WebRequest {

    URI uri();

    String httpMethod();

    Map<String, List<String>> httpHeaders();

    Object body();

    Optional<ContentType> findContentType();

    Duration timeoutDuration();

    static Builder builder(){
        return new ClientHttpRequest.Builder();
    }

    interface Builder{

        Builder uri(URI uri);

        Builder httpMethod(String httpMethod);

        Builder addHttpHeader(String httpHeader, String httpHeaderValue);

        Builder addHttpHeaders(String httpHeader, Iterable<String> headerValues);

        Builder contentType(ContentType contentType);

        Builder body(Object body);

        Builder timeoutDuration(Duration duration);

        WebRequest build();
    }
}
