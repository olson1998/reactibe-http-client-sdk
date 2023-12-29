package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.olson1998.http.jacksonserial.json.exception.ApplicationJsonDeserializationException;
import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ResponseMapping;
import com.github.olson1998.http.serialization.context.SerializationContext;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.function.BiFunction;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JacksonJsonDeserializer extends AbstractJacksonJsonSerialization implements ContentDeserializer {

    public JacksonJsonDeserializer(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public <C> BiFunction<byte[], SerializationContext, C> deserialize(Class<C> deserializedPojoClass) {
        return (bodyBytes, context) -> doDeserializeJson(bodyBytes, deserializedPojoClass, context);
    }

    @Override
    public <C> BiFunction<byte[], SerializationContext, C> deserializeMapped(ResponseMapping<C> responseMapping) {
        return (bodyBytes, context) -> doDeserializeJson(bodyBytes, responseMapping, context);
    }

    private <C> C doDeserializeJson(byte[] jsonBytes, Class<C> mappedClass, SerializationContext serializationContext) {
        var optionalCharset = serializationContext.findContentCharset();
        if (optionalCharset.isEmpty()) {
            return doDeserialize(jsonBytes, mappedClass, serializationContext);
        } else {
            var charset = optionalCharset.get();
            if (charset.equals(UTF_8)) {
                return doDeserialize(jsonBytes, mappedClass, serializationContext);
            } else {
                return doDeserialize(jsonBytes, charset, mappedClass, serializationContext);
            }
        }
    }

    private <C> C doDeserializeJson(byte[] jsonBytes, ResponseMapping<C> responseMapping, SerializationContext serializationContext) {
        var optionalCharset = serializationContext.findContentCharset();
        if (optionalCharset.isEmpty()) {
            return doDeserialize(jsonBytes, responseMapping, serializationContext);
        } else {
            var charset = optionalCharset.get();
            if (charset.equals(UTF_8)) {
                return doDeserialize(jsonBytes, responseMapping, serializationContext);
            } else {
                return doDeserialize(jsonBytes, charset, responseMapping, serializationContext);
            }
        }
    }

    private <C> C doDeserialize(byte[] jsonBytes, Class<C> mappedClass, SerializationContext serializationContext) {
        try {
            return objectMapper.readValue(jsonBytes, mappedClass);
        } catch (IOException e) {
            throw new ApplicationJsonDeserializationException(e, jsonBytes, serializationContext.getContentType());
        }
    }

    private <C> C doDeserialize(byte[] jsonBytes, ResponseMapping<C> responseMapping, SerializationContext serializationContext) {
        try {
            var typeRef = createJacksonTypeRef(responseMapping);
            return objectMapper.readValue(jsonBytes, typeRef);
        } catch (IOException e) {
            throw new ApplicationJsonDeserializationException(e, jsonBytes, serializationContext.getContentType());
        }
    }

    private <C> C doDeserialize(byte[] jsonBytes, Charset charset, Class<C> mappedClass, SerializationContext serializationContext) {
        try {
            var json = new String(jsonBytes, charset);
            return objectMapper.readValue(json, mappedClass);
        } catch (IOException e) {
            throw new ApplicationJsonDeserializationException(e, jsonBytes, serializationContext.getContentType());
        }
    }

    private <C> C doDeserialize(byte[] jsonBytes, Charset charset, ResponseMapping<C> responseMapping, SerializationContext serializationContext) {
        try {
            var json = new String(jsonBytes, charset);
            var typeRef = createJacksonTypeRef(responseMapping);
            return objectMapper.readValue(json, typeRef);
        } catch (IOException e) {
            throw new ApplicationJsonDeserializationException(e, jsonBytes, serializationContext.getContentType());
        }
    }


    private <C> TypeReference<C> createJacksonTypeRef(ResponseMapping<C> responseMapping) {
        return new TypeReference<C>() {
            @Override
            public Type getType() {
                return responseMapping.getPojoType();
            }
        };
    }

}
