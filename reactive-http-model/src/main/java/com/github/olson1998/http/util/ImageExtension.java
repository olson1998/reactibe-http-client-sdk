package com.github.olson1998.http.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ImageExtension {

    JPG('/', "jpg", ".jpg"),
    PNG('i', "png", ".png"),
    GIF('R', "gif", ".gif"),
    WEBP('U', "webp", ".webp");

    private final char firstChar;

    private final String type;

    private final String extension;

    public static Optional<ImageExtension> findByFistChar(char character){
        return Arrays.stream(ImageExtension.values())
                .filter(imageExtension -> imageExtension.firstChar == character)
                .findFirst();
    }

    public static Optional<ImageExtension> findByExtension(String extension){
        return Arrays.stream(ImageExtension.values())
                .filter(imageExtension -> imageExtension.extension.equals(extension))
                .findFirst();
    }

    public static Optional<ImageExtension> findImageExtension(String image){
        if(image == null || image.length() < 1){
            return Optional.empty();
        }else {
            return findByFistChar(image.charAt(0));
        }
    }
}
