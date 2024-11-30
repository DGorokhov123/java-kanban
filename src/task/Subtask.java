package task;

/**
 * Subtask class. Extends Task class. Subtask of Epic task.
 */
public class Subtask extends Task {

    private Epic epic;   // Epic task that this subtask belongs to
    private final int epicId;  // Special field used for linking

    /**
     * Updating constructor makes object for update method.
     * @param id
     * @param title
     * @param description
     * @param status
     */
    public Subtask(int id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
        epicId = 0;
    }

    /**
     * Adding constructor makes object for add method.
     * @param title
     * @param description
     * @param status
     * @param epicId
     */
    public Subtask(String title, String description, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }


    /**
     * This method (and field epicId) is used just for detecting Epic to link.
     * Only incoming objects contain this field. All factory-generated objects have epicId = 0.<br>
     * This method is never used in task manager logic except linking Subtask and Epic in add method.
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
     * Package-private method. Is used only in Epic class.
     * @param epic Epic object to link
     */
    void setEpic(Epic epic) {
        this.epic = epic;
    }

    /**
     * Sets status of this subtask. Updates status of owner Epic.
     * @param status new {@code TaskStatus} status to update
     */
    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
        if (epic != null)  epic.setStatus(null);
    }

    /**
     * Represents task as string to print. Usable with System.out.println()
     */
    @Override
    public String toString() {
        String eId = (epic == null) ? "null" : Integer.toString(epic.getId());
        return "Subtask #" + id + ", epic #" + eId + "\t[" + status + "]\t"
                + title + " (" + description + ")";
    }


}
