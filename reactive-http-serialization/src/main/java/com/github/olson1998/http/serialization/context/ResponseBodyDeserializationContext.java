package com.github.olson1998.http.serialization.context;

import com.github.olson1998.http.HttpHeaders;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.http.entity.ContentType;

import java.nio.charset.Charset;
import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ResponseBodyDeserializationContext implements DeserializationContext {

    private final int statusCode;

    private final ContentType contentType;

    private final HttpHeaders httpHeaders;

    @Override
    public Optional<Charset> findContentCharset() {
        return Optional.ofNullable(contentType)
                .map(ContentType::getCharset);
    }
}
