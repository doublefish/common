package com.tt.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.var;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * CustomInstantSerializer
 *
 * @author Shuang Yu
 */
public class CustomInstantSerializer extends JsonSerializer<Instant> {

    private final DateTimeFormatter formatter;

    public CustomInstantSerializer() {
        this(null);
    }

    public CustomInstantSerializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            return;
        }
        var string = formatter != null ? formatter.format(value.atZone(ZoneId.systemDefault())) : value.toString();
        gen.writeString(string);
    }
}
