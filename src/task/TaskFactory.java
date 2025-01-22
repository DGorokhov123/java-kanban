package task;

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

    public Task fromCSVArray(String[] array) throws WrongCSVArrayException {
        if (array.length < 5)  throw new WrongCSVArrayException("CSV array should have 5 elements to be deserialized");
        int id;
        Task res;
        try {
            id = Integer.parseInt(array[0]);                    // throws NumberFormatException
            if (id < 1)  throw new WrongCSVArrayException("ID field should be > 0, but = " + id);
            TaskType type = TaskType.valueOf(array[1]);             // throws IllegalArgumentException
            String title = array[2];
            TaskStatus status = TaskStatus.valueOf(array[3]);       // throws IllegalArgumentException
            String description = array[4];

            if (type == TaskType.EPIC) {
                res = new Epic(id, title, description);
            } else if (type == TaskType.SUBTASK) {
                if (array.length < 6)  throw new WrongCSVArrayException("CSV array should have 6 elements to be deserialized to SUBTASK");
                int epicId = Integer.parseInt(array[5]);            // throws NumberFormatException
                if (epicId < 1)  throw new WrongCSVArrayException("EPIC ID field should be > 0, but = " + id);
                res = new Subtask(id, title, description, status, epicId);
            } else {
                res = new Task(id, title, description, status);
            }

        } catch (IllegalArgumentException e) {    // NumberFormatException is subclass of IllegalArgumentException
            StringBuilder errorMessage = new StringBuilder("Incorrect fields of CSV array:");
            for (String el : array)  errorMessage.append(" \"").append(el).append("\",");
            throw new WrongCSVArrayException(errorMessage.toString());
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
