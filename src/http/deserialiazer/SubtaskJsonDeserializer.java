package http.deserialiazer;

import com.google.gson.*;
import http.HttpTaskServer;
import task.Subtask;

import java.lang.reflect.Type;

public class SubtaskJsonDeserializer implements JsonDeserializer<Subtask> {
    @Override
    public Subtask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (!jsonObject.has("type") || !jsonObject.has("id") || !jsonObject.has("title") || !jsonObject.has("epicId") ||
                !Subtask.class.getSimpleName().equals(jsonObject.get("type").getAsString())) {
            throw new JsonSyntaxException("json should contain a correct JSON Object of Subtask.class");
        }
        return HttpTaskServer.getStandardGson().fromJson(json, Subtask.class);
    }
}
