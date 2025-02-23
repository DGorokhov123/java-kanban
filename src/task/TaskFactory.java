package task;

import exception.WrongCSVLineException;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class. Creates new objects of Task, Subtask, Epic types.
 * <br>Sequentially sets IDs for all new objects.
 */
public class TaskFactory {

    private int taskCounter = 0;

    /**
     * New simple task generator. Returns new simple Task object (not Subtask / Epic)
     * @return {@code Task} new object
     */
    public Task newTask(Task task) {
        if (task == null) return null;
        taskCounter++;
        return new Task(taskCounter, task.getTitle(), task.getDescription(), task.getStatus(), task.getStartTime(), task.getDuration());
    }

    /**
     * New Epic generator. Returns new Epic object.
     * @return {@code Epic} new object
     */
    public Epic newEpic(Epic epic) {
        if (epic == null) return null;
        taskCounter++;
        return new Epic(taskCounter, epic.getTitle(), epic.getDescription());
    }


    /**
     * New subtask generator. Returns new Subtask object.
     * @return {@code Subtask} new object
     */
    public Subtask newSubtask(Subtask subtask) {
        if (subtask == null) return null;
        taskCounter++;
        return new Subtask(taskCounter, 0, subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getStartTime(), subtask.getDuration());
    }

    public Task fromCSVLine(String line) throws WrongCSVLineException {
        List<String> values = new ArrayList<>();
        boolean readMode = false;
        StringBuilder curValue = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                readMode = !readMode;
            } else if (c == ',' && !readMode) {
                values.add(curValue.toString());
                curValue.setLength(0);
            } else {
                curValue.append(c);
            }
        }
        values.add(curValue.toString());

        if (values.size() < 7)  throw new WrongCSVLineException("Incorrect CSV line (size<7): " + line);
        int id;
        Task res;
        try {
            id = Integer.parseInt(values.get(0));                    // throws NumberFormatException (extends IllegalArgumentException)
            if (id < 1)  throw new WrongCSVLineException("ID field should be > 0, but = " + id);
            TaskType type = TaskType.valueOf(values.get(1));             // throws IllegalArgumentException
            String title = values.get(2);
            TaskStatus status = TaskStatus.valueOf(values.get(3));       // throws IllegalArgumentException
            String description = values.get(4);
            LocalDateTime startTime = (values.get(5).isBlank()) ? null
                     : LocalDateTime.parse(values.get(5), Task.DATE_TIME_FORMATTER);  // throws DateTimeParseException
            Duration duration = (values.get(6).isBlank()) ? null
                     : Duration.ofSeconds(Long.parseLong(values.get(6)));   // throws NumberFormatException

            if (type == TaskType.EPIC) {
                res = new Epic(id, title, description);
            } else if (type == TaskType.SUBTASK) {
                if (values.size() < 8)  throw new WrongCSVLineException("CSV line should have 8 elements to be deserialized to SUBTASK");
                int epicId = Integer.parseInt(values.get(7));            // throws NumberFormatException
                if (epicId < 1)  throw new WrongCSVLineException("EPIC ID field should be > 0, but = " + id);
                res = new Subtask(id, epicId, title, description, status, startTime, duration);
            } else {
                res = new Task(id, title, description, status, startTime, duration);
            }

        } catch (IllegalArgumentException | DateTimeException e) {
            throw new WrongCSVLineException("Incorrect CSV line: " + line);
        }
        if (id > taskCounter)  taskCounter = id;
        return res;
    }

    /**
     * Resets counter. Usable before loading from file
     */
    public void clear() {
        taskCounter = 0;
    }


}
