package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olson1998.http.jacksonserial.AbstractJsonSerialization;
import lombok.Getter;
import org.apache.http.entity.ContentType;

import java.util.Set;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@Getter
abstract class AbstractJacksonJsonSerialization extends AbstractJsonSerialization {

    private final Set<ContentType> supportedContentTypes = Set.of(APPLICATION_JSON);

    protected AbstractJacksonJsonSerialization(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
