package manager;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) return null;
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
    public List<Subtask> getSubTasks(int epicId) {
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
    public Task add(Task task) {
        if (task == null) return null;
        if (hasIntersections(task)) reArrangeTask(task);
        Task newTask = taskFactory.newTask(task);
        tasks.put(newTask.getId(), newTask);
        if (newTask.getStartTime() != null) sortedTasks.add(newTask);
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
        if (hasIntersections(subtask)) reArrangeTask(subtask);
        Task eTask = tasks.get(subtask.getEpicId());
        if (eTask == null) return null;
        if (eTask instanceof Epic epic) {
            Subtask newSubtask = taskFactory.newSubtask(subtask);
            epic.linkSubtask(newSubtask);
            tasks.put(newSubtask.getId(), newSubtask);
            if (newSubtask.getStartTime() != null) sortedTasks.add(newSubtask);
            return newSubtask;
        }
        return null;
    }

    @Override
    public Task update(Task task) {
        if (task == null)  return null;
        if (hasIntersections(task)) reArrangeTask(task);
        if (!tasks.containsKey(task.getId()))  return null;
        Task foundTask = tasks.get(task.getId());
        if (foundTask instanceof Epic) {
            foundTask.update(task);
        } else {
            sortedTasks.remove(foundTask);
            foundTask.update(task);
            if (foundTask.getStartTime() != null) sortedTasks.add(foundTask);
        }
        return foundTask;
    }


    //#################################### Remove methods. ####################################

    @Override
    public boolean removeById(int id) {
        Task task = tasks.get(id);
        if (task == null) return false;
        if (task instanceof Epic epic) {
            for (Subtask subtask : epic.getSubtasks().values()) {
                historyManager.remove(subtask.getId());
                sortedTasks.remove(subtask);
                tasks.remove(subtask.getId());
            }
        } else if (task instanceof Subtask subtask) {
            if (subtask.getEpic() != null)  subtask.getEpic().unlinkSubtask(id);
            sortedTasks.remove(task);
        } else {
            sortedTasks.remove(task);
        }
        historyManager.remove(id);
        tasks.remove(id);
        return true;
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

    //#################################### Date Time methods ####################################

    @Override
    public boolean hasIntersections(Task task) {
        if (task == null || task.getStartTime() == null) return false;
        return sortedTasks.stream()
          .anyMatch(t -> t.getStartTime().isBefore(task.getEndTime()) && task.getStartTime().isBefore(t.getEndTime()));
    }

    protected void reArrangeTask(Task task) {
        if (task == null || task.getStartTime() == null) return;
        List<Task> intersections;
        LocalDateTime start, end;
        int dayShift = 0;
        int minuteShift = 0;

        while (true) {
            LocalDateTime finalStart = start = task.getStartTime().plusDays(dayShift).plusMinutes(minuteShift);
            LocalDateTime finalEnd = end = task.getEndTime().plusDays(dayShift).plusMinutes(minuteShift);
            intersections = sortedTasks.stream()
                    .filter(t -> t.getStartTime().isBefore(finalEnd) && finalStart.isBefore(t.getEndTime()))
                    .toList();
            if (intersections.isEmpty()) {
                task.setTiming(start, end);
                return;
            }
            if (Duration.between(start, end).toHours() > 12) {
                LocalDateTime lastPoint = intersections.stream()
                        .map(Task::getEndTime)
                        .max(Comparator.naturalOrder())
                        .get();
                dayShift += (int) Duration.between(start, lastPoint).toDays() + 1;
                continue;
            }
            boolean ok = false;
            while (end.getHour() < 21) {
                minuteShift += 15;
                LocalDateTime finalStart2 = start = start.plusMinutes(15);
                LocalDateTime finalEnd2 = end = end.plusMinutes(15);
                ok = intersections.stream()
                        .noneMatch(t -> t.getStartTime().isBefore(finalEnd2) && finalStart2.isBefore(t.getEndTime()));
                if (ok) break;
            }
            if (ok) continue;
            minuteShift = 0;
            dayShift++;
        }
    }


}
