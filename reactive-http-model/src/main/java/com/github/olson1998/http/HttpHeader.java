package com.github.olson1998.http;

import java.util.Map;

public interface HttpHeader extends Map.Entry<String, String> {

    HttpHeader readOnly();

    static HttpHeader of(String httpHeader, String httpHeaderValue){
        return new Header(httpHeader, httpHeaderValue);
    }

    static HttpHeader of(String httpHeader){
        return new Header(httpHeader, null);
    }
}
