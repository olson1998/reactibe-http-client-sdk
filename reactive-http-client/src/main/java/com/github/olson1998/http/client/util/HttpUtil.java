package com.github.olson1998.http.client.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtil {

    public static Map<String, List<String>> transformToReadOnly(Map<String, List<String>> httpHeaders) {
        return httpHeaders.entrySet()
                .stream()
                .map(HttpUtil::transformToReadOnlyHttpHeader)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, List<String>> transformToReadWrite(Map<String, List<String>> httpHeaders) {
        return httpHeaders.entrySet()
                .stream()
                .map(HttpUtil::transformToReadWriteHeader)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static void appendHttpHeader(@NonNull Map<String, List<String>> httpHeaders, @NonNull String header, String value) {
        Optional.ofNullable(httpHeaders.get(header)).ifPresentOrElse(values -> values.add(value), () -> {
            var values = new ArrayList<String>();
            values.add(value);
            httpHeaders.put(header, values);
        });
    }

    public static void appendHttpHeaders(@NonNull Map<String, List<String>> httpHeaders, @NonNull String header, @NonNull Iterable<String> valuesIterable) {
        Optional.ofNullable(httpHeaders.get(header)).ifPresentOrElse(values -> valuesIterable.forEach(values::add), () -> {
            var values = new ArrayList<String>();
            valuesIterable.forEach(values::add);
            httpHeaders.put(header, values);
        });
    }

    public static void removeHttpHeaders(@NonNull Map<String, List<String>> httpHeaders, @NonNull String header, @NonNull Collection<String> valuesCollection) {
        Optional.ofNullable(httpHeaders.get(header)).ifPresent(values -> values.removeAll(valuesCollection));
    }

    private static Map.Entry<String, List<String>> transformToReadOnlyHttpHeader(Map.Entry<String, List<String>> httpHeaderEntry) {
        var httpHeader = httpHeaderEntry.getKey();
        var values = httpHeaderEntry.getValue();
        return entry(httpHeader, values.stream().toList());
    }

    private static Map.Entry<String, List<String>> transformToReadWriteHeader(Map.Entry<String, List<String>> httpHeaderEntry) {
        var httpHeader = httpHeaderEntry.getKey();
        var values = new ArrayList<>(httpHeaderEntry.getValue());
        return entry(httpHeader, values);
    }
}
