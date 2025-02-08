package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Epic task class. Extends Task class. Has subtasks of Subtask type.
 */
public class Epic extends Task {

    private final LinkedHashMap<Integer, Subtask> subtasks = new LinkedHashMap<>();
    private LocalDateTime endTime;

    public Epic(int id, String title, String description) {
        super(id, title, description, TaskStatus.NEW, null, null);
    }

    /**
     * Return HashMap of all the subtasks of this Epic. Key field is ID.
     * @return {@code Map<Integer, Subtask>}
     */
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    /**
     * Calculates status of this Epic basing on subtasks statuses.
     */
    void calculateStatus() {
        boolean isNew = true;
        boolean isDone = true;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStatus() != TaskStatus.NEW) isNew = false;
            if (subtask.getStatus() != TaskStatus.DONE) isDone = false;
        }
        if (isNew) status = TaskStatus.NEW;
        else if (isDone) status = TaskStatus.DONE;
        else status = TaskStatus.IN_PROGRESS;
    }

    /**
     * Calculates startTime, endTime, duration for this Epic basing on subtasks info
     */
    void calculateTime() {
        LocalDateTime firstTime = null;
        LocalDateTime lastTime = null;
        for (Subtask subtask : subtasks.values()) {
            if ( subtask.getStartTime() != null && ( firstTime == null || subtask.getStartTime().isBefore(firstTime) ) )  firstTime = subtask.getStartTime();
            if ( subtask.getEndTime() != null && ( lastTime == null || subtask.getEndTime().isAfter(lastTime) ) )  lastTime = subtask.getEndTime();
        }
        startTime = firstTime;
        endTime = lastTime;
        if (firstTime != null && lastTime != null) duration = Duration.between(firstTime, lastTime);
        else duration = null;
    }

    /**
     * Makes references between this Epic and its Subtask
     * @param subtask Subtask object to link
     * @return {@code true} - normal termination, {@code false} null parameter or already linked
     */
    public boolean linkSubtask(Subtask subtask) {
        if (subtask == null) return false;
        if (subtasks.containsKey(subtask.getId()))  return false;
        subtasks.put(subtask.getId(), subtask);
        subtask.setEpic(this);
        calculateStatus();
        calculateTime();
        return true;
    }

    /**
     * Removes references between this Epic and Subtask with specified ID.
     * @param id ID of existing extended Task type object to unlink.
     * @return {@code true} - normal termination, {@code false} if not found
     */
    public boolean unlinkSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) return false;
        subtask.setEpic(null);
        subtasks.remove(id);
        calculateStatus();
        calculateTime();
        return true;
    }

    /**
     * Updates this Epic using data from received epic.
     * @param task new instance of Epic object, containing new data
     */
    @Override
    public boolean update(Task task) {
        if (task == null || task.getClass() != this.getClass()) return false;
        this.title = task.title;
        this.description = task.description;
        calculateStatus();
        calculateTime();
        return true;
    }

    /**
     * Represents the Epic as a string to write to CSV file.
     */
    @Override
    public String toCSVLine() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"").append(id).append("\",");
        builder.append("\"").append(TaskType.EPIC.toString()).append("\",");
        builder.append("\"").append(title).append("\",");
        builder.append("\"").append(status.toString()).append("\",");
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
        StringBuilder res = new StringBuilder("Epic #" + id + "\t[" + status + "]\t" + title);
        if (!description.isEmpty())  res.append(" (").append(description).append(")");
        String start = (startTime != null) ? startTime.format(DATE_TIME_FORMATTER) : "null";
        String end = (getEndTime() != null) ? getEndTime().format(DATE_TIME_FORMATTER) : "null";
        res.append(" {").append(start).append(" --> ").append(end).append("}");
        return res.toString();
    }

}
