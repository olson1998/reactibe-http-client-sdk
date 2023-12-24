package com.github.olson1998.http.contract;

import java.util.List;
import java.util.Map;

public record ClientHttpResponse<C>(int statusCode, Map<String, List<String>> httpHeaders, C body) implements WebResponse<C> {

}
