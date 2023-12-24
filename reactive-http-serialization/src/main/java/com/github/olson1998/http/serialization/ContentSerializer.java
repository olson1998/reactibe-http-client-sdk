package com.github.olson1998.http.serialization;

import org.apache.http.entity.ContentType;

import java.util.Set;
import java.util.function.BiFunction;

public interface ContentSerializer {

    ContentType getPrimaryContentType();

    Set<ContentType> getSupportedContentTypes();

    BiFunction<Object, ContentType, byte[]> serialize();
}
