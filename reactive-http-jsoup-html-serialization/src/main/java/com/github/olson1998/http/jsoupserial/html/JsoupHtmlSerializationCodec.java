package com.github.olson1998.http.jsoupserial.html;

import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ContentSerializer;
import com.github.olson1998.http.serialization.SerializationCodec;
import org.apache.http.entity.ContentType;

import java.util.Set;

public class JsoupHtmlSerializationCodec implements SerializationCodec {

    @Override
    public Set<ContentType> getSupportedContentTypes() {
        return Set.of(ContentType.TEXT_HTML);
    }

    @Override
    public ContentSerializer getContentSerializer() {
        return null;
    }

    @Override
    public ContentDeserializer getContentDeserializer() {
        return new JsoupHtmlDeserializer();
    }
}
