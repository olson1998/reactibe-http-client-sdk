package com.github.olson1998.http.client.util;

import com.github.olson1998.http.client.exception.Base64ImageDecodeException;
import com.github.olson1998.http.contract.ImageRead;
import com.github.olson1998.http.util.ImageExtension;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@UtilityClass
public class Base64ImageUtil {

    public static ImageRead readBase64Image(String image){
        return Optional.ofNullable(image)
                .map(Base64ImageUtil::resolveExtension)
                .map(extension -> readBase64Image(extension, decodeBase64(image)))
                .orElse(null);
    }

    private static ImageRead readBase64Image(@NonNull ImageExtension imageExtension, @NonNull byte[] imageBytes){
        try(var input = new ByteArrayInputStream(imageBytes)){
            var memoryCache = new MemoryCacheImageInputStream(input);
            var image = ImageIO.read(memoryCache);
            return new ImageRead(imageExtension, image);
        }catch (IOException e){
            throw new Base64ImageDecodeException(e);
        }
    }

    private static ImageExtension resolveExtension(@NonNull String image){
        return ImageExtension.findImageExtension(image).orElseThrow();
    }

    private static byte[] decodeBase64(String base64){
        var decoder = Base64.getDecoder();
        return decoder.decode(base64);
    }
}
