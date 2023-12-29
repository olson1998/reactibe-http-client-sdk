package com.github.olson1998.http.contract;

import com.github.olson1998.http.util.ImageExtension;

import java.awt.image.BufferedImage;

public record ImageRead(ImageExtension extension, BufferedImage image) {
}
