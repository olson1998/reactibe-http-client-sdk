package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olson1998.http.jacksonserial.json.exception.ApplicationJsonSerializationException;
import com.github.olson1998.http.serialization.ContentDeserializer;

import java.io.IOException;
import java.util.function.Function;

public class JacksonJsonDeserializer <C> extends AbstractJacksonJsonSerialization implements ContentDeserializer<C> {

    private Class<C> responseMappingClass;

    private TypeReference<C> responseMappingType;

    public JacksonJsonDeserializer(ObjectMapper objectMapper, Class<C> responseMappingClass) {
        super(objectMapper);
        this.responseMappingClass =responseMappingClass;
    }

    public JacksonJsonDeserializer(ObjectMapper objectMapper, TypeReference<C> responseMappingType) {
        super(objectMapper);
        this.responseMappingType = responseMappingType;
    }

    @Override
    public String getPrimaryContentType() {
        return getSupportedContentTypes().stream().findFirst().orElseThrow();
    }

    @Override
    public Function<byte[], C> deserialize() {
        return this::doDeserializeApplicationJson;
    }

    private C doDeserializeApplicationJson(byte[] jsonBytes){
        try{
            if(responseMappingClass != null){
                return objectMapper.readValue(jsonBytes, responseMappingClass);
            } else if (responseMappingType != null) {
                return objectMapper.readValue(jsonBytes, responseMappingType);
            }else {
                throw new IOException("Unknown JSON mapping type");
            }
        }catch (IOException e){
            throw new ApplicationJsonSerializationException(e);
        }
    }
}
