package com.github.olson1998.http.nettyclient;

import com.github.olson1998.http.client.ReactiveHttpRequestExecutor;
import com.github.olson1998.http.contract.ClientHttpResponse;
import com.github.olson1998.http.contract.WebRequest;
import com.github.olson1998.http.contract.WebResponse;
import com.github.olson1998.http.exception.HttpRequestException;
import com.github.olson1998.http.nettyclient.util.NettyUtil;
import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ContentSerializer;
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

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class NettyReactiveHttpRequestExecutor implements ReactiveHttpRequestExecutor {

    private final HttpClient httpClient;

    @Override
    public <C> Mono<WebResponse<C>> executeHttpRequest(WebRequest webRequest, ContentDeserializer<C> contentDeserializer) {
        var httpMethod = new HttpMethod(webRequest.httpMethod());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .sendForm(((httpClientRequest, httpClientForm) -> doSend(httpClientRequest, httpClientForm, webRequest)))
                .responseSingle(((httpClientResponse, byteBufMono) -> doReceive(httpClientResponse, byteBufMono, contentDeserializer)));
    }

    @Override
    public <T, C> Mono<WebResponse<C>> executeHttpRequest(WebRequest webRequest, T content, ContentSerializer<T> contentSerializer, ContentDeserializer<C> contentDeserializer) {
        var httpMethod = new HttpMethod(webRequest.httpMethod());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .send(((httpClientRequest, nettyOutbound) -> doSend(httpClientRequest, nettyOutbound, webRequest, content, contentSerializer)))
                .responseSingle(((httpClientResponse, byteBufMono) -> doReceive(httpClientResponse, byteBufMono, contentDeserializer)));
    }

    @Override
    public Mono<WebResponse<byte[]>> executeHttpRequestForResponseBodyBytes(WebRequest webRequest) {
        var httpMethod = new HttpMethod(webRequest.httpMethod());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .sendForm(((httpClientRequest, httpClientForm) -> doSend(httpClientRequest, httpClientForm, webRequest)))
                .responseSingle(this::doReceive);
    }

    @Override
    public <T> Mono<WebResponse<byte[]>> executeHttpRequestForResponseBodyBytes(WebRequest webRequest, T content, ContentSerializer<T> contentSerializer) {
        var httpMethod = new HttpMethod(webRequest.httpMethod());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .send(((httpClientRequest, nettyOutbound) -> doSend(httpClientRequest, nettyOutbound, webRequest, content, contentSerializer)))
                .responseSingle(this::doReceive);
    }

    @Override
    public Mono<WebResponse<Void>> executeHttpRequestForNoResponseBody(WebRequest webRequest) {
        var httpMethod = new HttpMethod(webRequest.httpMethod());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .sendForm(((httpClientRequest, httpClientForm) -> doSend(httpClientRequest, httpClientForm, webRequest)))
                .responseSingle(((httpClientResponse, byteBufMono) -> doReceive(httpClientResponse)));
    }

    @Override
    public <T> Mono<WebResponse<Void>> executeHttpRequestForNoResponseBody(WebRequest webRequest, T content, ContentSerializer<T> contentSerializer) {
        var httpMethod = new HttpMethod(webRequest.httpMethod());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .send(((httpClientRequest, nettyOutbound) -> doSend(httpClientRequest, nettyOutbound, webRequest, content, contentSerializer)))
                .responseSingle(((httpClientResponse, byteBufMono) -> doReceive(httpClientResponse)));
    }

    private void doSend(@NonNull HttpClientRequest httpClientRequest, @NonNull HttpClientForm httpClientForm, @NonNull WebRequest webRequest){
        Optional.ofNullable(webRequest.timeoutDuration()).ifPresent(httpClientRequest::responseTimeout);
        webRequest.httpHeaders()
                .forEach((httpHeader, httpHeaderValues) -> httpHeaderValues.forEach(httpHeaderValue -> httpClientRequest.addHeader(httpHeader, httpHeaderValue)));
    }

    private <C> Publisher<Void> doSend(@NonNull HttpClientRequest httpClientRequest, NettyOutbound nettyOutbound, @NonNull WebRequest webRequest, C content, ContentSerializer<C> contentSerializer){
        Optional.ofNullable(webRequest.timeoutDuration()).ifPresent(httpClientRequest::responseTimeout);
        validateContentType(webRequest, contentSerializer);
        webRequest.httpHeaders()
                .forEach((httpHeader, httpHeaderValues) -> httpHeaderValues.forEach(httpHeaderValue -> httpClientRequest.addHeader(httpHeader, httpHeaderValue)));
        return nettyOutbound.send(NettyUtil.createContentPublisher(contentSerializer, content));
    }

    private Mono<WebResponse<Void>> doReceive(HttpClientResponse httpClientResponse){
        return Mono.just(doCreateWebResponse(httpClientResponse, null));
    }

    private Mono<WebResponse<byte[]>> doReceive(HttpClientResponse httpClientResponse, ByteBufMono byteBufMono){
        return byteBufMono.asByteArray()
                .map(responseBody -> doCreateWebResponse(httpClientResponse, responseBody));
    }

    private <T> Mono<WebResponse<T>> doReceive(HttpClientResponse httpClientResponse, ByteBufMono byteBufMono, ContentDeserializer<T> contentDeserializer){
        return byteBufMono.asByteArray()
                .map(contentDeserializer.deserialize())
                .map(responseBody -> doCreateWebResponse(httpClientResponse, responseBody));
    }

    private <T> WebResponse<T> doCreateWebResponse(HttpClientResponse httpClientResponse, T responseBody){
        return new ClientHttpResponse<>(
                httpClientResponse.status().code(),
                NettyUtil.transformHttpHeaders(httpClientResponse.responseHeaders()),
                responseBody
        );
    }

    private void validateContentType(WebRequest webRequest, ContentSerializer<?> contentSerializer){
        var inputHttpHeaders = webRequest.httpHeaders();
        var serializedContentTypes = contentSerializer.getSupportedContentTypes();
        if(inputHttpHeaders.containsKey("Content-Type")){
            var contentTypes = inputHttpHeaders.get("Content-Type");
            if(contentTypes.size() == 0){
                contentTypes.add(contentSerializer.getPrimaryContentType());
            } else if (contentTypes.size() == 1) {
                var contentType = contentTypes.get(0);
                if(!serializedContentTypes.contains(contentType)){
                    throw new HttpRequestException("Expected content type: '%s' but got serializer for content types: %s".formatted(contentType, serializedContentTypes));
                }
            }else {
                throw new HttpRequestException("More than one content type specified");
            }
        }else {
            inputHttpHeaders.put("Content-Type", List.of(contentSerializer.getPrimaryContentType()));
        }
    }

}
