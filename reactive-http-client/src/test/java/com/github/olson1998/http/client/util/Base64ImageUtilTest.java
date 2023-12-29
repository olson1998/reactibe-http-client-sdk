package com.github.olson1998.http.client.util;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static com.github.olson1998.http.util.ImageExtension.PNG;
import static org.assertj.core.api.Assertions.assertThat;

class Base64ImageUtilTest {

    private static final String TEST_IMAGE_PATH = "src/test/resources/_imageb64.txt";

    @Test
    void shouldReadBase64Image(){
        var imageBase64 = readTestImageBase64();
        var imageRead = Base64ImageUtil.readBase64Image(imageBase64);
        assertThat(imageRead).isNotNull();
        assertThat(imageRead.extension()).isEqualTo(PNG);
        assertThat(imageRead.image()).isNotNull();
    }

    @SneakyThrows
    private String readTestImageBase64(){
        var imageFile = new File(TEST_IMAGE_PATH);
        return Files.readString(imageFile.toPath());
    }
}
