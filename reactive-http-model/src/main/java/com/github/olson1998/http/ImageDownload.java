package com.github.olson1998.http;

import com.github.olson1998.http.contract.WebResponse;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Optional;

public record ImageDownload(@NonNull URI uri, WebResponse<BufferedImage> response) {

    public boolean isResponseReceived(){
        return response != null;
    }

    public Optional<String> resolveFileName(){
        var path = uri.getPath();
        var fileName = StringUtils.substringAfterLast(path, "/");
        if(!StringUtils.isBlank(fileName)){
            return Optional.of(fileName);
        }else {
            return Optional.empty();
        }
    }
}
