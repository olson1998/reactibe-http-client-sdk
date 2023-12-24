package com.github.olson1998.http.jacksonserial.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonJsonNodeDeserializer extends JacksonJsonDeserializer<JsonNode> {

    @Override
    public String getPrimaryContentType() {
        return "application/json";
    }

    public JacksonJsonNodeDeserializer(ObjectMapper objectMapper) {
        super(objectMapper, JsonNode.class);
    }
}
