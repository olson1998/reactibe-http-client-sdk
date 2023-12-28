package com.github.olson1998.http.contract;

import com.github.olson1998.http.util.ImageExtension;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Optional;

@Getter
@ToString
@RequiredArgsConstructor
public class ImageDownload {

    private final URI uri;

    private final String fileName;

    private final ImageRead imageRead;

    public ImageDownload(URI uri, BufferedImage bufferedImage) {
        this.uri = uri;
        this.fileName = resolveFileName(uri).orElse(null);
        this.imageRead = Optional.ofNullable(fileName)
                .map(this::resolveImageExtension)
                .map(extension -> new ImageRead(extension, bufferedImage))
                .orElseGet(()-> new ImageRead(null, bufferedImage));
    }

    private Optional<String> resolveFileName(URI uri){
        if(uri == null){
            return Optional.empty();
        }
        var path = uri.getPath();
        var fileName = StringUtils.substringAfterLast(path, "/");
        if(!StringUtils.isBlank(fileName)){
            return Optional.of(fileName);
        }else {
            return Optional.empty();
        }
    }

    private ImageExtension resolveImageExtension(String fileName){
        var extensionValue = StringUtils.substringAfterLast(fileName, ".");
        return ImageExtension.findByExtension('.' + extensionValue)
                .orElse(null);
    }

}
