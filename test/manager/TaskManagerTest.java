package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    @Test
    void allGetMethods() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 9, 0), Duration.ofHours(24)));
        Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
        taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
        assertEquals("1", taskManager.getTaskById(1).getDescription());
        assertEquals("2", taskManager.getTaskById(2).getDescription());
        assertEquals("3", taskManager.getTaskById(3).getDescription());
        assertEquals("4", taskManager.getTaskById(4).getDescription());
        assertEquals("5", taskManager.getTaskById(5).getDescription());

        List<Task> tasks = taskManager.getTasks();
        assertEquals("task 1", tasks.get(0).getTitle());
        assertEquals("task 2", tasks.get(1).getTitle());

        List<Epic> epics = taskManager.getEpics();
        assertEquals("epic 1", epics.get(0).getTitle());

        List<Subtask> subtasks = taskManager.getEpicSubtasks(epics.get(0).getId());
        assertEquals("subtask 1", subtasks.get(0).getTitle());
        assertEquals("subtask 2", subtasks.get(1).getTitle());
        assertTrue(taskManager.getEpicSubtasks(1).isEmpty());
        assertTrue(taskManager.getEpicSubtasks(4).isEmpty());
    }

    @Test
    void getHistory() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
        taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
        assertTrue(taskManager.getHistory().isEmpty());
        taskManager.getTaskById(1);
        assertEquals(1, taskManager.getHistory().size());
        assertEquals(1, taskManager.getHistory().getFirst().getId());
        for (int i = 0; i < 12; i++) {
            try {
                taskManager.getTaskById(i);
            } catch (TaskNotFoundException e) {
                // nothing
            }
        }
        taskManager.getTaskById(4);
        taskManager.getTaskById(2);
        taskManager.getTaskById(5);
        taskManager.getTaskById(2);
        assertEquals(5, taskManager.getHistory().size());
        assertEquals(1, taskManager.getHistory().get(0).getId());
        assertEquals(3, taskManager.getHistory().get(1).getId());
        assertEquals(4, taskManager.getHistory().get(2).getId());
        assertEquals(5, taskManager.getHistory().get(3).getId());
        assertEquals(2, taskManager.getHistory().get(4).getId());
    }

    @Test
    void update() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
        taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
        taskManager.update(new Task(1, "updated task 1", "1", TaskStatus.IN_PROGRESS, null, null));
        assertEquals("updated task 1", taskManager.getTaskById(1).getTitle());
        taskManager.update(new Epic(2, "updated epic 1", "2"));
        assertEquals("updated epic 1", taskManager.getTaskById(2).getTitle());
        taskManager.update(new Subtask(3, 0, "updated subtask 1", "3", TaskStatus.NEW, null, null));
        assertEquals("updated subtask 1", taskManager.getTaskById(3).getTitle());
    }

    @Test
    void updateWrongTypes() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
        taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
        assertThrows(WrongTaskArgumentException.class, () -> {
            taskManager.update(new Epic(1, "epic to task", "2"));
        });
        assertThrows(WrongTaskArgumentException.class, () -> {
            taskManager.update(new Subtask(1, 0, "subtask to task", "3", TaskStatus.NEW, null, null));
        });
        assertEquals("task 1", taskManager.getTaskById(1).getTitle());
        assertThrows(WrongTaskArgumentException.class, () -> {
            taskManager.update(new Task(2, "task to epic", "1", TaskStatus.IN_PROGRESS, null, null));
        });
        assertThrows(WrongTaskArgumentException.class, () -> {
            taskManager.update(new Subtask(2, 0, "subtask to epic", "3", TaskStatus.NEW, null, null));
        });
        assertEquals("epic 1", taskManager.getTaskById(2).getTitle());
        assertThrows(WrongTaskArgumentException.class, () -> {
            taskManager.update(new Task(3, "task to subtask", "1", TaskStatus.IN_PROGRESS, null, null));
        });
        assertThrows(WrongTaskArgumentException.class, () -> {
            taskManager.update(new Epic(3, "epic to subtask", "2"));
        });
        assertEquals("subtask 1", taskManager.getTaskById(3).getTitle());
    }

    @Test
    void removeById() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
        taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
        for (int i = 0; i < 15; i++) {
            try {
                taskManager.getTaskById(i);
            } catch (TaskNotFoundException e) {
                // nothing
            }
        }
        assertEquals(5, taskManager.getHistory().size());                  // history size = 5

        taskManager.removeById(1);                                         // remove simple task #1
        assertThrows(TaskNotFoundException.class, () -> {
            taskManager.getTaskById(1);
        });
        assertEquals(4, taskManager.getHistory().size());

        assertEquals(2, taskManager.getEpics().getFirst().getSubtasks().size());
        taskManager.removeById(3);                                         // remove subtask #3
        assertThrows(TaskNotFoundException.class, () -> {
            taskManager.getTaskById(3);
        });
        assertEquals(1, taskManager.getEpics().getFirst().getSubtasks().size());
        assertEquals(3, taskManager.getHistory().size());

        assertEquals(3, taskManager.getTasks().size() + taskManager.getEpics().size() + taskManager.getEpicSubtasks(2).size());
        taskManager.removeById(2);                                         // remove epic #2 (and its subtask #4)
        assertThrows(TaskNotFoundException.class, () -> {
            taskManager.getTaskById(2);
        });
        assertThrows(TaskNotFoundException.class, () -> {
            taskManager.getTaskById(4);
        });
        assertNotNull(taskManager.getTaskById(5));
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void removeAllTasks() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
        taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
        for (int i = 0; i < 15; i++) {
            try {
                taskManager.getTaskById(i);
            } catch (TaskNotFoundException e) {
                //nothing
            }
        }
        taskManager.removeAllTasks();
        assertTrue(taskManager.getTasks().isEmpty());
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(2, taskManager.getEpicSubtasks(2).size());
        assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    void removeAllSubtasks() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
        taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
        for (int i = 0; i < 15; i++) {
            try {
                taskManager.getTaskById(i);
            } catch (TaskNotFoundException e) {
                // do nothing
            }
        }
        taskManager.removeAllSubtasks();
        assertEquals(2, taskManager.getTasks().size());
        assertEquals(1, taskManager.getEpics().size());
        assertTrue(taskManager.getEpicSubtasks(2).isEmpty());
        assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    void removeAllEpics() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
        taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
        for (int i = 0; i < 15; i++) {
            try {
                taskManager.getTaskById(i);
            } catch (TaskNotFoundException e) {
                //nothing
            }
        }
        taskManager.removeAllEpics();
        assertEquals(2, taskManager.getTasks().size());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getEpicSubtasks(2).isEmpty());
        assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    void keepFieldsWhenAdd() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
        taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
        Task t1 = new Task(0, "very", "important data", TaskStatus.DONE, null, null);
        Task t2 = taskManager.add(t1);
        assertEquals(t1.getTitle(), t2.getTitle());
        assertEquals(t1.getDescription(), t2.getDescription());
        assertEquals(t1.getStatus(), t2.getStatus());

        e1 = new Epic(0, "very", "important data");
        Epic e2 = taskManager.add(e1);
        assertEquals(e1.getTitle(), e2.getTitle());
        assertEquals(e1.getDescription(), e2.getDescription());
        assertEquals(e1.getStatus(), e2.getStatus());

        Subtask s1 = new Subtask(0, e2.getId(), "very", "important data", TaskStatus.DONE, null, null);
        Subtask s2 = taskManager.add(s1);
        assertEquals(s1.getTitle(), s2.getTitle());
        assertEquals(s1.getDescription(), s2.getDescription());
        assertEquals(s1.getStatus(), s2.getStatus());
        assertEquals(e2.getStatus(), s2.getStatus());
    }

    @Test
    void sortedTasks() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 9, 0), Duration.ofHours(1)));
        Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofHours(1)));
        taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, LocalDateTime.of(2025, 1, 15, 14, 0), Duration.ofHours(1)));
        taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, LocalDateTime.of(2025, 1, 12, 9, 0), Duration.ofHours(1)));
        List<Task> sorted = taskManager.getPrioritizedTasks();
        assertEquals("1", sorted.get(0).getDescription());
        assertEquals("3", sorted.get(1).getDescription());
        assertEquals("5", sorted.get(2).getDescription());
        assertEquals("4", sorted.get(3).getDescription());
    }

    @Test
    void sortedRandom() throws WrongTaskArgumentException {
        for (int i = 0; i < 10; i++) {
            taskManager.add(new Epic(0, "epic " + i, String.valueOf(i)));
        }
        Random rnd = new Random();
        for (int i = 0; i < 100; i++) {
            if (rnd.nextBoolean()) {
                try {
                    taskManager.add(new Task(0, "t", "t", TaskStatus.NEW,
                            LocalDateTime.of(2025, rnd.nextInt(12) + 1, rnd.nextInt(28) + 1, rnd.nextInt(24), rnd.nextInt(60)),
                            Duration.ofHours(1)));
                } catch (TaskIntersectionException e) {
                    // just skip the move
                }
            } else {
                try {
                    taskManager.add(new Subtask(0, rnd.nextInt(10), "s", "s", TaskStatus.NEW,
                            LocalDateTime.of(2025, rnd.nextInt(12) + 1, rnd.nextInt(28) + 1, rnd.nextInt(24), rnd.nextInt(60)),
                            Duration.ofHours(1)));
                } catch (TaskIntersectionException | TaskNotFoundException e) {
                    // just skip the move
                }
            }
        }
        Task prevTask = null;
        for (Task task : taskManager.getPrioritizedTasks()) {
            if (prevTask == null) {
                prevTask = task;
                continue;
            }
            assertTrue(prevTask.getStartTime().isBefore(task.getStartTime()));
            prevTask = task;
        }
    }

    @Test
    void linkedEpicAndSubtask() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        Epic e1 = taskManager.add(new Epic(0, "e1", ""));
        assertThrows(TaskNotFoundException.class, () -> {
            Subtask s1 = taskManager.add(new Subtask(0, 0, "s1", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofHours(24)));
        });
        assertTrue(e1.getSubtasks().isEmpty());
        Subtask s2 = taskManager.add(new Subtask(0, e1.getId(), "s2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofHours(24)));
        assertNotNull(s2);
        assertEquals(e1, s2.getEpic());
        assertEquals(s2, e1.getSubtasks().get(s2.getId()));
    }

    @Test
    void taskIntersectionExceptions() throws TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        Task t1 = taskManager.add(new Task(0, "t1", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 9, 0), Duration.ofHours(1)));
        Epic e1 = taskManager.add(new Epic(0, "e1", ""));
        Subtask s1 = taskManager.add(new Subtask(0, e1.getId(), "s1", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 12, 0), Duration.ofHours(2)));

        assertThrows(TaskIntersectionException.class, () -> {
            taskManager.add(new Task(0, "t2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 6, 30), Duration.ofHours(3)));
        });
        assertThrows(TaskIntersectionException.class, () -> {
            taskManager.add(new Subtask(0, 2, "s2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 10, 1), Duration.ofHours(2)));
        });
        taskManager.add(new Subtask(0, 2, "s2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofHours(2)));
        assertThrows(TaskIntersectionException.class, () -> {
            taskManager.add(new Task(0, "t2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofDays(30)));
        });

        Task t2 = taskManager.add(new Task(0, "t2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 11, 9, 0), Duration.ofHours(1)));
        taskManager.update(new Task(t2.getId(), "updated t2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 11, 9, 0), Duration.ofHours(1)));
        assertEquals("updated t2", taskManager.getTaskById(t2.getId()).getTitle());

        assertThrows(TaskIntersectionException.class, () -> {
            taskManager.update(new Task(t2.getId(), "t4", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 9, 30), Duration.ofHours(1)));
        });
    }


}