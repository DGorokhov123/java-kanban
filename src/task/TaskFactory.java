package task;

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
        return new Task(taskCounter, task.getTitle(), task.getDescription(), task.getStatus());
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
        return new Subtask(taskCounter, subtask.getTitle(), subtask.getDescription(), subtask.getStatus());
    }

    public Task fromCSVLine(String line) throws WrongCSVLineException {
        List<String> values = new ArrayList<>();
        boolean readMode = false;
        StringBuilder curValue = new StringBuilder();
        for (char c : line.toCharArray()) {
            if ( c == '"') {
                readMode = !readMode;
            } else if (c == ',' && !readMode) {
                values.add(curValue.toString());
                curValue.setLength(0);
            } else {
                curValue.append(c);
            }
        }
        values.add(curValue.toString());

        if (values.size() < 5)  throw new WrongCSVLineException("Incorrect CSV line: " + line);
        int id;
        Task res;
        try {
            id = Integer.parseInt(values.get(0));                    // throws NumberFormatException
            if (id < 1)  throw new WrongCSVLineException("ID field should be > 0, but = " + id);
            TaskType type = TaskType.valueOf(values.get(1));             // throws IllegalArgumentException
            String title = values.get(2);
            TaskStatus status = TaskStatus.valueOf(values.get(3));       // throws IllegalArgumentException
            String description = values.get(4);

            if (type == TaskType.EPIC) {
                res = new Epic(id, title, description);
            } else if (type == TaskType.SUBTASK) {
                if (values.size() < 6)  throw new WrongCSVLineException("CSV line should have 6 elements to be deserialized to SUBTASK");
                int epicId = Integer.parseInt(values.get(5));            // throws NumberFormatException
                if (epicId < 1)  throw new WrongCSVLineException("EPIC ID field should be > 0, but = " + id);
                res = new Subtask(id, title, description, status, epicId);
            } else {
                res = new Task(id, title, description, status);
            }

        } catch (IllegalArgumentException e) {    // NumberFormatException is subclass of IllegalArgumentException
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
