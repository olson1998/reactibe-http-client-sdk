package com.github.olson1998.http.serialization.context;

import com.github.olson1998.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.nio.charset.Charset;
import java.util.Optional;

public interface SerializationContext {

    ContentType getContentType();

    HttpHeaders getHttpHeaders();

    Optional<Charset> findContentCharset();
}
