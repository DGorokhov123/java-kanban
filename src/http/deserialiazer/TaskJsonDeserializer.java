package http.deserialiazer;

import com.google.gson.*;
import http.HttpTaskServer;
import task.Epic;
import task.Subtask;
import task.Task;

import java.lang.reflect.Type;

public class TaskJsonDeserializer implements JsonDeserializer<Task> {

    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (!jsonObject.has("type") || !jsonObject.has("id") || !jsonObject.has("title")) {
            throw new JsonSyntaxException("json should contain a correct task Object");
        }

        if (Epic.class.getSimpleName().equals(jsonObject.get("type").getAsString())) {
            return HttpTaskServer.getStandardGson().fromJson(json, Epic.class);
        } else if (Subtask.class.getSimpleName().equals(jsonObject.get("type").getAsString())) {
            return HttpTaskServer.getStandardGson().fromJson(json, Subtask.class);
        } else if (Task.class.getSimpleName().equals(jsonObject.get("type").getAsString())) {
            return HttpTaskServer.getStandardGson().fromJson(json, Task.class);
        } else {
            throw new JsonSyntaxException("Wrong type field!");
        }

    }

}
