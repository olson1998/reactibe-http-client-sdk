package com.github.olson1998.http.client.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@UtilityClass
public class HttpEntityUtil {

    public static Map<String, List<String>> transformToReadOnly(Map<String, List<String>> httpHeaders){
        return httpHeaders.entrySet()
                .stream()
                .map(HttpEntityUtil::transformToReadOnlyHttpHeader)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<String, List<String>> transformToReadOnlyHttpHeader(Map.Entry<String, List<String>> httpHeaderEntry){
        var httpHeader = httpHeaderEntry.getKey();
        var values = httpHeaderEntry.getValue();
        return entry(httpHeader, values.stream().toList());
    }
}
