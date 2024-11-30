package manager;
import task.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Task manager containing all the data in RAM.
 */
public class InMemoryTaskManager implements TaskManager {

    private final LinkedHashMap<Integer, Task> tasks = new LinkedHashMap<>();
    private final TaskFactory tFac;
    private final HistoryManager hMan;

    public InMemoryTaskManager(TaskFactory taskFactory, HistoryManager historyManager) {
        if (taskFactory == null) throw new IllegalArgumentException("Parameter 'taskFactory' cannot be null");
        if (historyManager == null) throw new IllegalArgumentException("Parameter 'historyManager' cannot be null");
        this.tFac = taskFactory;
        this.hMan = historyManager;
    }

    //#################################### Get methods ####################################

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) return null;
        hMan.add(task);
        return task;
    }

    @Override
    public List<Task> getTasks() {
        if (tasks.isEmpty()) return null;
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask || task instanceof Epic) continue;
            result.add(task);
        }
        if (result.isEmpty()) return null;
        return result;
    }

    @Override
    public List<Epic> getEpics() {
        if (tasks.isEmpty()) return null;
        ArrayList<Epic> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic epic)  result.add(epic);
        }
        if (result.isEmpty()) return null;
        return result;
    }

    @Override
    public List<Subtask> getSubTasks(int epicId) {
        Task task = tasks.get(epicId);
        if (task == null) return null;
        if (task instanceof Epic epic) {
            if (epic.getSubtasks().isEmpty()) return null;
            return new ArrayList<>(epic.getSubtasks().values());
        }
        return null;
    }

    public List<Task> getHistory() {
        return hMan.getHistory();
    }


    //#################################### Edit methods ####################################

    @Override
    public int add(Task task) {
        if (task == null) return -1;
        Task newTask = tFac.newTask(task);
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    @Override
    public int add(Epic epic) {
        if (epic == null) return -1;
        Epic newEpic = tFac.newEpic(epic);
        tasks.put(newEpic.getId(), newEpic);
        return newEpic.getId();
    }

    @Override
    public int add(Subtask subtask) {
        if (subtask == null) return -1;
        Task eTask = tasks.get(subtask.getEpicId());
        if (eTask == null) return -2;
        if (eTask instanceof Epic epic) {
            Subtask newSubtask = tFac.newSubtask(subtask);
            epic.linkSubtask(newSubtask);
            tasks.put(newSubtask.getId(), newSubtask);
            return newSubtask.getId();
        }
        return -3;
    }

    @Override
    public int update(Task task) {
        if (task == null)  return -1;
        if (!tasks.containsKey(task.getId()))  return -2;
        return tasks.get(task.getId()).update(task);
    }


    //#################################### Remove methods. ####################################

    @Override
    public int removeById(int id) {
        Task task = tasks.get(id);
        if (task == null) return -1;
        if (task instanceof Epic epic) {
            for (Subtask subtask : epic.getSubtasks().values()) {
                tasks.remove(subtask.getId());
            }
            tasks.remove(id);
        } else if (task instanceof Subtask subtask) {
            if (subtask.getEpic() != null)  subtask.getEpic().unlinkSubtask(id);
            tasks.remove(id);
        } else {
            tasks.remove(id);
        }
        return 0;
    }

    @Override
    public void removeAllTasks() {
        Iterator<Task> taskIterator = tasks.values().iterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            if (task instanceof Epic || task instanceof Subtask) continue;
            taskIterator.remove();
        }
    }

    @Override
    public void removeAllSubtasks() {
        Iterator<Task> taskIterator = tasks.values().iterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            if (task instanceof Subtask subtask) {
                if (subtask.getEpic() != null) subtask.getEpic().unlinkSubtask(subtask.getId());
                taskIterator.remove();
            }
        }
    }

    @Override
    public void removeAllEpics() {
        Iterator<Task> taskIterator = tasks.values().iterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            if (task instanceof Epic || task instanceof Subtask)  taskIterator.remove();
        }
    }


}
