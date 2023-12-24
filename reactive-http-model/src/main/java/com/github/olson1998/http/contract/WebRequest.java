package com.github.olson1998.http.contract;

import com.github.olson1998.http.serialization.ContentSerializer;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface WebRequest {

    URI uri();

    String httpMethod();

    Map<String, List<String>> httpHeaders();

    Duration timeoutDuration();

    static Builder builder(){
        return new ClientHttpRequest.Builder();
    }

    interface Builder{

        Builder uri(URI uri);

        Builder httpMethod(String httpMethod);

        Builder addHttpHeader(String httpHeader, String httpHeaderValue);

        Builder addHttpHeaders(String httpHeader, Iterable<String> headerValues);

        Builder timeoutDuration(Duration duration);

        WebRequest build();
    }
}
