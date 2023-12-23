package com.github.olson1998.http.contract;

import java.util.List;
import java.util.Map;

public interface HttpOutputMessage {

    int statusCode();

    Map<String, List<String>> httpHeaders();

    byte[] bodyBytes();
}
