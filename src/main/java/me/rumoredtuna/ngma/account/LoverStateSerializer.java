package me.rumoredtuna.ngma.account;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class LoverStateSerializer extends JsonSerializer<LoverState> {

    @Override
    public void serialize(
            LoverState loverState, JsonGenerator generator, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        generator.writeStartObject();
        generator.writeFieldName("name");
        generator.writeString(loverState.name());
        generator.writeFieldName("hasWaiters");
        generator.writeBoolean(loverState.isHasWaiters());
        generator.writeEndObject();
    }
}
