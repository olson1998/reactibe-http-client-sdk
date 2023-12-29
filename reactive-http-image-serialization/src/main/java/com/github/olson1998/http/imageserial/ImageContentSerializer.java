package com.github.olson1998.http.imageserial;

import com.github.olson1998.http.imageserial.exception.ImageSerializationException;
import com.github.olson1998.http.serialization.ContentSerializer;
import com.github.olson1998.http.serialization.context.SerializationContext;
import org.apache.http.entity.ContentType;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;

public class ImageContentSerializer extends AbstractImageSerialization implements ContentSerializer {

    @Override
    public BiFunction<Object, SerializationContext, byte[]> serialize() {
        return null;
    }

    private byte[] doSerializeImage(Object imageObject, SerializationContext serializationContext) {
        if (imageObject instanceof byte[] bytes) {
            return bytes;
        } else if (imageObject instanceof RenderedImage image) {
            return doSerialize(image, serializationContext);
        } else {
            throw new ImageSerializationException(
                    "Cannot serialize: '%s' into image bytes".formatted(imageObject),
                    imageObject,
                    serializationContext.getContentType()
            );
        }
    }

    private byte[] doSerialize(RenderedImage image, SerializationContext serializationContext) {
        ImageWriter imageWriter = null;
        ImageOutputStream imageOutput = null;
        var contentType = serializationContext.getContentType();
        try (var outputStream = new ByteArrayOutputStream()) {
            imageWriter = selectImageWriter(contentType);
            imageOutput = ImageIO.createImageOutputStream(outputStream);
            imageWriter.setOutput(imageOutput);
            imageWriter.write(image);
            imageWriter.dispose();
            imageOutput.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            Optional.ofNullable(imageWriter).ifPresent(ImageWriter::dispose);
            Optional.ofNullable(imageOutput).ifPresent(imageOutputStream -> {
                try {
                    imageOutputStream.flush();
                } catch (IOException ioException) {
                    e.addSuppressed(ioException);
                }
            });
            throw new ImageSerializationException("Failed to serialize image", e, image, serializationContext.getContentType());
        }
    }

    private ImageWriter selectImageWriter(ContentType contentType) throws IOException {
        var imageWriters = ImageIO.getImageWritersByMIMEType(contentType.getMimeType());
        if (imageWriters.hasNext()) {
            return imageWriters.next();
        } else {
            throw new IOException("No Image writer registered for content type: %s".formatted(contentType));
        }
    }
}
