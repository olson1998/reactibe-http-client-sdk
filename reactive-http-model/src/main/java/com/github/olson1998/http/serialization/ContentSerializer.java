package com.github.olson1998.http.serialization;

import java.util.Set;
import java.util.function.Function;

public interface ContentSerializer<C> {

    String getPrimaryContentType();

    Set<String> getSupportedContentTypes();

    Function<C, byte[]> serialize();
}
