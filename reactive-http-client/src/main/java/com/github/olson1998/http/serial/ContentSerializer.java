package com.github.olson1998.http.serial;

import java.util.function.Function;

public interface ContentSerializer<C> {

    String getContentType();

    Function<C, byte[]> serialize();
}
