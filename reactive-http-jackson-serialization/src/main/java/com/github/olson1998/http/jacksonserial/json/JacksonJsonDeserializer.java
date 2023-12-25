package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olson1998.http.jacksonserial.json.exception.ApplicationJsonDeserializationException;
import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ResponseMapping;
import com.github.olson1998.http.serialization.context.DeserializationContext;
import lombok.NonNull;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.function.BiFunction;

import static java.nio.charset.StandardCharsets.UTF_8;
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
    public <C> BiFunction<byte[], DeserializationContext, C> deserialize(Class<C> deserializedPojoClass) {
        return (bodyBytes, context) -> doDeserializeJson(bodyBytes, context, deserializedPojoClass);
    }

    @Override
    public <C> BiFunction<byte[], DeserializationContext, C> deserializeMapped(ResponseMapping<C> responseMapping) {
        return (bodyBytes, context) -> doDeserializeJson(bodyBytes, context, responseMapping);
    }

    private <C> C doDeserializeJson(byte[] jsonBytes, DeserializationContext deserializationContext, Class<C> mappedClass){
        return deserializationContext.findContentCharset()
                .map(charset -> doDeserializeJson(jsonBytes, charset, mappedClass))
                .orElseGet(()-> doDeserializeJson(jsonBytes, mappedClass));
    }

    private <C> C doDeserializeJson(byte[] jsonBytes, DeserializationContext deserializationContext, ResponseMapping<C> responseMapping){
        return deserializationContext.findContentCharset()
                .map(charset -> doDeserializeJson(jsonBytes, charset, responseMapping))
                .orElseGet(()-> doDeserializeJson(jsonBytes, responseMapping));
    }

    private <C> C doDeserializeJson(byte[] jsonBytes, Class<C> mappedClass){
        try{
            return objectMapper.readValue(jsonBytes, mappedClass);
        }catch (IOException e){
            throw new ApplicationJsonDeserializationException(e, jsonBytes, getPrimaryContentType());
        }
    }

    private <C> C doDeserializeJson(byte[] jsonBytes, ResponseMapping<C> responseMapping){
        try{
            var typeRef = createJacksonTypeRef(responseMapping);
            return objectMapper.readValue(jsonBytes, typeRef);
        }catch (IOException e){
            throw new ApplicationJsonDeserializationException(e, jsonBytes, getPrimaryContentType());
        }
    }

    private <C> C doDeserializeJson(byte[] jsonBytes, @NonNull Charset charset, Class<C> mappedClass){
        if(charset.equals(UTF_8)){
            return doDeserializeJson(jsonBytes, mappedClass);
        }else {
            try{
                var json = new String(jsonBytes, charset);
                return objectMapper.readValue(json, mappedClass);
            }catch (IOException e){
                throw new ApplicationJsonDeserializationException(e, jsonBytes, getPrimaryContentType());
            }
        }
    }

    private <C> C doDeserializeJson(byte[] jsonBytes, @NonNull Charset charset, ResponseMapping<C> responseMapping){
        if(charset.equals(UTF_8)){
            return doDeserializeJson(jsonBytes, responseMapping);
        }else {
            try{
                var json = new String(jsonBytes, charset);
                var typeRef = createJacksonTypeRef(responseMapping);
                return objectMapper.readValue(json, typeRef);
            }catch (IOException e){
                throw new ApplicationJsonDeserializationException(e, jsonBytes, getPrimaryContentType());
            }
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
