package manager;

import org.junit.jupiter.api.Test;
import task.*;
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
    void allGetMethods() {
        try {
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

            List<Subtask> subtasks = taskManager.getSubTasks(epics.get(0).getId());
            assertEquals("subtask 1", subtasks.get(0).getTitle());
            assertEquals("subtask 2", subtasks.get(1).getTitle());
            assertTrue(taskManager.getSubTasks(1).isEmpty());
            assertTrue(taskManager.getSubTasks(4).isEmpty());
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void getHistory() {
        try {
            taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
            Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
            taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
            assertTrue(taskManager.getHistory().isEmpty());
            taskManager.getTaskById(1);
            assertEquals(1, taskManager.getHistory().size());
            assertEquals(1, taskManager.getHistory().getFirst().getId());
            for (int i = 0; i < 12; i++) taskManager.getTaskById(i);
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
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void update() {
        try {
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
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void updateWrongTypes() {
        try {
            taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
            Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
            taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
            taskManager.update(new Epic(1, "epic to task", "2"));
            taskManager.update(new Subtask(1, 0, "subtask to task", "3", TaskStatus.NEW, null, null));
            assertEquals("task 1", taskManager.getTaskById(1).getTitle());

            taskManager.update(new Task(2, "task to epic", "1", TaskStatus.IN_PROGRESS, null, null));
            taskManager.update(new Subtask(2, 0, "subtask to epic", "3", TaskStatus.NEW, null, null));
            assertEquals("epic 1", taskManager.getTaskById(2).getTitle());

            taskManager.update(new Task(3, "task to subtask", "1", TaskStatus.IN_PROGRESS, null, null));
            taskManager.update(new Epic(3, "epic to subtask", "2"));
            assertEquals("subtask 1", taskManager.getTaskById(3).getTitle());
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void removeById() {
        try {
            taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
            Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
            taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
            for (int i = 0; i < 15; i++) taskManager.getTaskById(i);           // views history ids { 1, 2, 3, 4, 5 }
            assertEquals(5, taskManager.getHistory().size());                  // history size = 5

            taskManager.removeById(1);                                         // remove simple task #1
            assertNull(taskManager.getTaskById(1));
            assertEquals(4, taskManager.getHistory().size());

            assertEquals(2, taskManager.getEpics().getFirst().getSubtasks().size());
            taskManager.removeById(3);                                         // remove subtask #3
            assertNull(taskManager.getTaskById(3));
            assertEquals(1, taskManager.getEpics().getFirst().getSubtasks().size());
            assertEquals(3, taskManager.getHistory().size());

            assertEquals(3, taskManager.getTasks().size() + taskManager.getEpics().size() + taskManager.getSubTasks(2).size());
            taskManager.removeById(2);                                         // remove epic #2 (and its subtask #4)
            assertNull(taskManager.getTaskById(2));
            assertNull(taskManager.getTaskById(4));
            assertNotNull(taskManager.getTaskById(5));
            assertEquals(1, taskManager.getHistory().size());
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void removeAllTasks() {
        try {
            taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
            Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
            taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
            for (int i = 0; i < 15; i++) taskManager.getTaskById(i);           // views history ids { 1, 2, 3, 4, 5 }
            taskManager.removeAllTasks();
            assertTrue(taskManager.getTasks().isEmpty());
            assertEquals(1, taskManager.getEpics().size());
            assertEquals(2, taskManager.getSubTasks(2).size());
            assertEquals(3, taskManager.getHistory().size());
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void removeAllSubtasks() {
        try {
            taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
            Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
            taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
            for (int i = 0; i < 15; i++) taskManager.getTaskById(i);           // views history ids { 1, 2, 3, 4, 5 }
            taskManager.removeAllSubtasks();
            assertEquals(2, taskManager.getTasks().size());
            assertEquals(1, taskManager.getEpics().size());
            assertTrue(taskManager.getSubTasks(2).isEmpty());
            assertEquals(3, taskManager.getHistory().size());
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void removeAllEpics() {
        try {
            taskManager.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
            Epic e1 = taskManager.add(new Epic(0, "epic 1", "2"));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 1", "3", TaskStatus.NEW, null, null));
            taskManager.add(new Subtask(0, e1.getId(), "subtask 2", "4", TaskStatus.NEW, null, null));
            taskManager.add(new Task(0, "task 2", "5", TaskStatus.NEW, null, null));
            for (int i = 0; i < 15; i++) taskManager.getTaskById(i);           // views history ids { 1, 2, 3, 4, 5 }
            taskManager.removeAllEpics();
            assertEquals(2, taskManager.getTasks().size());
            assertTrue(taskManager.getEpics().isEmpty());
            assertTrue(taskManager.getSubTasks(2).isEmpty());
            assertEquals(2, taskManager.getHistory().size());
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void keepFieldsWhenAdd() {
        try {
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
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void sortedTasks() {
        try {
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
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void sortedRandom() {
        for (int i = 0; i < 10; i++) taskManager.add(new Epic(0, "epic " + i, String.valueOf(i)));
        Random rnd = new Random();
        for (int i = 0; i < 100; i++) {
            if (rnd.nextBoolean()) {
                try {
                    taskManager.add(new Task(0, "t", "t", TaskStatus.NEW,
                            LocalDateTime.of(2025, rnd.nextInt(12)+1, rnd.nextInt(28)+1, rnd.nextInt(24), rnd.nextInt(60)),
                            Duration.ofHours(1)));
                } catch (TaskIntersectionException e) {
                    // just skip the move
                }
            } else {
                try {
                    taskManager.add(new Subtask(0, rnd.nextInt(10), "s", "s", TaskStatus.NEW,
                            LocalDateTime.of(2025, rnd.nextInt(12)+1, rnd.nextInt(28)+1, rnd.nextInt(24), rnd.nextInt(60)),
                            Duration.ofHours(1)));
                } catch (TaskIntersectionException e) {
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
    void linkedEpicAndSubtask() {
        try {
            Epic e1 = taskManager.add(new Epic(0, "e1", ""));
            Subtask s1 = taskManager.add(new Subtask(0, 0, "s1", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofHours(24)));
            assertNull(s1);
            assertTrue(e1.getSubtasks().isEmpty());
            Subtask s2 = taskManager.add(new Subtask(0, e1.getId(), "s2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofHours(24)));
            assertNotNull(s2);
            assertEquals(e1, s2.getEpic());
            assertEquals(s2, e1.getSubtasks().get(s2.getId()));
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

    @Test
    void taskIntersectionExceptions() {
        try {
            Task t1 = taskManager.add(new Task(0, "t1", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 9, 0), Duration.ofHours(1)));
            Epic e1 = taskManager.add(new Epic(0, "e1", ""));
            Subtask s1 = taskManager.add(new Subtask(0, e1.getId(), "s1", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 12, 0), Duration.ofHours(2)));
        } catch (TaskIntersectionException e) {
            assertNull(e);   // everything should be ok
        }
        assertThrows(TaskIntersectionException.class, () -> {
            taskManager.add(new Task(0, "t2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 6, 30), Duration.ofHours(3)));
        });
        assertThrows(TaskIntersectionException.class, () -> {
            taskManager.add(new Subtask(0, 0, "s2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 10, 1), Duration.ofHours(2)));
        });
        assertDoesNotThrow(() -> {
            taskManager.add(new Subtask(0, 0, "s2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofHours(2)));
        });
        assertThrows(TaskIntersectionException.class, () -> {
            taskManager.add(new Task(0, "t2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofDays(30)));
        });

        Task t2 = new Task(0, "t2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 11, 9, 0), Duration.ofHours(1));
        try {
            t2 = taskManager.add(t2);
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
        Task t3 = new Task(t2.getId(), "updated t2", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 11, 9, 0), Duration.ofHours(1));
        assertDoesNotThrow(() -> {
            taskManager.update(t3);
        });
        assertEquals("updated t2", taskManager.getTaskById(t2.getId()).getTitle());
        Task t4 = new Task(t2.getId(), "t4", "", TaskStatus.NEW, LocalDateTime.of(2025, 1, 10, 9, 30), Duration.ofHours(1));
        assertThrows(TaskIntersectionException.class, () -> {
            taskManager.update(t4);
        });
    }


}