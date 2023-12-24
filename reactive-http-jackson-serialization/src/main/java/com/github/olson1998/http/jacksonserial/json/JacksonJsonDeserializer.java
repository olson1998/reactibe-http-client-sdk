package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olson1998.http.jacksonserial.json.exception.ApplicationJsonDeserializationException;
import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ResponseMapping;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.function.BiFunction;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class JacksonJsonDeserializer extends AbstractJacksonJsonSerialization implements ContentDeserializer {

    public JacksonJsonDeserializer(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public ContentType getPrimaryContentType() {
        return APPLICATION_JSON;
    }

    @Override
    public <C> BiFunction<byte[], ContentType, C> deserialize(Class<C> deserializedPojoClass) {
        return (bodyBytes, contentType) -> doDeserializeApplicationJson(bodyBytes, contentType, deserializedPojoClass);
    }

    @Override
    public <C> BiFunction<byte[], ContentType, C> deserializeMapped(ResponseMapping<C> responseMapping) {
        return (bodyBytes, contentType) -> doDeserializeApplicationJson(bodyBytes, contentType, responseMapping);
    }


    private <C> C doDeserializeApplicationJson(byte[] jsonBytes, ContentType contentType, Class<C> mappedClass){
        try{
            return objectMapper.readValue(jsonBytes, mappedClass);
        }catch (IOException e){
            throw new ApplicationJsonDeserializationException(jsonBytes, getPrimaryContentType());
        }
    }

    private <C> C doDeserializeApplicationJson(byte[] jsonBytes, ContentType contentType, ResponseMapping<C> responseMapping){
        try{
            var typeRef = createJacksonTypeRef(responseMapping);
            return objectMapper.readValue(jsonBytes, typeRef);
        }catch (IOException e){
            throw new ApplicationJsonDeserializationException(jsonBytes, getPrimaryContentType());
        }
    }

    private<C>  TypeReference<C> createJacksonTypeRef(ResponseMapping<C> responseMapping){
        return new TypeReference<C>() {
            @Override
            public Type getType() {
                return responseMapping.getPojoType();
            }
        };
    }

}
