package http;

import com.google.gson.Gson;
import manager.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiTest {

    TaskManager taskManager = Managers.createNewInMemory(new TaskFactory(), new InMemoryHistoryManager(5));
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    void setUp() throws WrongTaskArgumentException, TaskIntersectionException, TaskNotFoundException, IOException {
        taskManager.clearAllData();
        taskManager.add(new Task(0, "t1", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 12, 0), Duration.ofHours(2)));
        int e2 = taskManager.add(new Epic(0, "e2", "")).getId();
        taskManager.add(new Subtask(0, e2, "s3", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 15, 0), Duration.ofHours(2)));
        taskManager.add(new Subtask(0, e2, "s4", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 18, 0), Duration.ofHours(2)));
        taskManager.add(new Task(0, "t5", "", TaskStatus.NEW, null, null));
        int e6 = taskManager.add(new Epic(0, "e6", "")).getId();
        taskManager.add(new Subtask(0, e6, "s7", "", TaskStatus.NEW, null, null));
        taskServer.start();
    }

    /**
     * Test of List-based endpoints:
     * GET /tasks     * GET /epics     * GET /subtasks     * GET /epics/id/subtasks
     */
    @Test
    void getTasksEpicsSubtasksEpicSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
        HttpRequest request;
        HttpResponse<String> response;

        // testing endpoint: GET /tasks
        request = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/tasks")).header("Accept", "application/json").build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        List<Task> receivedTaskList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        List<Task> taskList = taskManager.getTasks();
        for (int i = 0; i < taskList.size(); i++) {
            assertEquals(taskList.get(i).getId(), receivedTaskList.get(i).getId());
            assertEquals(taskList.get(i).getTitle(), receivedTaskList.get(i).getTitle());
        }

        // testing endpoint: GET /epics
        request = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/epics")).header("Accept", "application/json").build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        List<Epic> receivedEpicList = gson.fromJson(response.body(), new EpicListTypeToken().getType());
        List<Epic> epicList = taskManager.getEpics();
        for (int i = 0; i < epicList.size(); i++) {
            assertEquals(epicList.get(i).getId(), receivedEpicList.get(i).getId());
            assertEquals(epicList.get(i).getTitle(), receivedEpicList.get(i).getTitle());
        }

        // testing endpoint: GET /epics/2/subtasks
        request = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/epics/2/subtasks")).header("Accept", "application/json").build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        List<Subtask> receivedEpicSubtasksList = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        List<Subtask> epicSubtasksList = taskManager.getEpicSubtasks(2);
        for (int i = 0; i < epicSubtasksList.size(); i++) {
            assertEquals(epicSubtasksList.get(i).getId(), receivedEpicSubtasksList.get(i).getId());
            assertEquals(epicSubtasksList.get(i).getTitle(), receivedEpicSubtasksList.get(i).getTitle());
        }

        // testing endpoint: GET /subtasks
        request = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/subtasks")).header("Accept", "application/json").build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        List<Subtask> receivedSubtasksList = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        List<Subtask> subtasksList = taskManager.getSubTasks();
        for (int i = 0; i < subtasksList.size(); i++) {
            assertEquals(subtasksList.get(i).getId(), receivedSubtasksList.get(i).getId());
            assertEquals(subtasksList.get(i).getTitle(), receivedSubtasksList.get(i).getTitle());
        }
        client.close();
    }

    /**
     * Test of GET by ID endpoints:
     * GET /tasks/id     * GET /epics/id     * GET /subtasks/id
     */
    @Test
    void getTaskEpicSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        // testing endpoint: GET /tasks/id
        for (Task task : taskManager.getTasks()) {
            HttpRequest request = HttpRequest.newBuilder().GET().header("Accept", "application/json")
                    .uri(URI.create("http://localhost:8080/tasks/" + task.getId())).build();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(200, response.statusCode());
            Task receivedTask = gson.fromJson(response.body(), Task.class);
            assertEquals(task.getId(), receivedTask.getId());
            assertEquals(task.getTitle(), receivedTask.getTitle());
        }
        String[] wrongIds = {"-1", "0", "2", "3", "4", "6", "7", "8", "/", "a", "2a"};
        int[] wrongCodes = {404, 404, 404, 404, 404, 404, 404, 404, 200, 400, 400};
        for (int i = 0; i < wrongIds.length; i++) {
            HttpRequest request = HttpRequest.newBuilder().GET().header("Accept", "application/json")
                    .uri(URI.create("http://localhost:8080/tasks/" + wrongIds[i])).build();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(wrongCodes[i], response.statusCode());
        }

        // testing endpoint: GET /epics/id
        for (Epic epic : taskManager.getEpics()) {
            HttpRequest request = HttpRequest.newBuilder().GET().header("Accept", "application/json")
                    .uri(URI.create("http://localhost:8080/epics/" + epic.getId())).build();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(200, response.statusCode());
            Epic receivedEpic = gson.fromJson(response.body(), Epic.class);
            assertEquals(epic.getId(), receivedEpic.getId());
            assertEquals(epic.getTitle(), receivedEpic.getTitle());
        }
        wrongIds = new String[]{"-1", "0", "1", "3", "4", "5", "7", "8", "/", "a", "2a"};
        wrongCodes = new int[]{404, 404, 404, 404, 404, 404, 404, 404, 200, 400, 400};
        for (int i = 0; i < wrongIds.length; i++) {
            HttpRequest request = HttpRequest.newBuilder().GET().header("Accept", "application/json")
                    .uri(URI.create("http://localhost:8080/epics/" + wrongIds[i])).build();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(wrongCodes[i], response.statusCode());
        }

        // testing endpoint: GET /subtasks/id
        for (Subtask subtask : taskManager.getSubTasks()) {
            HttpRequest request = HttpRequest.newBuilder().GET().header("Accept", "application/json")
                    .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId())).build();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(200, response.statusCode());
            Subtask receivedSubtask = gson.fromJson(response.body(), Subtask.class);
            assertEquals(subtask.getId(), receivedSubtask.getId());
            assertEquals(subtask.getTitle(), receivedSubtask.getTitle());
        }
        wrongIds = new String[]{"-1", "0", "1", "2", "5", "6", "8", "/", "a", "2a"};
        wrongCodes = new int[]{404, 404, 404, 404, 404, 404, 404, 200, 400, 400};
        for (int i = 0; i < wrongIds.length; i++) {
            HttpRequest request = HttpRequest.newBuilder().GET().header("Accept", "application/json")
                    .uri(URI.create("http://localhost:8080/subtasks/" + wrongIds[i])).build();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(wrongCodes[i], response.statusCode());
        }
        client.close();
    }

    /**
     * Test of Update endpoints:
     * POST /tasks/id     * POST /epics/id     * POST /subtasks/id
     */
    @Test
    void updateTaskEpicSubtask() throws IOException, InterruptedException, TaskNotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
        HttpResponse<String> response;
        HttpRequest request;

        // testing endpoint: POST /tasks/id
        Task t1 = new Task(1, "t1", "updated task", TaskStatus.NEW, null, null);
        String json = gson.toJson(t1);
        request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Accept", "application/json").header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/tasks/1")).build();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        assertEquals("t1", taskManager.getTaskById(1).getTitle());
        assertEquals("updated task", taskManager.getTaskById(1).getDescription());
        String[] wrongIds = {"-1", "0", "2", "3", "4", "5", "6", "7", "8", "a", "2a"};
        int[] wrongCodes = {404, 404, 404, 404, 404, 404, 404, 404, 404, 400, 400};
        for (int i = 0; i < wrongIds.length; i++) {
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Accept", "application/json").header("Content-Type", "application/json")
                    .uri(URI.create("http://localhost:8080/tasks/" + wrongIds[i])).build();
            response = client.send(request, handler);
            assertEquals(wrongCodes[i], response.statusCode());
        }

        // testing endpoint: POST /epics/id
        Epic e2 = new Epic(2, "e2", "updated epic");
        json = gson.toJson(e2);
        request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Accept", "application/json").header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/epics/2")).build();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        assertEquals("e2", taskManager.getTaskById(2).getTitle());
        assertEquals("updated epic", taskManager.getTaskById(2).getDescription());
        wrongIds = new String[]{"-1", "0", "1", "3", "4", "5", "6", "7", "8", "a", "2a"};
        wrongCodes = new int[]{404, 404, 404, 404, 404, 404, 404, 404, 404, 400, 400};
        for (int i = 0; i < wrongIds.length; i++) {
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Accept", "application/json").header("Content-Type", "application/json")
                    .uri(URI.create("http://localhost:8080/epics/" + wrongIds[i])).build();
            response = client.send(request, handler);
            assertEquals(wrongCodes[i], response.statusCode());
        }

        // testing endpoint: POST /subtasks/id
        Subtask s3 = new Subtask(3, 2, "s3", "updated subtask", TaskStatus.NEW, null, null);
        json = gson.toJson(s3);
        request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Accept", "application/json").header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/subtasks/3")).build();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        assertEquals("s3", taskManager.getTaskById(3).getTitle());
        assertEquals("updated subtask", taskManager.getTaskById(3).getDescription());
        wrongIds = new String[]{"-1", "0", "1", "2", "4", "5", "6", "7", "8", "a", "2a"};
        wrongCodes = new int[]{404, 404, 404, 404, 404, 404, 404, 404, 404, 400, 400};
        for (int i = 0; i < wrongIds.length; i++) {
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Accept", "application/json").header("Content-Type", "application/json")
                    .uri(URI.create("http://localhost:8080/subtasks/" + wrongIds[i])).build();
            response = client.send(request, handler);
            assertEquals(wrongCodes[i], response.statusCode());
        }
        client.close();
    }

    /**
     * Intersected task exceptions test of Update endpoints:
     * POST /tasks/id     * POST /epics/id     * POST /subtasks/id
     */
    @Test
    void updateIntersectedTaskSubtask() throws IOException, InterruptedException, TaskNotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
        HttpResponse<String> response;
        HttpRequest request;

        // testing endpoint: POST /tasks/id

        // not-intersected task
        Task t1 = new Task(1, "t1", "updated task", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 12, 0), Duration.ofHours(2));
        String json = gson.toJson(t1);
        request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Accept", "application/json").header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/tasks/1")).build();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        assertEquals("t1", taskManager.getTaskById(1).getTitle());
        assertEquals("updated task", taskManager.getTaskById(1).getDescription());

        // intersected task
        t1 = new Task(1, "t1", "intersected task", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 13, 0), Duration.ofHours(3));
        json = gson.toJson(t1);
        request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Accept", "application/json").header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/tasks/1")).build();
        response = client.send(request, handler);
        assertEquals(406, response.statusCode());
        assertEquals("t1", taskManager.getTaskById(1).getTitle());
        assertEquals("updated task", taskManager.getTaskById(1).getDescription());

        // testing endpoint: POST /subtasks/id

        // not-intersected subtask
        Subtask s3 = new Subtask(3, 2, "s3", "updated subtask", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 15, 0), Duration.ofHours(2));
        json = gson.toJson(s3);
        request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Accept", "application/json").header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/subtasks/3")).build();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        assertEquals("s3", taskManager.getTaskById(3).getTitle());
        assertEquals("updated subtask", taskManager.getTaskById(3).getDescription());

        // intersected subtask
        s3 = new Subtask(3, 2, "s3", "intersected subtask", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 13, 0), Duration.ofHours(3));
        json = gson.toJson(s3);
        request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Accept", "application/json").header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/subtasks/3")).build();
        response = client.send(request, handler);
        assertEquals(406, response.statusCode());
        assertEquals("s3", taskManager.getTaskById(3).getTitle());
        assertEquals("updated subtask", taskManager.getTaskById(3).getDescription());

        client.close();
    }

    /**
     * Test of Create endpoints:
     * POST /tasks     * POST /epics     * POST /subtasks
     */
    @Test
    void createTaskEpicSubtask() throws IOException, InterruptedException, TaskNotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
        HttpResponse<String> response;
        HttpRequest request;

        // testing endpoint: POST /tasks
        Map<String, Integer> jsons = new LinkedHashMap<>();
        jsons.put("", 400);
        jsons.put("{\"name\":\"ivan\"}", 400);
        jsons.put("nonsense", 400);
        jsons.put("[]", 400);
        jsons.put("{\"type\":\"FAKE\",\"id\":0,\"title\":\"ttt\",\"description\":\"fake\"," +
                "\"status\":\"NEW\",\"startTime\":\"\",\"duration\":\"\"}", 400);
        jsons.put(gson.toJson(new Task(0, "intersected", "task", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 13, 0), Duration.ofHours(3))), 406);
        jsons.put(gson.toJson(new Epic(0, "fake", "epic")), 400);
        jsons.put(gson.toJson(new Subtask(3, 2, "fake", "subtask", TaskStatus.NEW, null, null)), 400);
        jsons.put(gson.toJson(new Task(0, "t8", "created task", TaskStatus.NEW, null, null)), 201);

        for (Map.Entry<String, Integer> entry : jsons.entrySet()) {
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(entry.getKey()))
                    .header("Accept", "application/json").header("Content-Type", "application/json")
                    .uri(URI.create("http://localhost:8080/tasks")).build();
            response = client.send(request, handler);
            assertEquals(entry.getValue(), response.statusCode());
        }
        assertEquals("t8", taskManager.getTaskById(8).getTitle());

        // testing endpoint: POST /epics
        jsons = new LinkedHashMap<>();
        jsons.put("", 400);
        jsons.put("{\"name\":\"ivan\"}", 400);
        jsons.put("nonsense", 400);
        jsons.put("[]", 400);
        jsons.put(gson.toJson(new Task(0, "fake", "task", TaskStatus.NEW, null, null)), 400);
        jsons.put("{\"type\":\"FAKE\",\"id\":0,\"title\":\"fake\",\"description\":\"epic\"," +
                "\"status\":\"NEW\",\"startTime\":\"\",\"duration\":\"\"}", 400);
        jsons.put(gson.toJson(new Subtask(3, 2, "fake", "subtask", TaskStatus.NEW, null, null)), 400);
        jsons.put(gson.toJson(new Epic(0, "e9", "created epic")), 201);

        for (Map.Entry<String, Integer> entry : jsons.entrySet()) {
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(entry.getKey()))
                    .header("Accept", "application/json").header("Content-Type", "application/json")
                    .uri(URI.create("http://localhost:8080/epics")).build();
            response = client.send(request, handler);
            assertEquals(entry.getValue(), response.statusCode());
        }
        assertEquals("e9", taskManager.getTaskById(9).getTitle());

        // testing endpoint: POST /subtasks
        jsons = new LinkedHashMap<>();
        jsons.put("", 400);
        jsons.put("{\"name\":\"ivan\"}", 400);
        jsons.put("nonsense", 400);
        jsons.put("[]", 400);
        jsons.put("{\"epicId\":9,\"type\":\"FAKE\",\"id\":0,\"title\":\"s10\",\"description\":" +
                "\"created subtask\",\"status\":\"NEW\",\"startTime\":\"\",\"duration\":\"\"}", 400);
        jsons.put(gson.toJson(new Task(0, "fake", "task", TaskStatus.NEW, null, null)), 400);
        jsons.put(gson.toJson(new Epic(0, "fake", "epic")), 400);
        jsons.put(gson.toJson(new Subtask(0, 2, "intersected", "subtask", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 13, 0), Duration.ofHours(3))), 406);
        jsons.put(gson.toJson(new Subtask(0, 1, "wrong epicId", "", TaskStatus.NEW, null, null)), 404);
        jsons.put(gson.toJson(new Subtask(0, 9, "s10", "created subtask", TaskStatus.NEW, null, null)), 201);

        for (Map.Entry<String, Integer> entry : jsons.entrySet()) {
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(entry.getKey()))
                    .header("Accept", "application/json").header("Content-Type", "application/json")
                    .uri(URI.create("http://localhost:8080/subtasks")).build();
            response = client.send(request, handler);
            assertEquals(entry.getValue(), response.statusCode());
        }
        assertEquals("s10", taskManager.getTaskById(10).getTitle());

        client.close();
    }

    /**
     * Test of Delete endpoints:
     * DELETE /tasks     * DELETE /epics     * DELETE /subtasks
     */
    @Test
    void deleteTaskEpicSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
        HttpResponse<String> response;
        HttpRequest request;

        // testing endpoint: DELETE /tasks/id
        String[] wrongIds = {"-1", "0", "2", "3", "4", "6", "7", "8", "a", "2a"};
        int[] wrongCodes = {404, 404, 404, 404, 404, 404, 404, 404, 400, 400};
        for (int i = 0; i < wrongIds.length; i++) {
            request = HttpRequest.newBuilder().DELETE().header("Accept", "application/json")
                    .uri(URI.create("http://localhost:8080/tasks/" + wrongIds[i])).build();
            response = client.send(request, handler);
            assertEquals(wrongCodes[i], response.statusCode());
        }
        assertDoesNotThrow(() -> {taskManager.getTaskById(1);});
        request = HttpRequest.newBuilder().DELETE().header("Accept", "application/json")
                .uri(URI.create("http://localhost:8080/tasks/1")).build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        assertThrows(TaskNotFoundException.class, () -> {taskManager.getTaskById(1);});

        // testing endpoint: DELETE /epics/id
        wrongIds = new String[]{"-1", "0", "1", "3", "4", "5", "7", "8", "a", "2a"};
        wrongCodes = new int[]{404, 404, 404, 404, 404, 404, 404, 404, 400, 400};
        for (int i = 0; i < wrongIds.length; i++) {
            request = HttpRequest.newBuilder().DELETE().header("Accept", "application/json")
                    .uri(URI.create("http://localhost:8080/epics/" + wrongIds[i])).build();
            response = client.send(request, handler);
            assertEquals(wrongCodes[i], response.statusCode());
        }
        assertDoesNotThrow(() -> {taskManager.getTaskById(6);});
        assertDoesNotThrow(() -> {taskManager.getTaskById(7);});
        request = HttpRequest.newBuilder().DELETE().header("Accept", "application/json")
                .uri(URI.create("http://localhost:8080/epics/6")).build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        assertThrows(TaskNotFoundException.class, () -> {taskManager.getTaskById(6);});
        assertThrows(TaskNotFoundException.class, () -> {taskManager.getTaskById(7);});

        // testing endpoint: DELETE /subtasks/id
        wrongIds = new String[]{"-1", "0", "1", "2", "5", "6", "8", "a", "2a"};
        wrongCodes = new int[]{404, 404, 404, 404, 404, 404, 404, 400, 400};
        for (int i = 0; i < wrongIds.length; i++) {
            request = HttpRequest.newBuilder().DELETE().header("Accept", "application/json")
                    .uri(URI.create("http://localhost:8080/subtasks/" + wrongIds[i])).build();
            response = client.send(request, handler);
            assertEquals(wrongCodes[i], response.statusCode());
        }
        assertDoesNotThrow(() -> {taskManager.getTaskById(3);});
        request = HttpRequest.newBuilder().DELETE().header("Accept", "application/json")
                .uri(URI.create("http://localhost:8080/subtasks/3")).build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        assertThrows(TaskNotFoundException.class, () -> {taskManager.getTaskById(3);});

        client.close();
    }

    /**
     * Test of special endpoints:
     * GET /history     * GET /prioritized
     */
    @Test
    void getHistoryAndPrioritized() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
        HttpRequest request;
        HttpResponse<String> response;

        // testing endpoint: GET /history
        assertDoesNotThrow(() -> {taskManager.getTaskById(2);});
        assertDoesNotThrow(() -> {taskManager.getTaskById(5);});
        assertDoesNotThrow(() -> {taskManager.getTaskById(1);});
        assertDoesNotThrow(() -> {taskManager.getTaskById(7);});
        request = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/history")).header("Accept", "application/json").build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        List<Task> receivedList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        List<Task> taskList = taskManager.getHistory();
        for (int i = 0; i < taskList.size(); i++) {
            assertEquals(taskList.get(i).getId(), receivedList.get(i).getId());
            assertEquals(taskList.get(i).getTitle(), receivedList.get(i).getTitle());
        }

        // testing endpoint: GET /prioritized
        request = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/prioritized")).header("Accept", "application/json").build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        receivedList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        taskList = taskManager.getPrioritizedTasks();
        for (int i = 0; i < taskList.size(); i++) {
            assertEquals(taskList.get(i).getId(), receivedList.get(i).getId());
            assertEquals(taskList.get(i).getTitle(), receivedList.get(i).getTitle());
            assertEquals(taskList.get(i).getStartTime(), receivedList.get(i).getStartTime());
            assertEquals(taskList.get(i).getDuration(), receivedList.get(i).getDuration());
        }

        client.close();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

}