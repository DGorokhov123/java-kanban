package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import manager.TaskNotFoundException;
import manager.WrongTaskArgumentException;
import task.Epic;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHttpHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Headers headers = exchange.getRequestHeaders();
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if ("GET".equals(method)) {
            if (path.length < 3) {
                getEpics(exchange);
            } else {
                try {
                    int taskId = Integer.parseInt(path[2]);
                    if (path.length < 4) {
                        getEpicById(exchange, taskId);
                    } else if ("subtasks".equalsIgnoreCase(path[3])) {
                        getEpicSubtasks(exchange, taskId);
                    } else {
                        sendBadRequest(exchange, "Not supported GET request, wrong command after Epic ID");
                    }
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange, "Not supported GET request, wrong Epic ID");
                }
            }
        } else if ("POST".equals(method)) {
            if (path.length < 3) {
                createEpic(exchange, body);
            } else {
                try {
                    int taskId = Integer.parseInt(path[2]);
                    updateEpicById(exchange, taskId, body);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange, "Not supported POST request, wrong Epic ID");
                }
            }
        } else if ("DELETE".equals(method)) {
            if (path.length < 3) {
                sendBadRequest(exchange, "Not supported DELETE request, missing Epic ID");
            } else {
                try {
                    int taskId = Integer.parseInt(path[2]);
                    deleteEpicById(exchange, taskId);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange, "Not supported DELETE request, wrong Epic ID");
                }
            }
        } else {
            sendBadRequest(exchange, "Not supported request");
        }
    }

    private void getEpicSubtasks(HttpExchange exchange, int taskId) throws IOException {
        List<Subtask> subtasks = Managers.getDefault().getEpicSubtasks(taskId);
        String json = HttpTaskServer.getGson().toJson(subtasks);
        sendJson(exchange, json);
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = Managers.getDefault().getEpics();
        String json = HttpTaskServer.getGson().toJson(epics);
        sendJson(exchange, json);
    }

    private void getEpicById(HttpExchange exchange, int taskId) throws IOException {
        try {
            Epic epic = (Epic) Managers.getDefault().getTaskById(taskId);
            String json = HttpTaskServer.getGson().toJson(epic);
            sendJson(exchange, json);
        } catch (ClassCastException e) {
            sendNotFound(exchange, "Get error: Object is not an instance of Epic.class");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Get error: Epic not found");
        }
    }

    private void deleteEpicById(HttpExchange exchange, int taskId) throws IOException {
        try {
            Epic epic = (Epic) Managers.getDefault().getTaskById(taskId);
            Managers.getDefault().removeById(epic.getId());
            send200(exchange);
        } catch (ClassCastException e) {
            sendNotFound(exchange, "Delete error: Object is not an instance of Epic.class");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Delete error: Epic not found");
        }
    }

    private void updateEpicById(HttpExchange exchange, int taskId, String body) throws IOException {
        try {
            Managers.getDefault().getTaskById(taskId);
            Epic newEpic = HttpTaskServer.getGson().fromJson(body, Epic.class);
            if (newEpic == null) throw new WrongTaskArgumentException();
            if (newEpic.getId() != taskId) throw new IllegalArgumentException();
            Managers.getDefault().update(newEpic);
            send201(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Update error: body should contain a correct JSON Object of Epic.class");
        } catch (ClassCastException e) {
            sendBadRequest(exchange, "Update error: Object is not an instance of Epic.class");
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange, "Update error: Epic and URL id mismatch");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Update error: Epic not found");
        } catch (WrongTaskArgumentException e) {
            sendBadRequest(exchange, "Update error: Empty or wrong body");
        }
    }

    private void createEpic(HttpExchange exchange, String body) throws IOException {
        try {
            Epic newEpic = HttpTaskServer.getGson().fromJson(body, Epic.class);
            Managers.getDefault().add(newEpic);
            send201(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Create error: body should contain a correct JSON Object of Epic.class");
        } catch (WrongTaskArgumentException e) {
            sendBadRequest(exchange, "Create error: Empty or wrong body");
        }
    }

}
