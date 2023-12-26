package com.github.olson1998.http.serialization;

import com.github.olson1998.http.serialization.context.SerializationContext;
import org.apache.http.entity.ContentType;

import java.util.Set;
import java.util.function.BiFunction;

public interface ContentSerializer {

    Set<ContentType> getSupportedContentTypes();

    BiFunction<Object, SerializationContext, byte[]> serialize();
}
