package com.github.olson1998.http.serialization;

import org.apache.http.entity.ContentType;

import java.util.Set;

public interface SerializationCodec {

    Set<ContentType> getSupportedContentTypes();

    ContentSerializer getContentSerializer();

    ContentDeserializer getContentDeserializer();
}
