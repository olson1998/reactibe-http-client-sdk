package com.github.olson1998.http.serialization;

import org.apache.http.entity.ContentType;

import java.util.Set;
import java.util.function.BiFunction;

public interface ContentDeserializer {

    ContentType getPrimaryContentType();

    Set<ContentType> getSupportedContentTypes();

    <C> BiFunction<byte[], ContentType, C> deserialize(Class<C> deserializedPojoClass);

    <C> BiFunction<byte[], ContentType, C> deserializeMapped(ResponseMapping<C> responseMapping);

}
