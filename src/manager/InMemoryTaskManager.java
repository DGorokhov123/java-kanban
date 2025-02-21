package manager;
import task.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Task manager containing all the data in RAM.
 */
public class InMemoryTaskManager implements TaskManager {

    protected final LinkedHashMap<Integer, Task> tasks = new LinkedHashMap<>();
    protected final TaskFactory taskFactory;
    protected final HistoryManager historyManager;
    protected final Set<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime).thenComparing(Task::getId));

    public InMemoryTaskManager(TaskFactory taskFactory, HistoryManager historyManager) {
        if (taskFactory == null) throw new IllegalArgumentException("Parameter 'taskFactory' cannot be null");
        if (historyManager == null) throw new IllegalArgumentException("Parameter 'historyManager' cannot be null");
        this.taskFactory = taskFactory;
        this.historyManager = historyManager;
    }

    //#################################### Get methods ####################################

    @Override
    public Task getTaskById(int id) throws TaskNotFoundException {
        Task task = tasks.get(id);
        if (task == null) throw new TaskNotFoundException("Task #" + id + " not found!");
        historyManager.add(task);
        return task;
    }

    protected List<Task> getAllRecords() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<Task> getTasks() {
        return tasks.values().stream()
                .filter(t -> !(t instanceof Epic) && !(t instanceof Subtask))
                .toList();
    }

    @Override
    public List<Epic> getEpics() {
        return tasks.values().stream()
                .filter(t -> (t instanceof Epic))
                .map(t -> (Epic) t)
                .toList();
    }

    @Override
    public List<Subtask> getSubTasks() {
        return tasks.values().stream()
                .filter(t -> (t instanceof Subtask))
                .map(t -> (Subtask) t)
                .toList();
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        return tasks.values().stream()
                .filter(t -> (t instanceof Subtask))
                .map(t -> (Subtask) t)
                .filter(s -> s.getEpic() != null)
                .filter(s -> s.getEpic().getId() == epicId)
                .toList();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    //#################################### Edit methods ####################################

    @Override
    public Task add(Task task) throws TaskIntersectionException, WrongTaskArgumentException {
        if (task == null) throw new WrongTaskArgumentException("Add error: Task shouldn't be null");
        checkIntersections(task);
        Task newTask = taskFactory.newTask(task);
        tasks.put(newTask.getId(), newTask);
        if (newTask.getStartTime() != null) sortedTasks.add(newTask);
        return newTask;
    }

    @Override
    public Epic add(Epic epic) throws WrongTaskArgumentException {
        if (epic == null) throw new WrongTaskArgumentException("Add error: Epic shouldn't be null");
        Epic newEpic = taskFactory.newEpic(epic);
        tasks.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Subtask add(Subtask subtask) throws TaskIntersectionException, WrongTaskArgumentException, TaskNotFoundException {
        if (subtask == null) throw new WrongTaskArgumentException("Add error: Subtask shouldn't be null");
        Task eTask = tasks.get(subtask.getEpicId());
        if (eTask == null) throw new TaskNotFoundException("Add error: Subtask's Epic not found");
        if (!(eTask instanceof Epic epic)) throw new TaskNotFoundException("Add error: Subtask's Epic type mismatch");
        checkIntersections(subtask);
        Subtask newSubtask = taskFactory.newSubtask(subtask);
        epic.linkSubtask(newSubtask);
        tasks.put(newSubtask.getId(), newSubtask);
        if (newSubtask.getStartTime() != null) sortedTasks.add(newSubtask);
        return newSubtask;
    }

    @Override
    public Task update(Task newTask) throws TaskIntersectionException, WrongTaskArgumentException, TaskNotFoundException {
        if (newTask == null) throw new WrongTaskArgumentException("Update error: Task shouldn't be null");
        Task task = tasks.get(newTask.getId());
        if (task == null) throw new TaskNotFoundException("Update error: Task not found");
        if ((task instanceof Epic) || (task instanceof Subtask))
            throw new WrongTaskArgumentException("Update error: Type mismatch");
        checkIntersections(newTask);
        if (task.getStartTime() != null) sortedTasks.remove(task);
        task.update(newTask);
        if (task.getStartTime() != null) sortedTasks.add(task);
        return task;
    }

    @Override
    public Epic update(Epic newEpic) throws WrongTaskArgumentException, TaskNotFoundException {
        if (newEpic == null) throw new WrongTaskArgumentException("Update error: Epic shouldn't be null");
        Task task = tasks.get(newEpic.getId());
        if (task == null) throw new TaskNotFoundException("Update error: Epic not found");
        if (!(task instanceof Epic epic)) throw new WrongTaskArgumentException("Update error: Type mismatch");
        epic.update(newEpic);
        return epic;
    }

    @Override
    public Subtask update(Subtask newSubtask) throws TaskIntersectionException, WrongTaskArgumentException, TaskNotFoundException {
        if (newSubtask == null) throw new WrongTaskArgumentException("Update error: Subtask shouldn't be null");
        Task task = tasks.get(newSubtask.getId());
        if (task == null) throw new TaskNotFoundException("Update error: Subtask not found");
        if (!(task instanceof Subtask subtask)) throw new WrongTaskArgumentException("Update error: Type mismatch");
        checkIntersections(newSubtask);
        if (task.getStartTime() != null) sortedTasks.remove(subtask);
        subtask.update(newSubtask);
        if (task.getStartTime() != null) sortedTasks.add(subtask);
        return subtask;
    }


    //#################################### Remove methods. ####################################

    @Override
    public void removeById(int id) throws TaskNotFoundException {
        Task task = tasks.get(id);
        if (task == null) {
            throw new TaskNotFoundException("Remove error: Task #" + id + " not found!");
        } else if (task instanceof Epic epic) {
            for (Subtask subtask : epic.getSubtasks().values()) {
                historyManager.remove(subtask.getId());
                if (subtask.getStartTime() != null) sortedTasks.remove(subtask);
                tasks.remove(subtask.getId());
            }
        } else if (task instanceof Subtask subtask) {
            if (subtask.getEpic() != null) subtask.getEpic().unlinkSubtask(id);
            if (task.getStartTime() != null) sortedTasks.remove(task);
        } else {
            if (task.getStartTime() != null) sortedTasks.remove(task);
        }
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeAllTasks() {
        Iterator<Task> taskIterator = tasks.values().iterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            if (task instanceof Epic || task instanceof Subtask) continue;
            historyManager.remove(task.getId());
            taskIterator.remove();
        }
        sortedTasks.clear();
        sortedTasks.addAll(tasks.values().stream()
                .filter(t -> !(t instanceof Epic))
                .filter(t -> t.getStartTime() != null)
                .toList());
    }

    @Override
    public void removeAllSubtasks() {
        Iterator<Task> taskIterator = tasks.values().iterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            if (task instanceof Subtask subtask) {
                if (subtask.getEpic() != null) subtask.getEpic().unlinkSubtask(subtask.getId());
                historyManager.remove(task.getId());
                taskIterator.remove();
            }
        }
        sortedTasks.clear();
        sortedTasks.addAll(tasks.values().stream()
                .filter(t -> !(t instanceof Epic))
                .filter(t -> t.getStartTime() != null)
                .toList());
    }

    @Override
    public void removeAllEpics() {
        Iterator<Task> taskIterator = tasks.values().iterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            if (task instanceof Epic || task instanceof Subtask) {
                historyManager.remove(task.getId());
                taskIterator.remove();
            }
        }
        sortedTasks.clear();
        sortedTasks.addAll(tasks.values().stream()
                .filter(t -> !(t instanceof Epic))
                .filter(t -> t.getStartTime() != null)
                .toList());
    }

    @Override
    public void clearAllData() {
        historyManager.clear();
        sortedTasks.clear();
        tasks.clear();
        taskFactory.clear();
    }

    //#################################### Date Time methods ####################################

    protected void checkIntersections(Task task) throws TaskIntersectionException {
        if (task == null || task.getStartTime() == null) return;
        List<Task> intersected = sortedTasks.stream()
                .filter(t -> t.getStartTime().isBefore(task.getEndTime()) && task.getStartTime().isBefore(t.getEndTime()))
                .filter(t -> !t.equals(task))
                .toList();
        if (intersected.isEmpty()) return;
        String toMsg = intersected.stream().map(t -> String.valueOf(t.getId())).collect(Collectors.joining(", "));
        throw new TaskIntersectionException("Task \"" + task.getTitle()
                + "\" has intersections with other tasks: " + toMsg);
    }

}
