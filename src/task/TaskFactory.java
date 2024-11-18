package task;

/**
 * Далее в таких комментах - цитаты из ТЗ
 */
public class TaskFactory {
    /**
     * В трекере у каждого типа задач есть идентификатор. Это целое число, уникальное для всех типов задач.
     * По нему находят, обновляют, удаляют задачи. При создании задачи менеджер присваивает ей новый идентификатор.
     * Для генерации идентификаторов можно использовать числовое поле-счётчик внутри класса manager.TaskManager,
     * увеличивая его на 1, когда нужно получить новое значение.
     */
    private int taskCounter = 0;

    public void setTaskCounter(int taskCounter) {
        this.taskCounter = taskCounter;
    }

    /*
    New task.Task Generator
     */
    public Task newTask() {
        return newTask("New task", "");
    }

    public Task newTask(String title) {
        return newTask(title, "");
    }

    public Task newTask(String title, String description) {
        taskCounter++;
        return new Task(taskCounter, title, description);
    }


    /*
    New task.Epic Generator
     */
    public Epic newEpic() {
        return newEpic("New epic", "");
    }

    public Epic newEpic(String title) {
        return newEpic(title, "");
    }

    public Epic newEpic(String title, String description) {
        taskCounter++;
        return new Epic(taskCounter, title, description);
    }


    /*
    New task.Subtask Generator
     */
    public Subtask newSubtask() {
        return newSubtask("New subtask", "");
    }

    public Subtask newSubtask(String title) {
        return newSubtask(title, "");
    }

    public Subtask newSubtask(String title, String description) {
        taskCounter++;
        return new Subtask(taskCounter, title, description);
    }


}
