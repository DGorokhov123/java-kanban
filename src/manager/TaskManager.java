package manager;
import task.*;
import java.util.ArrayList;
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
     * or {@code null} if list is empty or specified epic doesn't exist.
     */
    List<Subtask> getSubTasks(int epicId);

    /**
     * Returns list of last viewed tasks
     * @return {@code List<Task>} list of Task objects
     */
    List<Task> getHistory();


    //#################################### Edit methods ####################################

    /**
     * Adds a new simple Task
     * @param task Task object to add
     * @return {@code int} new task ID in case of normal termination, {@code -N} Error code.
     */
    int add(Task task);

    /**
     * Adds a new Epic
     * @param epic Epic object to add
     * @return {@code int} new epic ID in case of normal termination, {@code -N} Error code.
     */
    int add(Epic epic);

    /**
     * Adds a new Subtask
     * @param subtask Subtask object to add
     * @return {@code int} new subtask ID in case of normal termination, {@code -N} Error code.
     */
    int add(Subtask subtask);

    /**
     * Updates an existing Task (or its child Epic / Subtask).
     * @param task New instance of extended Task type object, containing new data for existing entry.
     * @return {@code 0} Normal termination, {@code -N} Error code.
     */
    int update(Task task);


    //#################################### Remove methods. ####################################

    /**
     * Removes an existing Task (or its child Epic / Subtask).
     * @param id ID of existing extended Task type object to remove.
     * @return {@code 0} Normal termination, {@code -N} Error code.
     */
    int removeById(int id);

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


}
