package com.github.olson1998.http.contract;

import com.github.olson1998.http.HttpHeader;
import com.github.olson1998.http.HttpHeaders;
import com.github.olson1998.http.HttpMethod;
import org.apache.http.entity.ContentType;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WebRequest {

    URI getUri();

    HttpMethod getHttpMethod();

    HttpHeaders getHttpHeaders();

    Object getBody();

    Optional<ContentType> findContentType();

    Map<String, Object> getAttributes();

    static Builder builder(){
        return new ClientHttpRequest.Builder();
    }

    interface Builder{

        Builder uri(URI uri);

        Builder httpMethod(HttpMethod httpMethod);

        Builder addHttpHeader(HttpHeader httpHeader);

        Builder addHttpHeader(String httpHeader, String httpHeaderValue);

        Builder addHttpHeaders(String httpHeader, Iterable<String> headerValues);

        Builder addHttpHeaders(HttpHeaders httpHeaders);

        Builder contentType(ContentType contentType);

        Builder body(Object body);

        Builder addAttribute(String attribute, Object values);

        Builder addAttributes(Map<String, Object> attributes);

        Builder removeAttribute(String attribute);

        WebRequest build();
    }
}
