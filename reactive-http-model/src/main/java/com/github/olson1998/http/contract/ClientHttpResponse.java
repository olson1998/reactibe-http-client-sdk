package com.github.olson1998.http.contract;

import com.github.olson1998.http.HttpHeaders;

public record ClientHttpResponse<C>(int statusCode, HttpHeaders httpHeaders, C body) implements WebResponse<C> {

}
