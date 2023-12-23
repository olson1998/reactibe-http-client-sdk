package com.github.olson1998.http.client;

import com.github.olson1998.http.contract.HttpInputMessage;
import com.github.olson1998.http.contract.HttpOutputMessage;
import com.github.olson1998.http.serial.ContentSerializer;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface ReactiveHttpRequestExecutor {

    Mono<HttpOutputMessage> executeHttpRequest(HttpInputMessage httpInputMessage);

    Mono<HttpOutputMessage> executeHttpRequest(HttpInputMessage httpInputMessage, Duration timeoutDuration);

    <T> Mono<HttpOutputMessage> executeHttpRequest(HttpInputMessage httpInputMessage, ContentSerializer<T> contentSerializer, T content, Duration timeoutDuration);

}
