package com.github.olson1998.http.serialization;

import com.github.olson1998.http.serialization.context.DeserializationContext;
import org.apache.http.entity.ContentType;

import java.util.Set;
import java.util.function.BiFunction;

public interface ContentDeserializer {

    ContentType getPrimaryContentType();

    Set<ContentType> getSupportedContentTypes();

    <C> BiFunction<byte[], DeserializationContext, C> deserialize(Class<C> deserializedPojoClass);

    <C> BiFunction<byte[], DeserializationContext, C> deserializeMapped(ResponseMapping<C> responseMapping);

}
