package com.github.olson1998.http.imageserial;

import lombok.Getter;
import org.apache.http.entity.ContentType;

import java.util.Set;

import static org.apache.http.entity.ContentType.*;
import static org.apache.http.entity.ContentType.IMAGE_WEBP;

@Getter
abstract class AbstractImageSerialization {

    private final Set<ContentType> supportedContentTypes = Set.of(IMAGE_JPEG, IMAGE_PNG, IMAGE_GIF, IMAGE_WEBP);

}
