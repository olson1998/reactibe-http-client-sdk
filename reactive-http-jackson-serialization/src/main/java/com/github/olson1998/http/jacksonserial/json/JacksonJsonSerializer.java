package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olson1998.http.jacksonserial.json.exception.ApplicationJsonSerializationException;
import com.github.olson1998.http.serialization.ContentSerializer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.function.Function;

public class JacksonJsonSerializer<C> extends AbstractJacksonJsonSerialization implements ContentSerializer<C> {

    public JacksonJsonSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public String getPrimaryContentType() {
        return "application/json";
    }

    @Override
    public Function<C, byte[]> serialize() {
        return this::doSerializeApplicationJson;
    }

    private byte[] doSerializeApplicationJson(C content){
        try{
            return objectMapper.writeValueAsBytes(content);
        }catch (IOException e){
            throw new ApplicationJsonSerializationException(e);
        }
    }

}
