package http.deserialiazer;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        if (value == null) {
            out.value("");
        } else {
            out.value(String.valueOf(value.toSeconds()));
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        try {
            return Duration.ofSeconds(Long.parseLong(in.nextString()));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
