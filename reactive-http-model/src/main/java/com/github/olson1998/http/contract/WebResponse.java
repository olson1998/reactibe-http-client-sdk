package com.github.olson1998.http.contract;

import com.github.olson1998.http.HttpHeaders;

public interface WebResponse<C> {

    int statusCode();

    HttpHeaders httpHeaders();

    C body();
}
