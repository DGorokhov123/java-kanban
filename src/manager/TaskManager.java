package manager;
import task.*;

import java.util.List;

/**
 * Interface for task managers.
 */
public interface TaskManager {

    //#################################### Get methods ####################################

    /**
     * Returns an existing Task (or its child Epic / Subtask) with the specified ID.
     * @param id ID of existing Task object.
     * @return {@code Task} existing Task object, or {@code null} if it doesn't exist.
     */
    Task getTaskById(int id);

    /**
     * Returns a list of all simple tasks (strictly Task objects, not Epic / Subtask).
     * @return {@code ArrayList<Task>} list of existing objects, or {@code null} if list is empty.
     */
    List<Task> getTasks();

    /**
     * Returns a list of all epics (Epic objects).
     * @return {@code ArrayList<Epic>} list of existing objects, or {@code null} if list is empty.
     */
    List<Epic> getEpics();

    /**
     * Returns a list of all subtasks (Subtask objects) of the specified epic.
     * @param epicId ID of the epic.
     * @return {@code ArrayList<Subtask>} list of existing objects,
     */
    List<Subtask> getSubTasks(int epicId);

    /**
     * Returns a list of all tasks and subtasks ordered by start time
     * @return {@code List<Task>} list of existing objects,
     */
    List<Task> getPrioritizedTasks();

    /**
     * Returns list of last viewed tasks
     * @return {@code List<Task>} list of Task objects
     */
    List<Task> getHistory();


    //#################################### Edit methods ####################################

    /**
     * Adds a new simple Task
     * @param task Task data object to add to manager
     * @return {@code Task} new generated task, or {@code null} in case of wrong parameter
     */
    Task add(Task task);

    /**
     * Adds a new Epic
     * @param epic Epic data object to add to manager
     * @return {@code Epic} new generated epic, or {@code null} in case of wrong parameter
     */
    Epic add(Epic epic);

    /**
     * Adds a new Subtask
     * @param subtask Subtask data object to add to manager
     * @return {@code Subtask} new generated subtask, or {@code null} in case of wrong parameter
     */
    Subtask add(Subtask subtask);

    /**
     * Updates an existing Task (or its child Epic / Subtask).
     * @param task New instance of extended Task type object, containing new data for existing entry.
     * @return {@code Task} updated object, or {@code null} in case of wrong ID
     */
    Task update(Task task);


    //#################################### Remove methods. ####################################

    /**
     * Removes an existing Task (or its child Epic / Subtask).
     *
     * @param id ID of existing extended Task type object to remove.
     * @return {@code true} task is removed, {@code false} task is not found
     */
    boolean removeById(int id);

    /**
     * Removes all simple tasks (strictly Task objects, not Epic / Subtask).
     */
    void removeAllTasks();

    /**
     * Removes all subtasks (Subtask objects).
     */
    void removeAllSubtasks();

    /**
     * Removes all epics (Epic objects) and all subtasks (Subtask objects).
     */
    void removeAllEpics();


    //#################################### Date Time methods ####################################

    boolean hasIntersections(Task task);

}
