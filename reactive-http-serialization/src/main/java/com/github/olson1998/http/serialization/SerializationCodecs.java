package com.github.olson1998.http.serialization;

import com.github.olson1998.http.serialization.exception.NoCodecRegisteredException;
import lombok.NonNull;
import org.apache.http.entity.ContentType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class SerializationCodecs implements Iterable<SerializationCodec> {

    private final List<SerializationCodec> serializationCodecs = new ArrayList<>();

    @Override
    public Iterator<SerializationCodec> iterator() {
        return serializationCodecs.listIterator();
    }

    public ContentSerializer getContentSerializer(@NonNull ContentType contentType){
        return serializationCodecs.stream()
                .filter(serializationCodec -> isSupportingSerialization(serializationCodec, contentType))
                .map(SerializationCodec::getContentSerializer)
                .findFirst()
                .orElseThrow(()-> new NoCodecRegisteredException(contentType));
    }

    public ContentDeserializer getContentDeserializer(@NonNull ContentType contentType){
        return serializationCodecs.stream()
                .filter(serializationCodec -> isSupportingSerialization(serializationCodec, contentType))
                .map(SerializationCodec::getContentDeserializer)
                .findFirst()
                .orElseThrow(()-> new NoCodecRegisteredException(contentType));
    }

    public void registerCodec(SerializationCodec serializationCodec){
        serializationCodecs.add(serializationCodec);
    }

    public void unregisterCodec(SerializationCodec serializationCodec){
        serializationCodecs.remove(serializationCodec);
    }

    private boolean isSupportingSerialization(SerializationCodec serializationCodec, ContentType contentType){
        return serializationCodec.getSupportedContentTypes().stream()
                .anyMatch(supportedContentType -> isSameMimeType(supportedContentType, contentType));
    }

    private boolean isSameMimeType(ContentType thatType, ContentType thisType){
        var thatMimeType = Optional.ofNullable(thatType)
                .map(ContentType::getMimeType)
                .orElse(null);
        var thisMimeType = Optional.ofNullable(thisType)
                .map(ContentType::getMimeType)
                .orElse(null);
        if(thatMimeType == null || thisMimeType == null){
            return false;
        }else {
            return thatMimeType.equals(thisMimeType);
        }
    }

}
