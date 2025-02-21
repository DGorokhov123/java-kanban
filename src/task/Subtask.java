package task;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Subtask class. Extends Task class. Subtask of Epic task.
 */
public class Subtask extends Task {

    private Epic epic;   // Epic task that this subtask belongs to
    private final int epicId;  // Special field used for linking

    public Subtask(int id, int epicId, String title, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(id, title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    /**
     * This method (and field epicId) is used just for detecting Epic to link.
     * @return {@code int} ID of Epic to link Subtask.
     */
    public int getEpicId() {
        return epicId;
    }

    /**
     * Returns Epic object that this Subtask belongs to
     * @return {@code Epic} linked Epic object
     */
    public Epic getEpic() {
        return epic;
    }

    /**
     * Makes reference between this Subtask and Epic object that this Subtask belongs to.
     * @param epic Epic object to link
     */
    void setEpic(Epic epic) {
        this.epic = epic;
    }

    /**
     * Updates this subtask using data from received subtask.
     * @param task new instance of Task object, containing new data
     */
    @Override
    public boolean update(Task task) {
        super.update(task);
        if (epic != null) {
            epic.calculateStatus();
            epic.calculateTime();
        }
        return true;
    }

    /**
     * Represents the Subtask as a string to write to CSV file.
     */
    @Override
    public String toCSVLine() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"").append(id).append("\",");
        builder.append("\"").append(TaskType.SUBTASK.toString()).append("\",");
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
        builder.append("\",");
        builder.append("\"");
        if (epic != null)  builder.append(epic.getId());
        builder.append("\"");
        return builder.toString();
    }

    /**
     * Represents task as string to print. Usable with System.out.println()
     */
    @Override
    public String toString() {
        String eId = (epic == null) ? "null" : Integer.toString(epic.getId());
        String start = (startTime != null) ? startTime.format(DATE_TIME_FORMATTER) : "null";
        String end = (getEndTime() != null) ? getEndTime().format(DATE_TIME_FORMATTER) : "null";
        return "Subtask #" + id + ", epic #" + eId + "\t[" + status + "]\t"
                + title + " (" + description + ") {" + start + " --> " + end + "}";
    }


}
