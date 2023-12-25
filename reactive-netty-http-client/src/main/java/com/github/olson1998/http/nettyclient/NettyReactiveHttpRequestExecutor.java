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
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Map.entry;
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
        var httpMethod = new HttpMethod(webRequest.httpMethod().name());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .send(((httpClientRequest, nettyOutbound) -> doSend(httpClientRequest, nettyOutbound, webRequest)))
                .responseSingle((this::doReceive));
    }

    @Override
    public <T> Mono<WebResponse<T>> sendHttpRequest(WebRequest webRequest, Class<T> responseMapping) {
        var httpMethod = new HttpMethod(webRequest.httpMethod().name());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .send(((httpClientRequest, nettyOutbound) -> doSend(httpClientRequest, nettyOutbound, webRequest)))
                .responseSingle(((httpClientResponse, byteBufMono) -> doReceive(httpClientResponse, byteBufMono, responseMapping)));
    }

    @Override
    public <T> Mono<WebResponse<T>> sendHttpRequest(WebRequest webRequest, ResponseMapping<T> responseMapping) {
        var httpMethod = new HttpMethod(webRequest.httpMethod().name());
        return httpClient.request(httpMethod)
                .uri(webRequest.uri())
                .send(((httpClientRequest, nettyOutbound) -> doSend(httpClientRequest, nettyOutbound, webRequest)))
                .responseSingle(((httpClientResponse, byteBufMono) -> doReceive(httpClientResponse, byteBufMono, responseMapping)));
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

    private Publisher<Void> doSend(@NonNull HttpClientRequest httpClientRequest, NettyOutbound nettyOutbound, @NonNull WebRequest webRequest){
        Optional.ofNullable(webRequest.timeoutDuration()).ifPresent(httpClientRequest::responseTimeout);
        defaultHttpHeaders
                .forEach((httpHeader, httpHeaderValues) -> httpHeaderValues.forEach(httpHeaderValue -> httpClientRequest.addHeader(httpHeader, httpHeaderValue)));
        webRequest.httpHeaders()
                .forEach((httpHeader, httpHeaderValues) -> httpHeaderValues.forEach(httpHeaderValue -> httpClientRequest.addHeader(httpHeader, httpHeaderValue)));
        return webRequest.findContentType()
                .map(contentType -> doSend(contentType, nettyOutbound, webRequest))
                .orElseGet(()-> doSend(nettyOutbound));
    }

    private Publisher<Void> doSend(@NonNull ContentType contentType,@NonNull  NettyOutbound nettyOutbound,@NonNull  WebRequest webRequest){
        var contentSerializer = serializationCodecs.getContentSerializer(contentType);
        return nettyOutbound.send(NettyUtil.createContentPublisher(contentSerializer, contentType, webRequest.body()));
    }

    private Publisher<Void> doSend(@NonNull NettyOutbound nettyOutbound){
        return nettyOutbound.send(Mono.empty());
    }

    private Mono<WebResponse<byte[]>> doReceive(HttpClientResponse httpClientResponse, ByteBufMono byteBufMono){
        return byteBufMono.asByteArray()
                .map(responseBody -> doCreateWebResponse(httpClientResponse, responseBody));
    }

    private <T> Mono<WebResponse<T>> doReceive(HttpClientResponse httpClientResponse, ByteBufMono byteBufMono, ResponseMapping<T> responseMapping){
        var deserializer = getContentDeserializer(httpClientResponse);
        return byteBufMono.asByteArray()
                .map(bodyBytes -> doDeserializeResponseBody(httpClientResponse, bodyBytes, deserializer.getKey(), deserializer.getValue(), responseMapping))
                .map(responseBody -> doCreateWebResponse(httpClientResponse, responseBody));
    }

    private <T> Mono<WebResponse<T>> doReceive(HttpClientResponse httpClientResponse, ByteBufMono byteBufMono, Class<T> responseMapping){
        var deserializer = getContentDeserializer(httpClientResponse);
        return byteBufMono.asByteArray()
                .map(bodyBytes -> doDeserializeResponseBody(httpClientResponse, bodyBytes, deserializer.getKey(), deserializer.getValue(), responseMapping))
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
            throw new HttpResponseException("Failed to deserialize http response body content of type: %s using: %s".formatted(contentType, contentDeserializer),e, statusCode, httpHeaders);
        }
    }

    private <T> T doDeserializeResponseBody(HttpClientResponse httpClientResponse, byte[] bodyBytes, ContentType contentType, ContentDeserializer contentDeserializer, Class<T> responseMapping){
        var contentDeserialization = contentDeserializer.deserialize(responseMapping);
        try{
            return contentDeserialization.apply(bodyBytes, contentType);
        }catch (ContentDeserializationException e){
            var statusCode = httpClientResponse.status().code();
            var httpHeaders = NettyUtil.transformHttpHeaders(httpClientResponse.responseHeaders());
            throw new HttpResponseException("Failed to deserialize http response body content of type: %s using: %s".formatted(contentType, contentDeserializer),e, statusCode, httpHeaders);
        }
    }

    private Map.Entry<ContentType, ContentDeserializer> getContentDeserializer(HttpClientResponse httpClientResponse){
        var contentTypeValue = httpClientResponse.responseHeaders().get(CONTENT_TYPE);
        var contentType = ContentType.parse(contentTypeValue);
        return entry(contentType, serializationCodecs.getContentDeserializer(contentType));
    }

}
