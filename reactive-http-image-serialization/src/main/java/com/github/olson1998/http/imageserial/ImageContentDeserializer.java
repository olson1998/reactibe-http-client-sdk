package com.github.olson1998.http.imageserial;

import com.github.olson1998.http.imageserial.exception.ImageDeserializationException;
import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ResponseMapping;
import com.github.olson1998.http.serialization.context.SerializationContext;
import org.apache.http.entity.ContentType;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.function.BiFunction;

public class ImageContentDeserializer extends AbstractImageSerialization implements ContentDeserializer {

    @Override
    public <C> BiFunction<byte[], SerializationContext, C> deserialize(Class<C> deserializedPojoClass) {
        return (imageBytes, context) -> doDeserializeImage(imageBytes, context, deserializedPojoClass);
    }

    @Override
    public <C> BiFunction<byte[], SerializationContext, C> deserializeMapped(ResponseMapping<C> responseMapping) {
        return (imageBytes, context) -> doDeserializeImage(imageBytes, context, responseMapping.getPojoType());
    }

    private <C> C doDeserializeImage(byte[] imageBytes, SerializationContext serializationContext, Type type){
        if(type.equals(BufferedImage.class) || type.equals(RenderedImage.class)){
            return (C) doDeserialize(imageBytes, serializationContext);
        }else {
            throw new ImageDeserializationException(
                    "Cannot create instance of: '%s' from image".formatted(type.getTypeName()),
                    imageBytes,
                    serializationContext.getContentType()
            );
        }
    }

    private BufferedImage doDeserialize(byte[] imageBytes, SerializationContext serializationContext){
        var contentType = serializationContext.getContentType();
        try(var bytesInputStream = new ByteArrayInputStream(imageBytes)){
            var imageInputStream = new MemoryCacheImageInputStream(bytesInputStream);
            return ImageIO.read(imageInputStream);
        }catch (IOException e){
            throw new ImageDeserializationException("Failed to deserialize image", e, imageBytes, contentType);
        }
    }

}
