package com.github.olson1998.http.serialization;

import java.util.Set;
import java.util.function.Function;

public interface ContentDeserializer<C> {

    String getPrimaryContentType();

    Set<String> getSupportedContentTypes();

    Function<byte[], C> deserialize();
}
