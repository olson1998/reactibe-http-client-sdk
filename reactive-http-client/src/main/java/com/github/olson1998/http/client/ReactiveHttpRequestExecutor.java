package com.github.olson1998.http.client;

import com.github.olson1998.http.contract.WebRequest;
import com.github.olson1998.http.contract.WebResponse;
import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ContentSerializer;
import reactor.core.publisher.Mono;

public interface ReactiveHttpRequestExecutor {

    <C> Mono<WebResponse<C>> executeHttpRequest(WebRequest webRequest, ContentDeserializer<C> contentDeserializer);

    <T, C> Mono<WebResponse<C>> executeHttpRequest(WebRequest webRequest,T content, ContentSerializer<T> contentSerializer, ContentDeserializer<C> contentDeserializer);

    Mono<WebResponse<byte[]>> executeHttpRequestForResponseBodyBytes(WebRequest webRequest);

    <T> Mono<WebResponse<byte[]>> executeHttpRequestForResponseBodyBytes(WebRequest webRequest,T content, ContentSerializer<T> contentSerializer);

    Mono<WebResponse<Void>> executeHttpRequestForNoResponseBody(WebRequest webRequest);

    <T> Mono<WebResponse<Void>> executeHttpRequestForNoResponseBody(WebRequest webRequest,T content, ContentSerializer<T> contentSerializer);
}
