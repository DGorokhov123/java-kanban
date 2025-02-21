package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import manager.TaskNotFoundException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    public static final int DEFAULT_PORT = 8080;
    public static final int DEFAULT_MAX_CONNECTIONS = 0;

    private static Gson gsonInstance;
    private static Gson gsonStandardInstance;

    private final TaskManager taskManager;
    private final int port;
    private final int maxConnections;

    private HttpServer httpServer;

    HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.port = DEFAULT_PORT;
        this.maxConnections = DEFAULT_MAX_CONNECTIONS;
    }

    HttpTaskServer(TaskManager taskManager, int port, int maxConnections) {
        this.taskManager = taskManager;
        this.port = port;
        this.maxConnections = maxConnections;
    }

    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer(Managers.createNewFromFile(new File("file1.csv")));
        try {
            Managers.getDefault().getTaskById(5);  // generate history
            Managers.getDefault().getTaskById(8);
            Managers.getDefault().getTaskById(3);
        } catch (TaskNotFoundException e) {
            //nothing
        }
        try {
            taskServer.start();
        } catch (IOException e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }

    public static Gson getGson() {
        if (gsonInstance == null) {
            gsonInstance = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .registerTypeAdapter(Task.class, new TaskJsonDeserializer())
                    .registerTypeAdapter(Epic.class, new EpicJsonDeserializer())
                    .registerTypeAdapter(Subtask.class, new SubtaskJsonDeserializer())
                    .create();
        }
        return gsonInstance;
    }

    public static Gson getStandardGson() {
        if (gsonStandardInstance == null) {
            gsonStandardInstance = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();
        }
        return gsonStandardInstance;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), maxConnections);
        httpServer.createContext("/tasks", new TasksHttpHandler());
        httpServer.createContext("/subtasks", new SubtasksHttpHandler());
        httpServer.createContext("/epics", new EpicsHttpHandler());
        httpServer.createContext("/history", new HistoryHttpHandler());
        httpServer.createContext("/prioritized", new PrioritizedHttpHandler());
        httpServer.start();
        System.out.println("Сервер стартовал");
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("Сервер остановлен");
    }


}
