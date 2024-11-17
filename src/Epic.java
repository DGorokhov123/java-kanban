import java.util.HashMap;

/**
 * Далее в таких комментах - цитаты из ТЗ
 * Большая задача, которая делится на подзадачи, называется эпиком (англ. epic).
 * Для эталонного решения мы выбрали создание публичного не абстрактного класса Task,
 * который представляет отдельно стоящую задачу. Его данные наследуют подклассы Subtask и Epic.
 * Для подклассов Subtask и Epic наследуем сразу имплементацию, поскольку нам понадобится такое расширение
 * функциональности, которое совместимо с базовым классом и не отличается от него по поведению.
 */
public class Epic extends Task{
    /**
     * Каждый эпик знает, какие подзадачи в него входят.
     */
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(int id, String title, String description) {
        super(id, title, description);
    }

    public void addSubtask(Subtask task) {
        subtasks.put(task.getId(), task);
        task.setEpic(this);
        calculateStatus();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    /**
     * если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
     * если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
     * во всех остальных случаях статус должен быть IN_PROGRESS.
     * Завершение всех подзадач эпика считается завершением эпика.
     */
    public void calculateStatus() {
        if (subtasks.isEmpty()) {
            status = TaskStatus.NEW;
            return;
        }
        boolean isNew = true;
        boolean isDone = true;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStatus() != TaskStatus.NEW) isNew = false;
            if (subtask.getStatus() != TaskStatus.DONE) isDone = false;
        }
        if (isNew) status = TaskStatus.NEW;
        else if (isDone) status = TaskStatus.DONE;
        else status = TaskStatus.IN_PROGRESS;
    }

    /**
     * Из описания задачи видно, что эпик не управляет своим статусом самостоятельно. Это значит:
     * 1) Пользователь не должен иметь возможности поменять статус эпика самостоятельно.
     */
    @Override
    public void setStatus(TaskStatus status) {
        return;
    }

    public void removeSubtaskById(int remId) {
        subtasks.get(remId).setEpic(null);
        subtasks.remove(remId);
        calculateStatus();
    }

    /**
     * Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Эпик #" + id + "\t[" + status + "]\t" + title);
        if (!description.isEmpty())  res.append(" (").append(description).append(")");
        for (Subtask subtask : subtasks.values()) {
            res.append("\n").append(subtask.toString());
        }
        return res.toString();
    }
}
