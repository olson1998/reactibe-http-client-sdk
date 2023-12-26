package com.github.olson1998.http.serialization;

import com.github.olson1998.http.serialization.context.SerializationContext;
import org.apache.http.entity.ContentType;

import java.util.Set;
import java.util.function.BiFunction;

public interface ContentDeserializer {

    Set<ContentType> getSupportedContentTypes();

    <C> BiFunction<byte[], SerializationContext, C> deserialize(Class<C> deserializedPojoClass);

    <C> BiFunction<byte[], SerializationContext, C> deserializeMapped(ResponseMapping<C> responseMapping);

}
