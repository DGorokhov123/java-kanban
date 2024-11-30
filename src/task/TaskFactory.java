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


}
