package http.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskIntersectionException;
import exception.TaskNotFoundException;
import exception.WrongTaskArgumentException;
import http.HttpTaskServer;
import manager.Managers;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHttpHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Headers headers = exchange.getRequestHeaders();
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if ("GET".equals(method)) {
            if (path.length < 3) {
                getSubtasks(exchange);
            } else {
                try {
                    int taskId = Integer.parseInt(path[2]);
                    getSubtaskById(exchange, taskId);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange, "Not supported GET request, wrong Subtask ID");
                }
            }
        } else if ("POST".equals(method)) {
            if (path.length < 3) {
                createSubtask(exchange, body);
            } else {
                try {
                    int taskId = Integer.parseInt(path[2]);
                    updateSubtaskById(exchange, taskId, body);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange, "Not supported POST request, wrong Subtask ID");
                }
            }
        } else if ("DELETE".equals(method)) {
            if (path.length < 3) {
                sendBadRequest(exchange, "Not supported DELETE request, missing Subtask ID");
            } else {
                try {
                    int taskId = Integer.parseInt(path[2]);
                    deleteSubtaskById(exchange, taskId);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange, "Not supported DELETE request, wrong Subtask ID");
                }
            }
        } else {
            sendBadRequest(exchange, "Not supported request");
        }
    }

    private void getSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = Managers.getDefault().getSubTasks();
        String json = HttpTaskServer.getGson().toJson(subtasks);
        sendJson(exchange, json);
    }

    private void getSubtaskById(HttpExchange exchange, int taskId) throws IOException {
        try {
            Subtask subtask = (Subtask) Managers.getDefault().getTaskById(taskId);
            String json = HttpTaskServer.getGson().toJson(subtask);
            sendJson(exchange, json);
        } catch (ClassCastException e) {
            sendNotFound(exchange, "Get error: Object is not an instance of Subtask.class");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Get error: Subtask not found");
        }
    }

    private void deleteSubtaskById(HttpExchange exchange, int taskId) throws IOException {
        try {
            Subtask subtask = (Subtask) Managers.getDefault().getTaskById(taskId);
            Managers.getDefault().removeById(subtask.getId());
            sendOK(exchange);
        } catch (ClassCastException e) {
            sendNotFound(exchange, "Delete error: Object is not an instance of Subtask.class");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Delete error: Subtask not found");
        }
    }

    private void updateSubtaskById(HttpExchange exchange, int taskId, String body) throws IOException {
        try {
            Managers.getDefault().getTaskById(taskId);
            Subtask newSubtask = HttpTaskServer.getGson().fromJson(body, Subtask.class);
            if (newSubtask == null) throw new WrongTaskArgumentException();
            if (newSubtask.getId() != taskId) throw new IllegalArgumentException();
            Managers.getDefault().update(newSubtask);
            sendNoContent(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Update error: body should contain a correct JSON Object of Subtask.class");
        } catch (ClassCastException e) {
            sendBadRequest(exchange, "Update error: Object is not an instance of Subtask.class");
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange, "Update error: Subtask and URL id mismatch");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Update error: Subtask not found");
        } catch (WrongTaskArgumentException e) {
            sendBadRequest(exchange, "Update error: Empty or wrong body");
        } catch (TaskIntersectionException e) {
            sendConflict(exchange, "Subtask has intersections with existing tasks");
        }
    }

    private void createSubtask(HttpExchange exchange, String body) throws IOException {
        try {
            Subtask newSubtask = HttpTaskServer.getGson().fromJson(body, Subtask.class);
            Managers.getDefault().add(newSubtask);
            sendNoContent(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Create error: body should contain a correct JSON Object of Subtask.class");
        } catch (WrongTaskArgumentException e) {
            sendBadRequest(exchange, "Create error: Empty or wrong body");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Create error: Subtask's Epic not found");
        } catch (TaskIntersectionException e) {
            sendConflict(exchange, "Subtask has intersections with existing tasks");
        }
    }

}
