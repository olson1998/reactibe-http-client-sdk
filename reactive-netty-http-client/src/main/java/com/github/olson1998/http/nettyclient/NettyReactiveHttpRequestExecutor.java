package com.github.olson1998.http.nettyclient;

import com.github.olson1998.http.client.ReactiveHttpRequestExecutor;
import com.github.olson1998.http.client.util.HttpUtil;
import com.github.olson1998.http.contract.ClientHttpResponse;
import com.github.olson1998.http.contract.WebRequest;
import com.github.olson1998.http.contract.WebResponse;
import com.github.olson1998.http.exception.ContentDeserializationException;
import com.github.olson1998.http.exception.HttpResponseException;
import com.github.olson1998.http.nettyclient.util.NettyUtil;
import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ResponseMapping;
import com.github.olson1998.http.serialization.SerializationCodecs;
import io.netty.handler.codec.http.HttpMethod;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientForm;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;

import java.util.*;
import java.util.function.Consumer;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

@RequiredArgsConstructor
public class NettyReactiveHttpRequestExecutor implements ReactiveHttpRequestExecutor {

    private final HttpClient httpClient;

    private final SerializationCodecs serializationCodecs;

    private final Map<String, List<String>> defaultHttpHeaders = new HashMap<>();

    public NettyReactiveHttpRequestExecutor(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.serializationCodecs = new SerializationCodecs();
    }

    @Override
    public Mono<WebResponse<byte[]>> sendHttpRequest(WebRequest webRequest) {
        return executeHttpRequest(webRequest);
    }

    @Override
    public <T> Mono<WebResponse<T>> sendHttpRequest(WebRequest webRequest, Class<T> responseMapping) {
        return executeHttpRequest(webRequest, new ResponseMapping<>() {
        });
    }

    @Override
    public <T> Mono<WebResponse<T>> sendHttpRequest(WebRequest webRequest, ResponseMapping<T> responseMapping) {
        return executeHttpRequest(webRequest, responseMapping);
    }

    @Override
    public void addHttpHeader(String httpHeader, String httpHeaderValue) {
        HttpUtil.appendHttpHeader(defaultHttpHeaders, httpHeader, httpHeaderValue);
    }

    @Override
    public void addHttpHeaders(@NonNull String httpHeader,@NonNull Iterable<String> httpHeaderValues) {
        HttpUtil.appendHttpHeaders(defaultHttpHeaders, httpHeader, httpHeaderValues);
    }

    @Override
    public void removeHttpHeader(String httpHeader) {
        defaultHttpHeaders.remove(httpHeader);
    }

    @Override
    public void removeHttpHeader(String httpHeader, Collection<String> httpHeaderValues) {
        HttpUtil.removeHttpHeaders(defaultHttpHeaders, httpHeader, httpHeaderValues);
    }

    @Override
    public void serializationCodecs(@NonNull Consumer<SerializationCodecs> serializationCodecsConsumer) {
        serializationCodecsConsumer.accept(serializationCodecs);
    }

    private <V> Mono<WebResponse<V>> executeHttpRequest(WebRequest webRequest, ResponseMapping<V> responseMapping){
        return webRequest.findContentType()
                .map(contentType -> executeHttpRequestWithContent(webRequest, contentType, responseMapping))
                .orElseGet(()-> executeHttpRequestWithNoContent(webRequest, responseMapping));
    }

    private Mono<WebResponse<byte[]>> executeHttpRequest(WebRequest webRequest){
        return webRequest.findContentType()
                .map(contentType -> executeHttpRequestWithContent(webRequest, contentType))
                .orElseGet(()-> executeHttpRequestWithNoContent(webRequest));
    }

    private Mono<WebResponse<byte[]>> executeHttpRequestWithNoContent(WebRequest webRequest){
        var httpMethod = new HttpMethod(webRequest.httpMethod());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .sendForm(((httpClientRequest, httpClientForm) -> doSend(httpClientRequest, httpClientForm, webRequest)))
                .responseSingle((this::doReceive));
    }

    private Mono<WebResponse<byte[]>> executeHttpRequestWithContent(WebRequest webRequest, ContentType contentType){
        var httpMethod = new HttpMethod(webRequest.httpMethod());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .send(((httpClientRequest, nettyOutbound) -> doSend(httpClientRequest, nettyOutbound, webRequest, contentType)))
                .responseSingle((this::doReceive));
    }

    private <V> Mono<WebResponse<V>> executeHttpRequestWithNoContent(WebRequest webRequest, ResponseMapping<V> responseMapping){
        var httpMethod = new HttpMethod(webRequest.httpMethod());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .sendForm(((httpClientRequest, httpClientForm) -> doSend(httpClientRequest, httpClientForm, webRequest)))
                .responseSingle(((httpClientResponse, byteBufMono) -> doReceive(httpClientResponse, byteBufMono, responseMapping)));
    }

    private <V> Mono<WebResponse<V>> executeHttpRequestWithContent(WebRequest webRequest, ContentType contentType, ResponseMapping<V> responseMapping){
        var httpMethod = new HttpMethod(webRequest.httpMethod());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .send(((httpClientRequest, nettyOutbound) -> doSend(httpClientRequest, nettyOutbound, webRequest, contentType)))
                .responseSingle(((httpClientResponse, byteBufMono) -> doReceive(httpClientResponse, byteBufMono, responseMapping)));
    }

    private void doSend(@NonNull HttpClientRequest httpClientRequest, @NonNull HttpClientForm httpClientForm, @NonNull WebRequest webRequest){
        Optional.ofNullable(webRequest.timeoutDuration()).ifPresent(httpClientRequest::responseTimeout);
        webRequest.httpHeaders()
                .forEach((httpHeader, httpHeaderValues) -> httpHeaderValues.forEach(httpHeaderValue -> httpClientRequest.addHeader(httpHeader, httpHeaderValue)));
    }

    private Publisher<Void> doSend(@NonNull HttpClientRequest httpClientRequest, NettyOutbound nettyOutbound, @NonNull WebRequest webRequest, ContentType contentType){
        Optional.ofNullable(webRequest.timeoutDuration()).ifPresent(httpClientRequest::responseTimeout);
        var contentSerializer = serializationCodecs.getContentSerializer(contentType);
        defaultHttpHeaders.forEach((httpHeader, httpHeaderValues) -> httpHeaderValues.forEach(httpHeaderValue -> httpClientRequest.addHeader(httpHeader, httpHeaderValue)));
        webRequest.httpHeaders()
                .forEach((httpHeader, httpHeaderValues) -> httpHeaderValues.forEach(httpHeaderValue -> httpClientRequest.addHeader(httpHeader, httpHeaderValue)));
        return nettyOutbound.send(NettyUtil.createContentPublisher(contentSerializer, contentType, webRequest.body()));
    }

    private Mono<WebResponse<Void>> doReceive(HttpClientResponse httpClientResponse){
        return Mono.just(doCreateWebResponse(httpClientResponse, null));
    }

    private Mono<WebResponse<byte[]>> doReceive(HttpClientResponse httpClientResponse, ByteBufMono byteBufMono){
        return byteBufMono.asByteArray()
                .map(responseBody -> doCreateWebResponse(httpClientResponse, responseBody));
    }

    private <T> Mono<WebResponse<T>> doReceive(HttpClientResponse httpClientResponse, ByteBufMono byteBufMono, ResponseMapping<T> responseMapping){
        var contentTypeValue = httpClientResponse.responseHeaders().get(CONTENT_TYPE);
        var contentType = ContentType.parse(contentTypeValue);
        var contentDeserializer = serializationCodecs.getContentDeserializer(contentType);
        return byteBufMono.asByteArray()
                .map(bodyBytes -> doDeserializeResponseBody(httpClientResponse, bodyBytes, contentType, contentDeserializer, responseMapping))
                .map(responseBody -> doCreateWebResponse(httpClientResponse, responseBody));
    }

    private <T> WebResponse<T> doCreateWebResponse(HttpClientResponse httpClientResponse, T responseBody){
        return new ClientHttpResponse<>(
                httpClientResponse.status().code(),
                NettyUtil.transformHttpHeaders(httpClientResponse.responseHeaders()),
                responseBody
        );
    }

    private <T> T doDeserializeResponseBody(HttpClientResponse httpClientResponse, byte[] bodyBytes, ContentType contentType, ContentDeserializer contentDeserializer, ResponseMapping<T> responseMapping){
        var contentDeserialization = contentDeserializer.deserializeMapped(responseMapping);
        try{
            return contentDeserialization.apply(bodyBytes, contentType);
        }catch (ContentDeserializationException e){
            var statusCode = httpClientResponse.status().code();
            var httpHeaders = NettyUtil.transformHttpHeaders(httpClientResponse.responseHeaders());
            throw new HttpResponseException("Failed to deserialize http response",e, statusCode, httpHeaders);
        }
    }



}
