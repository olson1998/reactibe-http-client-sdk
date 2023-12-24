package com.github.olson1998.http.jacksonserial;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJsonSerialization {

    protected final ObjectMapper objectMapper;
}
