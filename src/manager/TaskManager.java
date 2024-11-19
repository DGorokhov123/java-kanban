package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Далее в таких комментах - цитаты из ТЗ
 * Кроме классов для описания задач, вам нужно реализовать класс для объекта-менеджера.
 * Он будет запускаться на старте программы и управлять всеми задачами.
 * Фраза «информация приходит вместе с информацией по задаче» означает, что не существует отдельного метода,
 * который занимался бы только обновлением статуса задачи.
 * Вместо этого статус задачи обновляется вместе с полным обновлением задачи.
 */
public class TaskManager {
    /**
     * Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
     * Один из способов организовать такое хранение — это присвоить соответствие между идентификатором и задачей
     * при помощи HashMap. Поскольку идентификатор не может повторяться (иначе он не был бы идентификатором),
     * такой подход позволит быстро получать задачу.
     */
    private final HashMap<Integer, Task> tasks = new HashMap<>();

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
     * UPDATE 1
     * надо возвращать ArrayList: public ArrayList<Task> getTasks()
     */
    // возвращает список задач 1 уровня - таски и эпики
    public ArrayList<Task> getTasks() {
        if (tasks.isEmpty()) return null;
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) continue;
            result.add(task);
        }
        return result;
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     * a. Получение списка всех задач.
     * UPDATE 1
     * нужен метод getEpics
     */
    public ArrayList<Epic> getEpics() {
        if (tasks.isEmpty()) return null;
        ArrayList<Epic> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic epic)  result.add(epic);
        }
        return result;
    }

    /**
     * Дополнительные методы:
     * a. Получение списка всех подзадач определённого эпика.
     * UPDATE 1
     * надо возвращать ArrayList: public ArrayList<Subtask> getSubTasks()
     */
    // хотя мне представляется что фронту удобнее было бы оперировать HashMap
    public ArrayList<Subtask> getSubTasks(int epicId) {
        Task eTask = tasks.get(epicId);
        if (eTask == null) return null;
        if (eTask instanceof Epic epic) return new ArrayList<>(epic.getSubtasks().values());
        return null;
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     *  d. Создание. Сам объект должен передаваться в качестве параметра.
     *  UPDATE 1
     *  метод updateTask(Task task) не подойдет для использования в методе addTask()
     */
    public void addTask(Task task) {
        if (task == null)  return;
        if (tasks.containsKey(task.getId()))  return;
        if (task instanceof Epic epic) {
            tasks.put(task.getId(), task);
            for (Subtask subtask : epic.getSubtasks().values()) {
                if (!tasks.containsKey(subtask.getId()))  tasks.put(subtask.getId(), subtask);
            }
        } else if (task instanceof Subtask subtask) {
            if (subtask.getEpic() == null)  return;
            if (!tasks.containsKey(subtask.getEpic().getId()))  return;
            tasks.put(task.getId(), task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     *  e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
     *  При обновлении данных можете считать, что на вход подаётся новый объект, который должен полностью заменить
     *  старый. К примеру, метод для обновления эпика может принимать эпик в качестве входных данных
     *  public void updateTask(task.Task task). Если вы храните эпики в HashMap, где ключами являются идентификаторы, то
     *  обновление — это запись нового эпика tasks.put(task.getId(), task)).
     *  UPDATE 1
     *  тут бы еще проверить есть ли такой id в map, и обновлять если есть.
     */
    // тут на вебинаре внезапно выяснилось, что фронт будет присылать обновления в виде совершенно нового объекта с
    // таким же id и если его просто класть в мап с помощью put, то будут рассогласования со ссылками в эпиках
    // и сабтасках. тут можно было бы циклами обойти всю структуру и поменять ссылки, но мне подумалось что
    // проще сделать метод копирования данных в самом таске, а структуру ссылок не трогать вообще.
    public void updateTask(Task task) {
        if (task == null)  return;
        if (!tasks.containsKey(task.getId()))  return;
        tasks.get(task.getId()).update(task);
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     *  b. Удаление всех задач.
     *  - Удаляет все Task
     */
    // Q: такие уж слабые?
    // за клир: не требует дополнительной памяти, гарбидж коллектору легче вычищать много мелких объектов,
    //          сохраняются ссылки на объект, сохраняются настройки если есть
    // за нью:  сложность O(1) против O(n) у клира, очищает сразу много памяти если таблица большая
    public void removeAllTasks() {
        Iterator<Task> taskIterator = tasks.values().iterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            if (task instanceof Epic) continue;
            if (task instanceof Subtask) continue;
            taskIterator.remove();
        }
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     *  b. Удаление всех задач.
     *  - Удаляет сабтаски и сабтаски внутри эпиков
     */
    public void removeAllSubtasks() {
        Iterator<Task> taskIterator = tasks.values().iterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            if (task instanceof Subtask subtask) {
                if (subtask.getEpic() != null) subtask.getEpic().removeSubtaskById(subtask.getId());
                taskIterator.remove();
            }
        }
    }

    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     *  b. Удаление всех задач.
     *  - Удаляет все эпики и следовательно все их сабтаски
     */
    public void removeAllEpics() {
        Iterator<Task> taskIterator = tasks.values().iterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            if (task instanceof Epic) {
                taskIterator.remove();
            } else if (task instanceof Subtask subtask) {
                if (subtask.getEpic() != null)  taskIterator.remove();
            }
        }
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
