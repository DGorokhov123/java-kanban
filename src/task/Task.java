package task;
import java.util.Objects;

/**
 * Simple task class. Standalone ungrouped tasks without subtasks.
 */
public class Task {

    protected final int id;
    protected String title;
    protected String description;
    protected TaskStatus status;

    /**
     * Updating constructor makes object for update method.
     * @param id
     * @param title
     * @param description
     * @param status
     */
    public Task(int id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    /**
     * Adding constructor makes object for add method.
     * @param title
     * @param description
     * @param status
     */
    public Task(String title, String description, TaskStatus status) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * Updates this task using data from received task.
     * @param task new instance of Task object, containing new data
     */
    public boolean update(Task task) {
        if (task == null) return false;
        if (task.getClass() != this.getClass()) return false;
        setTitle(task.getTitle());
        setDescription(task.getDescription());
        setStatus(task.getStatus());
        return true;
    }

    /**
     * Represents task as string to print. Usable with System.out.println()
     */
    @Override
    public String toString() {
        return "Task #" + id + "\t[" + status + "]\t" + title + " (" + description + ")";
    }

    /**
     * Compares two tasks. They should be equal only if they have the same ID.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    /**
     * Two tasks are equal if they have the same ID. Hashcode depends only on ID value.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
