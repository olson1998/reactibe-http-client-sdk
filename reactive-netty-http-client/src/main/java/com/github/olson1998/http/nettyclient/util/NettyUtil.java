package com.github.olson1998.http.nettyclient.util;

import com.github.olson1998.http.ReadOnlyHttpHeader;
import com.github.olson1998.http.ReadOnlyHttpHeaders;
import com.github.olson1998.http.serialization.ContentSerializer;
import com.github.olson1998.http.serialization.context.SerializationContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import reactor.netty.ByteBufMono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NettyUtil {

    public static void appendHttpHeaders(HttpHeaders httpHeaders, Map<String, List<String>> httpHeadersMap) {
        httpHeadersMap.forEach((httpHeader, httpHeaderValues) -> httpHeaderValues.forEach(httpHeaderValue -> httpHeaders.add(httpHeader, httpHeaderValue)));
    }

    public static com.github.olson1998.http.HttpHeaders transformHttpHeaders(HttpHeaders httpHeaders) {
        var httpHeadersList = new ArrayList<ReadOnlyHttpHeader>();
        httpHeaders.forEach(httpHeaderEntry -> {
            httpHeadersList.add(new ReadOnlyHttpHeader(httpHeaderEntry.getKey(), httpHeaderEntry.getValue()));
        });
        return new ReadOnlyHttpHeaders(httpHeadersList);
    }

    public static <C> Publisher<ByteBuf> createContentPublisher(ContentSerializer contentSerializer, SerializationContext serializationContext, C content) {
        var serializationFuture = CompletableFuture.supplyAsync(() -> contentSerializer.serialize().apply(content, serializationContext))
                .thenApplyAsync(Unpooled::copiedBuffer);
        return ByteBufMono.fromFuture(serializationFuture);
    }

}
