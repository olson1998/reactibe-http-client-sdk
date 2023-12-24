package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olson1998.http.jacksonserial.AbstractJsonSerialization;
import lombok.Getter;

import java.util.Set;

@Getter
abstract class AbstractJacksonJsonSerialization extends AbstractJsonSerialization {

    private final Set<String> supportedContentTypes = Set.of("application/json");

    protected AbstractJacksonJsonSerialization(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
