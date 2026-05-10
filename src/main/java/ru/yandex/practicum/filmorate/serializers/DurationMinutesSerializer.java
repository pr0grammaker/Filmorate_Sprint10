package ru.yandex.practicum.filmorate.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Duration;

public class DurationMinutesSerializer extends StdSerializer<Duration> {

    public DurationMinutesSerializer() {
        super(Duration.class);
    }

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        long minutes = value.toMinutes();
        gen.writeNumber(minutes);
    }
}
