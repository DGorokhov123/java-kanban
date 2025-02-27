package http.deserialiazer;

import com.google.gson.*;
import http.HttpTaskServer;
import task.Epic;

import java.lang.reflect.Type;

public class EpicJsonDeserializer implements JsonDeserializer<Epic> {
    @Override
    public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (!jsonObject.has("type") || !jsonObject.has("id") || !jsonObject.has("title") ||
                !Epic.class.getSimpleName().equals(jsonObject.get("type").getAsString())) {
            throw new JsonSyntaxException("json should contain a correct JSON Object of Epic.class");
        }
        return HttpTaskServer.getStandardGson().fromJson(json, Epic.class);
    }
}
