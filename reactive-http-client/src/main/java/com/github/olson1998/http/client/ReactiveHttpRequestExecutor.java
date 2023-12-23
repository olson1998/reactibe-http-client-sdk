package com.github.olson1998.http.client;

import com.github.olson1998.http.contract.HttpInputMessage;
import com.github.olson1998.http.contract.HttpOutputMessage;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface ReactiveHttpRequestExecutor {

    Mono<HttpOutputMessage> executeHttpRequest(HttpInputMessage httpInputMessage, Duration timeoutDuration);

}
