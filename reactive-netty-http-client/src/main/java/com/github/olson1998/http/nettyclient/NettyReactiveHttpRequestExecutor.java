package com.github.olson1998.http.nettyclient;

import com.github.olson1998.http.client.ReactiveHttpRequestExecutor;
import com.github.olson1998.http.contract.HttpInputMessage;
import com.github.olson1998.http.contract.HttpOutputMessage;
import com.github.olson1998.http.contract.HttpResponseMessage;
import com.github.olson1998.http.exception.HttpRequestException;
import com.github.olson1998.http.nettyclient.util.NettyUtil;
import com.github.olson1998.http.serial.ContentSerializer;
import io.netty.handler.codec.http.HttpMethod;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientForm;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class NettyReactiveHttpRequestExecutor implements ReactiveHttpRequestExecutor {

    private final HttpClient httpClient;

    @Override
    public Mono<HttpOutputMessage> executeHttpRequest(@NonNull HttpInputMessage httpInputMessage) {
        return executeHttpRequest(httpInputMessage, null);
    }

    @Override
    public Mono<HttpOutputMessage> executeHttpRequest(@NonNull HttpInputMessage httpInputMessage, Duration timeoutDuration) {
        var httpMethod = new HttpMethod(httpInputMessage.httpMethod());
        return httpClient.request(httpMethod)
                .uri(httpInputMessage.uri())
                .sendForm(((httpClientRequest, httpClientForm) -> doSend(httpClientRequest, httpClientForm, httpInputMessage, timeoutDuration)))
                .responseSingle(this::doReceive);
    }

    @Override
    public <T> Mono<HttpOutputMessage> executeHttpRequest(HttpInputMessage httpInputMessage, ContentSerializer<T> contentSerializer, T content, Duration timeoutDuration) {
        var httpMethod = new HttpMethod(httpInputMessage.httpMethod());
        return httpClient.request(httpMethod)
                .uri(httpInputMessage.uri())
                .send(((httpClientRequest, nettyOutbound) -> doSend(httpClientRequest, nettyOutbound, httpInputMessage, contentSerializer, content, timeoutDuration)))
                .responseSingle(this::doReceive);
    }

    private Mono<HttpOutputMessage> doReceive(HttpClientResponse httpClientResponse, ByteBufMono byteBufMono){
        return byteBufMono.asByteArray()
                .map(responseBody -> doCreateOutputMessage(httpClientResponse, responseBody));
    }

    private HttpOutputMessage doCreateOutputMessage(HttpClientResponse httpClientResponse, byte[] responseBody){
        return new HttpResponseMessage(
                httpClientResponse.status().code(),
                NettyUtil.transformHttpHeaders(httpClientResponse.responseHeaders()),
                responseBody
        );
    }

    private void doSend(@NonNull HttpClientRequest httpClientRequest, @NonNull HttpClientForm httpClientForm, @NonNull HttpInputMessage httpInputMessage, Duration timeoutDuration){
        Optional.ofNullable(timeoutDuration).ifPresent(httpClientRequest::responseTimeout);
        httpInputMessage.httpHeaders()
                .forEach((httpHeader, httpHeaderValues) -> httpHeaderValues.forEach(httpHeaderValue -> httpClientRequest.header(httpHeader, httpHeaderValue)));
    }

    private <C> Publisher<Void> doSend(@NonNull HttpClientRequest httpClientRequest, NettyOutbound nettyOutbound, @NonNull HttpInputMessage httpInputMessage, ContentSerializer<C> contentSerializer, C content, Duration timeoutDuration){
        Optional.ofNullable(timeoutDuration).ifPresent(httpClientRequest::responseTimeout);
        validateContentType(httpInputMessage, contentSerializer);
        httpInputMessage.httpHeaders()
                .forEach((httpHeader, httpHeaderValues) -> httpHeaderValues.forEach(httpHeaderValue -> httpClientRequest.header(httpHeader, httpHeaderValue)));
        return nettyOutbound.send(NettyUtil.createContentPublisher(contentSerializer, content));
    }

    private void validateContentType(HttpInputMessage httpInputMessage, ContentSerializer<?> contentSerializer){
        var inputHttpHeaders = httpInputMessage.httpHeaders();
        var serializedContentType = contentSerializer.getContentType();
        if(inputHttpHeaders.containsKey("Content-Type")){
            var contentTypes = inputHttpHeaders.get("Content-Type");
            if(contentTypes.size() == 0){
                contentTypes.add(contentSerializer.getContentType());
            } else if (contentTypes.size() == 1) {
                var contentType = contentTypes.stream().findFirst().get();
                if(!contentType.equals(serializedContentType)){
                    throw new HttpRequestException("Expected content type: '%s' but got serializer for content type: '%s'".formatted(contentType, serializedContentType));
                }
            }else {
                throw new HttpRequestException("More than one content type specified");
            }
        }else {
            inputHttpHeaders.put("Content-Type", List.of(serializedContentType));
        }
    }
}
