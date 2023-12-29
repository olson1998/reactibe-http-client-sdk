package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olson1998.http.jacksonserial.json.exception.ApplicationJsonSerializationException;
import com.github.olson1998.http.serialization.ContentSerializer;
import com.github.olson1998.http.serialization.context.SerializationContext;

import java.io.IOException;
import java.util.function.BiFunction;

public class JacksonJsonSerializer extends AbstractJacksonJsonSerialization implements ContentSerializer {

    public JacksonJsonSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public BiFunction<Object, SerializationContext, byte[]> serialize() {
        return this::doSerializeApplicationJson;
    }

    private byte[] doSerializeApplicationJson(Object content, SerializationContext serializationContext) {
        try {
            return objectMapper.writeValueAsBytes(content);
        } catch (IOException e) {
            throw new ApplicationJsonSerializationException(e, null, serializationContext.getContentType());
        }
    }
}
