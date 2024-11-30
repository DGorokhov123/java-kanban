package manager;

import task.TaskFactory;

public class Managers {

    //#################################### Task Manager ####################################

    /**
     * Returns a new default task manager. Full constructor.
     * @param historyManager instance of HistoryManager object to inject
     * @return {@code TaskManager} created object
     */
    public static TaskManager getDefault(TaskFactory taskFactory, HistoryManager historyManager) {
        if (taskFactory == null) taskFactory = getDefaultFactory();
        if (historyManager == null) historyManager = getDefaultHistory();
        return new InMemoryTaskManager(taskFactory, historyManager);
    }

    /**
     * Returns a new default task manager with auto generated dependencies.
     * @return {@code TaskManager} created object
     */
    public static TaskManager getDefault() {
        return getDefault(null, null);
    }


    //#################################### History Manager ####################################

    /**
     * Returns a new default history manager with size of 10 items
     * @return {@code HistoryManager} created object
     */
    public static HistoryManager getDefaultHistory() {
        return getDefaultHistory(10);
    }

    /**
     * Returns a new default history manager with user-defined size
     * @param historySize {@code int} size of history
     * @return {@code HistoryManager} created object
     */
    public static HistoryManager getDefaultHistory(int historySize) {
        if (historySize < 1) historySize = 10;
        return new InMemoryHistoryManager(historySize);
    }


    //#################################### Task Factory ####################################

    /**
     * Returns a new default task factory object
     * @return {@code TaskFactory} created object
     */
    public static TaskFactory getDefaultFactory() {
        return new TaskFactory();
    }


}
