package com.example;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class RuleNodeSerializer extends StdSerializer<RuleNode> {

    public RuleNodeSerializer() {
        this(null);
    }

    public RuleNodeSerializer(Class<RuleNode> t) {
        super(t);
    }

    @Override
    public void serialize(RuleNode value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", Integer.toString(value.getId()));
        gen.writeStringField("text", value.getText());
        gen.writeEndObject();
    }
}
