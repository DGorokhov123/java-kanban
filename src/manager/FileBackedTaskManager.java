package manager;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import task.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(TaskFactory taskFactory, HistoryManager historyManager, File file) {
        super(taskFactory, historyManager);
        this.file = file;
    }

    private void save() {
        String[] header = { "id", "type", "title", "status", "description", "epic" };
        try ( CSVWriter writer = new CSVWriter(new FileWriter(file)) ) {
            writer.writeNext(header);
            for (Task task : tasks.values())  writer.writeNext(task.toCSVArray());
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

        try ( CSVReader reader = new CSVReader(new FileReader(file)) ) {
            List<String[]> lines = reader.readAll();
            if (!lines.isEmpty())  lines.removeFirst();
            for (String[] line : lines) {
                Task task = taskFactory.fromCSVArray(line);
                tasks.put(task.getId(), task);
                if (task instanceof Subtask subtask) {
                    Task subtaskEpic = tasks.get(subtask.getEpicId());
                    if (subtaskEpic instanceof Epic epic) {
                        epic.linkSubtask(subtask);
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerLoadException("IO Error reading file " + file.toString());
        } catch (WrongCSVArrayException e) {
            throw new ManagerLoadException("Wrong CSV line: " + e.getMessage());
        } catch (CsvException e) {
            throw new ManagerLoadException("Wrong CSV file. Incorrect formatting.");
        }
    }

    @Override
    public Task add(Task task) {
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
    public Subtask add(Subtask subtask) {
        Subtask returnedSubtask = super.add(subtask);
        save();
        return returnedSubtask;
    }

    @Override
    public Task update(Task task) {
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
