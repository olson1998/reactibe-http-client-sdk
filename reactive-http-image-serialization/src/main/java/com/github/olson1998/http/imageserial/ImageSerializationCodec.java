package com.github.olson1998.http.imageserial;

import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ContentSerializer;
import com.github.olson1998.http.serialization.SerializationCodec;
import lombok.Getter;

@Getter
public class ImageSerializationCodec extends AbstractImageSerialization implements SerializationCodec {

    private final ContentSerializer contentSerializer = new ImageContentSerializer();

    private final ContentDeserializer contentDeserializer = new ImageContentDeserializer();
}
