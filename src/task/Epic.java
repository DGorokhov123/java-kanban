package task;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Epic task class. Extends Task class. Has subtasks of Subtask type.
 */
public class Epic extends Task {

    private final LinkedHashMap<Integer, Subtask> subtasks = new LinkedHashMap<>();

    /**
     * Updating constructor makes object for update method.
     * @param id
     * @param title
     * @param description
     */
    public Epic(int id, String title, String description) {
        super(id, title, description, TaskStatus.NEW);
    }

    /**
     * Adding constructor makes object for add method.
     * @param title
     * @param description
     */
    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
    }

    /**
     * Return HashMap of all the subtasks of this Epic. Key field is ID.
     * @return {@code Map<Integer, Subtask>}
     */
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    /**
     * Sets status of this Epic by calculation based on subtasks statuses.
     */
    @Override
    public void setStatus(TaskStatus status) {
        calculateStatus();
    }

    /**
     * Calculates status of this Epic basing on subtasks statuses.
     */
    private void calculateStatus() {
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
        return true;
    }

    /**
     * Represents task as string to print. Usable with System.out.println()
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Epic #" + id + "\t[" + status + "]\t" + title);
        if (!description.isEmpty())  res.append(" (").append(description).append(")");
        return res.toString();
    }

}
