package manager;

import task.TaskFactory;

import java.io.File;

public class Managers {

    private static TaskManager instance;

    //#################################### InMemory Task Manager ####################################

    /**
     * Returns a new default task manager. Full constructor.
     * @param taskFactory instance of TaskFactory object to inject
     * @param historyManager instance of HistoryManager object to inject
     * @return {@code TaskManager} created object
     */
    public static TaskManager createNewInMemory(TaskFactory taskFactory, HistoryManager historyManager) {
        if (taskFactory == null) throw new IllegalArgumentException("Parameter 'taskFactory' cannot be null");
        if (historyManager == null) throw new IllegalArgumentException("Parameter 'historyManager' cannot be null");
        instance = new InMemoryTaskManager(taskFactory, historyManager);
        return instance;
    }

    /**
     * Returns an existing instance of task manager or creates new with auto generated dependencies.
     * @return {@code TaskManager} TaskManager instance
     */
    public static TaskManager getDefault() {
        if (instance == null) {
            createNewInMemory(getDefaultFactory(), getDefaultHistory());
        }
        return instance;
    }


    //#################################### File Backed Task Manager ####################################

    /**
     * Returns a new File Backed task manager and loads info from file.
     * @param taskFactory instance of TaskFactory object to inject
     * @param historyManager instance of HistoryManager object to inject
     * @param file CSV file to read
     * @return {@code TaskManager} created object
     */
    public static TaskManager createNewFromFile(TaskFactory taskFactory, HistoryManager historyManager, File file) {
        if (taskFactory == null) throw new IllegalArgumentException("Parameter 'taskFactory' cannot be null");
        if (historyManager == null) throw new IllegalArgumentException("Parameter 'historyManager' cannot be null");
        if (file == null) throw new IllegalArgumentException("Parameter 'file' cannot be null");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(taskFactory, historyManager, file);
        if (file.canRead())  taskManager.readFromCSV();
        instance = taskManager;
        return instance;
    }

    /**
     * Returns a new File Backed task manager with auto generated dependencies.
     * @param file CSV file to read
     * @return {@code TaskManager} created object
     */
    public static TaskManager createNewFromFile(File file) {
        if (file == null) throw new IllegalArgumentException("Parameter 'file' cannot be null");
        createNewFromFile(getDefaultFactory(), getDefaultHistory(), file);
        return instance;
    }


    //#################################### History Manager ####################################

    /**
     * Returns a new default history manager with unlimited size
     * @return {@code HistoryManager} created object
     */
    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager(10);
    }

    /**
     * Returns a new default history manager with user-defined size
     * @param historySize {@code int} size of history
     * @return {@code HistoryManager} created object
     */
    public static InMemoryHistoryManager getDefaultHistory(int historySize) {
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
