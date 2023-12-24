package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olson1998.http.jacksonserial.json.exception.ApplicationJsonSerializationException;
import com.github.olson1998.http.serialization.ContentSerializer;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.util.function.BiFunction;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class JacksonJsonSerializer extends AbstractJacksonJsonSerialization implements ContentSerializer {

    public JacksonJsonSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public ContentType getPrimaryContentType() {
        return APPLICATION_JSON;
    }

    @Override
    public BiFunction<Object, ContentType, byte[]> serialize() {
        return this::doSerializeApplicationJson;
    }

    private byte[] doSerializeApplicationJson(Object content, ContentType contentType){
        try{
            return objectMapper.writeValueAsBytes(content);
        }catch (IOException e){
            throw new ApplicationJsonSerializationException(e, null, contentType);
        }
    }
}
