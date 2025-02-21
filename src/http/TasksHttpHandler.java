package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import manager.TaskIntersectionException;
import manager.TaskNotFoundException;
import manager.WrongTaskArgumentException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHttpHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Headers headers = exchange.getRequestHeaders();
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if ("GET".equals(method)) {
            if (path.length < 3) {
                getTasks(exchange);
            } else {
                try {
                    int taskId = Integer.parseInt(path[2]);
                    getTaskById(exchange, taskId);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange, "Not supported GET request, wrong Task ID");
                }
            }
        } else if ("POST".equals(method)) {
            if (path.length < 3) {
                createTask(exchange, body);
            } else {
                try {
                    int taskId = Integer.parseInt(path[2]);
                    updateTaskById(exchange, taskId, body);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange, "Not supported POST request, wrong Task ID");
                }
            }
        } else if ("DELETE".equals(method)) {
            if (path.length < 3) {
                sendBadRequest(exchange, "Not supported DELETE request, missing Task ID");
            } else {
                try {
                    int taskId = Integer.parseInt(path[2]);
                    deleteTaskById(exchange, taskId);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange, "Not supported DELETE request, wrong Task ID");
                }
            }
        } else {
            sendBadRequest(exchange, "Not supported request");
        }
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = Managers.getDefault().getTasks();
        String json = HttpTaskServer.getGson().toJson(tasks);
        sendJson(exchange, json);
    }

    private void getTaskById(HttpExchange exchange, int taskId) throws IOException {
        try {
            Task task = Managers.getDefault().getTaskById(taskId);
            if ((task instanceof Epic) || (task instanceof Subtask)) throw new ClassCastException();
            String json = HttpTaskServer.getGson().toJson(task);
            sendJson(exchange, json);
        } catch (ClassCastException e) {
            sendNotFound(exchange, "Get error: Object is not an instance of Task.class");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Get error: Task not found");
        }
    }

    private void deleteTaskById(HttpExchange exchange, int taskId) throws IOException {
        try {
            Task task = Managers.getDefault().getTaskById(taskId);
            if ((task instanceof Epic) || (task instanceof Subtask)) throw new ClassCastException();
            Managers.getDefault().removeById(task.getId());
            send200(exchange);
        } catch (ClassCastException e) {
            sendNotFound(exchange, "Delete error: Object is not an instance of Task.class");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Delete error: Task not found");
        }
    }

    private void updateTaskById(HttpExchange exchange, int taskId, String body) throws IOException {
        try {
            Managers.getDefault().getTaskById(taskId);
            Task newTask = HttpTaskServer.getGson().fromJson(body, Task.class);
            if (newTask == null) throw new WrongTaskArgumentException();
            if (newTask.getId() != taskId) throw new IllegalArgumentException();
            Managers.getDefault().update(newTask);
            send201(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Update error: body should contain a correct JSON Object of Task.class");
        } catch (ClassCastException e) {
            sendBadRequest(exchange, "Update error: Object is not an instance of Task.class");
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange, "Update error: Task not found");
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange, "Update error: Task and URL id mismatch");
        } catch (WrongTaskArgumentException e) {
            sendBadRequest(exchange, "Update error: Empty or wrong body");
        } catch (TaskIntersectionException e) {
            sendHasInteractions(exchange, "Task has intersections with existing tasks");
        }
    }

    private void createTask(HttpExchange exchange, String body) throws IOException {
        try {
            Task newTask = HttpTaskServer.getGson().fromJson(body, Task.class);
            if (newTask instanceof Epic || newTask instanceof Subtask) throw new JsonSyntaxException("");
            Managers.getDefault().add(newTask);
            send201(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Create error: body should contain a correct JSON Object of Task.class");
        } catch (WrongTaskArgumentException e) {
            sendBadRequest(exchange, "Create error: Empty or wrong body");
        } catch (TaskIntersectionException e) {
            sendHasInteractions(exchange, "Task has intersections with existing tasks");
        }
    }

}
