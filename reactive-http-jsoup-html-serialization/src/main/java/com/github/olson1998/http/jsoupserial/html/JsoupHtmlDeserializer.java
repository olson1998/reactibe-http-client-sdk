package com.github.olson1998.http.jsoupserial.html;

import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ResponseMapping;
import com.github.olson1998.http.serialization.context.DeserializationContext;
import org.apache.http.entity.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.SerializationException;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.function.BiFunction;

import static org.apache.http.entity.ContentType.TEXT_HTML;

public class JsoupHtmlDeserializer implements ContentDeserializer {
    @Override
    public ContentType getPrimaryContentType() {
        return TEXT_HTML;
    }

    @Override
    public Set<ContentType> getSupportedContentTypes() {
        return Set.of(TEXT_HTML);
    }

    @Override
    public <C> BiFunction<byte[], DeserializationContext, C> deserialize(Class<C> deserializedPojoClass) {
        return null;
    }

    @Override
    public <C> BiFunction<byte[], DeserializationContext, C> deserializeMapped(ResponseMapping<C> responseMapping) {
        return null;
    }

    private <C> C doDeserializeTextHtml(byte[] htmlBytes, ContentType contentType, Class<C> responseMapping){
        if(responseMapping.equals(Document.class)){
            return (C) doDeserializeTextHtml(htmlBytes, contentType);
        }else {
            throw new SerializationException();
        }
    }

    private Document doDeserializeTextHtml(byte[] htmlBytes, ContentType contentType) {
        var charset = contentType.getCharset();
        try(var byteInputStream = new ByteArrayInputStream(htmlBytes)){
            return Jsoup.parse(byteInputStream, charset.name(), null);
        }catch (IOException e){
            return null;
        }
    }
}
