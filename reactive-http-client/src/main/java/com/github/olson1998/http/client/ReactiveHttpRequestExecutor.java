package com.github.olson1998.http.client;

import com.github.olson1998.http.contract.WebRequest;
import com.github.olson1998.http.contract.WebResponse;
import com.github.olson1998.http.serialization.ResponseMapping;
import com.github.olson1998.http.serialization.SerializationCodecs;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public interface ReactiveHttpRequestExecutor {

    <T> Mono<WebResponse<T>> sendHttpRequest(WebRequest webRequest, ResponseMapping<T> responseMapping);

    void serializationCodecs(Consumer<SerializationCodecs> serializationCodecsConsumer);
}
