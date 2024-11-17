import java.util.HashMap;

/**
 * Далее в таких комментах - цитаты из ТЗ
 * Кроме классов для описания задач, вам нужно реализовать класс для объекта-менеджера.
 * Он будет запускаться на старте программы и управлять всеми задачами.
 * Фраза «информация приходит вместе с информацией по задаче» означает, что не существует отдельного метода,
 * который занимался бы только обновлением статуса задачи.
 * Вместо этого статус задачи обновляется вместе с полным обновлением задачи.
 */
public class TaskManager {
    // для возможности обнуления счетчика при удалении всех задач
    private final TaskFactory taskFactory;
    /**
     * Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
     * Один из способов организовать такое хранение — это присвоить соответствие между идентификатором и задачей
     * при помощи HashMap. Поскольку идентификатор не может повторяться (иначе он не был бы идентификатором),
     * такой подход позволит быстро получать задачу.
     */
    private HashMap<Integer, Task> tasks = new HashMap<>();

    public TaskManager(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     *  c. Получение по идентификатору.
     */
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     * a. Получение списка всех задач.
     */
    // возвращает список задач 1 уровня - таски и эпики
    public HashMap<Integer, Task> getTasks() {
        if (tasks.isEmpty()) return null;
        HashMap<Integer, Task> result = new HashMap<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) continue;
            result.put(task.getId(), task);
        }
        return result;
    }

    /**
     * Дополнительные методы:
     * a. Получение списка всех подзадач определённого эпика.
     */
    // а вот и функция для сабтасков
    public HashMap<Integer, Subtask> getSubTasks(int epicId) {
        Task eTask = tasks.get(epicId);
        if (eTask == null) return null;
        if (eTask instanceof Epic epic) return epic.getSubtasks();
        return null;
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     *  d. Создание. Сам объект должен передаваться в качестве параметра.
     */
    public void addTask(Task task) {
        if (task instanceof Epic epic) {
            updateTask(task);
            for (Subtask subtask : epic.getSubtasks().values()) {
                updateTask(subtask);
            }
        } else if (task instanceof Subtask subtask) {
            if (subtask.getEpic() == null)  return;
            if (!tasks.containsKey(subtask.getEpic().getId()))  return;
            updateTask(task);
        } else {
            updateTask(task);
        }
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     *  e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
     *  При обновлении данных можете считать, что на вход подаётся новый объект, который должен полностью заменить
     *  старый. К примеру, метод для обновления эпика может принимать эпик в качестве входных данных
     *  public void updateTask(Task task). Если вы храните эпики в HashMap, где ключами являются идентификаторы, то
     *  обновление — это запись нового эпика tasks.put(task.getId(), task)).
     */
    // не могу себе представить сценария, где бы эта функция могла использоваться, кроме собственно,
    // предыдущей функции addTask, где ее можно заменить на tasks.put(task.getId(), task);
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     *  b. Удаление всех задач.
     */
    public void removeAllTasks() {
        taskFactory.setTaskCounter(0);
        tasks = new HashMap<>();
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     *  f. Удаление по идентификатору.
     */
    public void removeTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) return;
        if (task instanceof Epic epic) {
            for (Subtask subtask : epic.getSubtasks().values()) {
                tasks.remove(subtask.getId());
            }
            tasks.remove(id);
        } else if (task instanceof Subtask subtask) {
            if (subtask.getEpic() != null)  subtask.getEpic().removeSubtaskById(id);
            tasks.remove(id);
        } else {
            tasks.remove(id);
        }
    }


}
