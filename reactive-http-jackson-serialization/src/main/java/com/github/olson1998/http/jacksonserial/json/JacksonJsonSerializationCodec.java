package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ContentSerializer;
import com.github.olson1998.http.serialization.SerializationCodec;

public class JacksonJsonSerializationCodec extends AbstractJacksonJsonSerialization implements SerializationCodec {

    private final JacksonJsonSerializer jacksonJsonSerializer;

    private final JacksonJsonDeserializer jacksonJsonDeserializer;

    public JacksonJsonSerializationCodec(ObjectMapper objectMapper) {
        super(objectMapper);
        this.jacksonJsonSerializer = new JacksonJsonSerializer(objectMapper);
        this.jacksonJsonDeserializer = new JacksonJsonDeserializer(objectMapper);
    }

    @Override
    public ContentSerializer getContentSerializer() {
        return jacksonJsonSerializer;
    }

    @Override
    public ContentDeserializer getContentDeserializer() {
        return jacksonJsonDeserializer;
    }
}
