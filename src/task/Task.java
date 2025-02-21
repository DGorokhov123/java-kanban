package task;
import com.google.gson.annotations.Expose;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Simple task class. Standalone ungrouped tasks without subtasks.
 */
public class Task {

    @Expose
    protected final String type = getClass().getSimpleName();
    @Expose
    protected final int id;
    @Expose
    protected String title;
    @Expose
    protected String description;
    @Expose
    protected TaskStatus status;
    @Expose
    protected LocalDateTime startTime;
    @Expose
    protected Duration duration;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(int id, String title, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.title = (title != null) ? title : "";
        this.description = (description != null) ? description : "";
        this.status = (status != null) ? status : TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null)  return null;
        if (duration == null)  return startTime;
        return startTime.plus(duration);
    }

    public void setTiming(LocalDateTime startTime, Duration duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setTiming(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.duration = (startTime != null && endTime != null) ? Duration.between(startTime, endTime) : null;
    }

    /**
     * Updates this task using data from received task.
     * @param task new instance of Task object, containing new data
     */
    public boolean update(Task task) {
        if (task == null || task.getClass() != this.getClass()) return false;
        this.title = task.title;
        this.description = task.description;
        this.status = task.status;
        this.startTime = task.startTime;
        this.duration = task.duration;
        return true;
    }

    /**
     * Represents the task as a string to write to CSV file.
     */
    public String toCSVLine() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"").append(id).append("\",");
        builder.append("\"").append(TaskType.TASK).append("\",");
        builder.append("\"").append(title).append("\",");
        builder.append("\"");
        if (status != null) {
            builder.append(status.toString());
        } else {
            builder.append("NEW");
        }
        builder.append("\",");
        builder.append("\"").append(description).append("\",");
        builder.append("\"");
        if (startTime != null)  builder.append(startTime.format(DATE_TIME_FORMATTER));
        builder.append("\",");
        builder.append("\"");
        if (duration != null)  builder.append(duration.toSeconds());
        builder.append("\"");
        return builder.toString();
    }

    /**
     * Represents task as string to print. Usable with System.out.println()
     */
    @Override
    public String toString() {
        String start = (startTime != null) ? startTime.format(DATE_TIME_FORMATTER) : "null";
        String end = (getEndTime() != null) ? getEndTime().format(DATE_TIME_FORMATTER) : "null";
        return "Task #" + id + " [" + status + "] " + title + " (" + description + ") {" + start + " --> " + end + "}";
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
