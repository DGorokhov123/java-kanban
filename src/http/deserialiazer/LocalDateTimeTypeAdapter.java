package http.deserialiazer;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import task.Task;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.value("");
        } else {
            out.value(value.format(Task.DATE_TIME_FORMATTER));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        try {
            return LocalDateTime.parse(in.nextString(), Task.DATE_TIME_FORMATTER);
        } catch (DateTimeException e) {
            return null;
        }
    }
}
