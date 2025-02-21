package manager;

import task.Epic;
import task.Subtask;
import task.Task;

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
    Task getTaskById(int id) throws TaskNotFoundException;

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
     * Returns a list of all subtasks (Subtask objects)
     * @return {@code ArrayList<Subtask>} list of existing objects,
     */
    List<Subtask> getSubTasks();

    /**
     * Returns a list of all subtasks (Subtask objects) of the specified epic.
     * @param epicId ID of the epic.
     * @return {@code ArrayList<Subtask>} list of existing objects,
     */
    List<Subtask> getEpicSubtasks(int epicId);

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
    Task add(Task task) throws TaskIntersectionException, WrongTaskArgumentException;

    /**
     * Adds a new Epic
     * @param epic Epic data object to add to manager
     * @return {@code Epic} new generated epic, or {@code null} in case of wrong parameter
     */
    Epic add(Epic epic) throws WrongTaskArgumentException;

    /**
     * Adds a new Subtask
     * @param subtask Subtask data object to add to manager
     * @return {@code Subtask} new generated subtask, or {@code null} in case of wrong parameter
     */
    Subtask add(Subtask subtask) throws TaskIntersectionException, WrongTaskArgumentException, TaskNotFoundException;

    /**
     * Updates an existing Task
     * @param task New instance of Task object, containing new data for existing entry.
     * @return {@code Task} updated object, or {@code null} in case of wrong ID
     */
    Task update(Task task) throws TaskIntersectionException, WrongTaskArgumentException, TaskNotFoundException;

    /**
     * Updates an existing Epic
     * @param epic New instance of Epic object, containing new data for existing entry.
     * @return {@code Epic} updated object, or {@code null} in case of wrong ID
     */
    Epic update(Epic epic) throws WrongTaskArgumentException, TaskNotFoundException;

    /**
     * Updates an existing Subtask
     * @param subtask New instance of Subtask object, containing new data for existing entry.
     * @return {@code Subtask} updated object, or {@code null} in case of wrong ID
     */
    Subtask update(Subtask subtask) throws TaskIntersectionException, WrongTaskArgumentException, TaskNotFoundException;


    //#################################### Remove methods. ####################################

    /**
     * Removes an existing Task (or its child Epic / Subtask).
     *
     * @param id ID of existing extended Task type object to remove.
     */
    void removeById(int id) throws TaskNotFoundException;

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

    void clearAllData();
}
