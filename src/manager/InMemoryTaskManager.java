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
    // так не пойдем, ставьте понятные название переменных: taskFactory, historyManager
    private final TaskFactory taskFactory;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(TaskFactory taskFactory, HistoryManager historyManager) {
        if (taskFactory == null) throw new IllegalArgumentException("Parameter 'taskFactory' cannot be null");
        if (historyManager == null) throw new IllegalArgumentException("Parameter 'historyManager' cannot be null");
        this.taskFactory = taskFactory;
        this.historyManager = historyManager;
    }

    //#################################### Get methods ####################################

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) return null;
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask || task instanceof Epic) continue;
            result.add(task);
        }
        return result;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic epic)  result.add(epic);
        }
        return result;
    }

    @Override
    public List<Subtask> getSubTasks(int epicId) {
        List<Subtask> result = new ArrayList<>();
        Task task = tasks.get(epicId);
        if (task instanceof Epic epic) {
            result.addAll(epic.getSubtasks().values());
        }
        return result;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    //#################################### Edit methods ####################################

    @Override
    public Task add(Task task) {
        if (task == null) return null;
        Task newTask = taskFactory.newTask(task);
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Epic add(Epic epic) {
        if (epic == null) return null;
        Epic newEpic = taskFactory.newEpic(epic);
        tasks.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Subtask add(Subtask subtask) {
        if (subtask == null) return null;
        Task eTask = tasks.get(subtask.getEpicId());
        if (eTask == null) return null;
        if (eTask instanceof Epic epic) {
            Subtask newSubtask = taskFactory.newSubtask(subtask);
            epic.linkSubtask(newSubtask);
            tasks.put(newSubtask.getId(), newSubtask);
            return newSubtask;
        }
        return null;
    }

    // так давайте без этих кодов: -1, -2 и так далее.
    // пусть ваши add/update методы возвращают новый/обновленный объект
    @Override
    public Task update(Task task) {
        if (task == null)  return null;
        if (!tasks.containsKey(task.getId()))  return null;
        Task foundTask = tasks.get(task.getId());
        if (foundTask.update(task))  return foundTask;
        return null;
    }


    //#################################### Remove methods. ####################################

    // тут или void или boolean: true - если удален, false - если такого объекта нет
    @Override
    public boolean removeById(int id) {
        Task task = tasks.get(id);
        if (task == null) return false;
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
        return true;
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
