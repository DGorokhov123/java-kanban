package manager;

import task.TaskFactory;

import java.io.File;

public class Managers {

    //#################################### InMemory Task Manager ####################################

    /**
     * Returns a new default task manager. Full constructor.
     * @param taskFactory instance of TaskFactory object to inject
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


    //#################################### File Backed Task Manager ####################################

    /**
     * Returns a new File Backed task manager. Full constructor.
     * @param taskFactory instance of TaskFactory object to inject
     * @param historyManager instance of HistoryManager object to inject
     * @param file CSV file to read
     * @return {@code TaskManager} created object
     */
    public static TaskManager loadFromFile(TaskFactory taskFactory, HistoryManager historyManager, File file) {
        if (taskFactory == null) taskFactory = getDefaultFactory();
        if (historyManager == null) historyManager = getDefaultHistory();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(taskFactory, historyManager, file);
        if (file.canRead())  taskManager.readFromCSV();
        return taskManager;
    }

    /**
     * Returns a new File Backed task manager with auto generated dependencies.
     * @param file CSV file to read
     * @return {@code TaskManager} created object
     */
    public static TaskManager loadFromFile(File file) {
        return loadFromFile(null, null, file);
    }


    //#################################### History Manager ####################################

    /**
     * Returns a new default history manager with unlimited size
     * @return {@code HistoryManager} created object
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    /**
     * Returns a new default history manager with user-defined size
     * @param historySize {@code int} size of history
     * @return {@code HistoryManager} created object
     */
    public static HistoryManager getDefaultHistory(int historySize) {
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
