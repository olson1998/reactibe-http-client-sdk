package com.github.olson1998.http.serial;

import java.util.function.Function;

public interface ContentDeserializer<C> {

    Function<byte[], C> deserialize();
}
