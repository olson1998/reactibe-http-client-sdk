package com.github.olson1998.http.client;

import com.github.olson1998.http.contract.WebRequest;
import com.github.olson1998.http.contract.WebResponse;
import com.github.olson1998.http.serialization.ResponseMapping;
import com.github.olson1998.http.serialization.SerializationCodecs;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.function.Consumer;

public interface ReactiveHttpRequestExecutor {

    Mono<WebResponse<byte[]>> sendHttpRequest(WebRequest webRequest);

    <T> Mono<WebResponse<T>> sendHttpRequest(WebRequest webRequest, Class<T> responseMapping);

    <T> Mono<WebResponse<T>> sendHttpRequest(WebRequest webRequest, ResponseMapping<T> responseMapping);

    void addHttpHeader(String httpHeader, String httpHeaderValue);

    void addHttpHeaders(String httpHeader, Iterable<String> httpHeaderValues);

    void removeHttpHeader(String httpHeader);

    void removeHttpHeader(String httpHeader, Collection<String> httpHeaderValues);

    void serializationCodecs(Consumer<SerializationCodecs> serializationCodecsConsumer);
}
