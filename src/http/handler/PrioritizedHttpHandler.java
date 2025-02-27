package http.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import manager.Managers;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PrioritizedHttpHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Headers headers = exchange.getRequestHeaders();
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if ("GET".equals(method) && path.length < 3) {
            getPrioritized(exchange);
        } else {
            sendBadRequest(exchange, "Not supported request");
        }

    }

    private void getPrioritized(HttpExchange exchange) throws IOException {
        List<Task> tasks = Managers.getDefault().getPrioritizedTasks();
        String json = HttpTaskServer.getGson().toJson(tasks);
        sendJson(exchange, json);
    }

}
