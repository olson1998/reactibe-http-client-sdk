package com.github.olson1998.http;

import org.apache.http.entity.ContentType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HttpHeaders extends Map<String, List<String>>{

    List<HttpHeader> getHttpHeaderList();

    String getFirstValue(String httpHeader);

    Optional<ContentType> findContentType();

    Optional<String> findFirstValue(String httpHeader);

    void appendHttpHeader(HttpHeader httpHeader);

    void appendHttpHeader(String httpHeader, String httpHeaderValue);

    HttpHeaders readOnly();

    static interface Builder{

        Builder nextHeader(HttpHeader httpHeader);

        Builder nextHeader(String httpHeader, String httpHeaderValues);

        Builder nextHeaders(HttpHeaders httpHeaders);

        Builder nextHeadersMap(Map<String, List<String>> httpHeaders);

        Builder contentType(ContentType contentType);
    }
}
