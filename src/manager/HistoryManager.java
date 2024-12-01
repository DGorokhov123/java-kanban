package manager;

import task.Task;

import java.util.List;

/**
 * Interface for history managers
 */
public interface HistoryManager {

    /**
     * Adds the viewed task to history
     * @param task Task object to add
     * @return {@code true} task added to history, or {@code false} in case of wrong parameter
     */
    boolean add(Task task);

    /**
     * Returns list of last viewed tasks
     * @return {@code List<Task>} list of Task objects
     */
    List<Task> getHistory();

}
