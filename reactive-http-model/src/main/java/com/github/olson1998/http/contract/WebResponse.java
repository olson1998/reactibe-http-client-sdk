package com.github.olson1998.http.contract;

import com.github.olson1998.http.HttpHeaders;

public interface WebResponse<C> {

    int getStatusCode();

    HttpHeaders getHttpHeaders();

    C getBody();
}
