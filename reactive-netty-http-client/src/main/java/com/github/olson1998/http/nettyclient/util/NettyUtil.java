package com.github.olson1998.http.nettyclient.util;

import com.github.olson1998.http.serialization.ContentSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.experimental.UtilityClass;
import org.apache.http.entity.ContentType;
import org.reactivestreams.Publisher;
import reactor.netty.ByteBufMono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.github.olson1998.http.client.util.HttpEntityUtil.transformToReadOnly;

@UtilityClass
public class NettyUtil {

    public static Map<String, List<String>> transformHttpHeaders(HttpHeaders httpHeaders){
        var readWriteHttpHeaders = new HashMap<String, List<String>>();
        httpHeaders.forEach(httpHeaderEntry ->{
            var httpHeader = httpHeaderEntry.getKey();
            var value = httpHeaderEntry.getValue();
            if(readWriteHttpHeaders.containsKey(httpHeader)){
                readWriteHttpHeaders.get(httpHeader).add(value);
            }else {
                var headerValues = new ArrayList<String>();
                headerValues.add(value);
                readWriteHttpHeaders.put(httpHeader, headerValues);
            }
        });
        return transformToReadOnly(readWriteHttpHeaders);
    }

    public static <C> Publisher<ByteBuf> createContentPublisher(ContentSerializer contentSerializer, ContentType contentType, C content){
        var serializationFuture = CompletableFuture.supplyAsync(()-> contentSerializer.serialize().apply(content, contentType))
                .thenApplyAsync(Unpooled::copiedBuffer);
        return ByteBufMono.fromFuture(serializationFuture);
    }

}
