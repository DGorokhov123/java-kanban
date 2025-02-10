package manager;

import task.*;
import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(TaskFactory taskFactory, HistoryManager historyManager, File file) {
        super(taskFactory, historyManager);
        this.file = file;
    }

    private void save() {
        String header = "\"id\",\"type\",\"title\",\"status\",\"description\",\"starttime\",\"duration\",\"epic\"";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(header + "\n");
            for (Task task : tasks.values())  writer.write(task.toCSVLine() + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException("File access error: can't write to " + file.toString());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        TaskFactory taskFactory = new TaskFactory();
        HistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(taskFactory, historyManager, file);
        if (file.canRead()) taskManager.readFromCSV();
        return taskManager;
    }

    public void readFromCSV() {
        taskFactory.clear();
        historyManager.clear();
        tasks.clear();
        sortedTasks.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();                        // header
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isBlank()) continue;
                Task task = taskFactory.fromCSVLine(line);
                tasks.put(task.getId(), task);
                if (task instanceof Subtask subtask) {
                    Task subtaskEpic = tasks.get(subtask.getEpicId());
                    if (subtaskEpic instanceof Epic epic) {
                        epic.linkSubtask(subtask);
                    }
                }
            }
            sortedTasks.addAll(tasks.values().stream()
                    .filter(t -> !(t instanceof Epic))
                    .filter(t -> t.getStartTime() != null)
                    .toList());
        } catch (IOException e) {
            throw new ManagerLoadException("IO Error reading file " + file.toString());
        } catch (WrongCSVLineException e) {
            throw new ManagerLoadException("Incorrect CSV file: " + e.getMessage());
        }
    }

    @Override
    public Task add(Task task) throws TaskIntersectionException {
        Task returnedTask = super.add(task);
        save();
        return returnedTask;
    }

    @Override
    public Epic add(Epic epic) {
        Epic returnedEpic = super.add(epic);
        save();
        return returnedEpic;
    }

    @Override
    public Subtask add(Subtask subtask) throws TaskIntersectionException {
        Subtask returnedSubtask = super.add(subtask);
        save();
        return returnedSubtask;
    }

    @Override
    public Task update(Task task) throws TaskIntersectionException {
        Task updatedTask = super.update(task);
        save();
        return updatedTask;
    }

    @Override
    public boolean removeById(int id) {
        boolean removeResult = super.removeById(id);
        save();
        return removeResult;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }
}
