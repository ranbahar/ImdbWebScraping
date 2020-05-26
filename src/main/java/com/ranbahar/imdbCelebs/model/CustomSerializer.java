package com.ranbahar.imdbCelebs.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class CustomSerializer extends StdDeserializer<Integer> {

    public CustomSerializer() {
        this(null);
    }

    public CustomSerializer(Class<Integer> t) {
        super(t);
    }

    @Override
    public Integer deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        int id = node.asInt();

        return id;
    }
}
