package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    InMemoryTaskManager tm;

    @BeforeEach
    void setUpAndAllAddMethods() {
        TaskFactory taskFactory = new TaskFactory();
        HistoryManager historyManager = new InMemoryHistoryManager(10);
        tm = new InMemoryTaskManager(taskFactory, historyManager);
        tm.add(new Task("task 1", "1", TaskStatus.NEW));
        Epic e1 = tm.add(new Epic("epic 1", "2"));
        tm.add(new Subtask("subtask 1", "3", TaskStatus.NEW, e1.getId()));
        tm.add(new Subtask("subtask 2", "4", TaskStatus.NEW, e1.getId()));
        tm.add(new Task("task 2", "5", TaskStatus.NEW));
    }

    @Test
    void allGetMethods() {
        assertEquals("1", tm.getTaskById(1).getDescription());
        assertEquals("2", tm.getTaskById(2).getDescription());
        assertEquals("3", tm.getTaskById(3).getDescription());
        assertEquals("4", tm.getTaskById(4).getDescription());
        assertEquals("5", tm.getTaskById(5).getDescription());

        List<Task> tasks = tm.getTasks();
        assertEquals("task 1", tasks.get(0).getTitle());
        assertEquals("task 2", tasks.get(1).getTitle());

        List<Epic> epics = tm.getEpics();
        assertEquals("epic 1", epics.get(0).getTitle());

        List<Subtask> subtasks = tm.getSubTasks(epics.get(0).getId());
        assertEquals("subtask 1", subtasks.get(0).getTitle());
        assertEquals("subtask 2", subtasks.get(1).getTitle());
        assertTrue(tm.getSubTasks(1).isEmpty());
        assertTrue(tm.getSubTasks(4).isEmpty());
    }

    @Test
    void getHistory() {
        assertTrue(tm.getHistory().isEmpty());
        tm.getTaskById(1);
        assertEquals(1, tm.getHistory().size());
        assertEquals(1, tm.getHistory().getFirst().getId());
        for (int i = 0; i < 12; i++) tm.getTaskById(i);
        tm.getTaskById(4);
        tm.getTaskById(2);
        tm.getTaskById(5);
        tm.getTaskById(2);
        assertEquals(5, tm.getHistory().size());
        assertEquals(1, tm.getHistory().get(0).getId());
        assertEquals(3, tm.getHistory().get(1).getId());
        assertEquals(4, tm.getHistory().get(2).getId());
        assertEquals(5, tm.getHistory().get(3).getId());
        assertEquals(2, tm.getHistory().get(4).getId());
    }

    @Test
    void update() {
        tm.update(new Task(1, "updated task 1", "1", TaskStatus.IN_PROGRESS));
        assertEquals("updated task 1", tm.getTaskById(1).getTitle());
        tm.update(new Epic(2, "updated epic 1", "2"));
        assertEquals("updated epic 1", tm.getTaskById(2).getTitle());
        tm.update(new Subtask(3, "updated subtask 1", "3", TaskStatus.NEW));
        assertEquals("updated subtask 1", tm.getTaskById(3).getTitle());
    }

    @Test
    void updateWrongTypes() {
        tm.update(new Epic(1, "epic to task", "2"));
        tm.update(new Subtask(1, "subtask to task", "3", TaskStatus.NEW));
        assertEquals("task 1", tm.getTaskById(1).getTitle());

        tm.update(new Task(2, "task to epic", "1", TaskStatus.IN_PROGRESS));
        tm.update(new Subtask(2, "subtask to epic", "3", TaskStatus.NEW));
        assertEquals("epic 1", tm.getTaskById(2).getTitle());

        tm.update(new Task(3, "task to subtask", "1", TaskStatus.IN_PROGRESS));
        tm.update(new Epic(3, "epic to subtask", "2"));
        assertEquals("subtask 1", tm.getTaskById(3).getTitle());
    }

    @Test
    void removeById() {
        for (int i = 0; i < 15; i++) tm.getTaskById(i);           // views history ids { 1, 2, 3, 4, 5 }
        assertEquals(5, tm.getHistory().size());                  // history size = 5

        tm.removeById(1);                                         // remove simple task #1
        assertNull(tm.getTaskById(1));
        assertEquals(4, tm.getHistory().size());

        assertEquals(2, tm.getEpics().getFirst().getSubtasks().size());
        tm.removeById(3);                                         // remove subtask #3
        assertNull(tm.getTaskById(3));
        assertEquals(1, tm.getEpics().getFirst().getSubtasks().size());
        assertEquals(3, tm.getHistory().size());

        assertEquals(3, tm.getTasks().size() + tm.getEpics().size() + tm.getSubTasks(2).size());
        tm.removeById(2);                                         // remove epic #2 (and its subtask #4)
        assertNull(tm.getTaskById(2));
        assertNull(tm.getTaskById(4));
        assertNotNull(tm.getTaskById(5));
        assertEquals(1, tm.getHistory().size());
    }

    @Test
    void removeAllTasks() {
        for (int i = 0; i < 15; i++) tm.getTaskById(i);           // views history ids { 1, 2, 3, 4, 5 }
        tm.removeAllTasks();
        assertTrue(tm.getTasks().isEmpty());
        assertEquals(1, tm.getEpics().size());
        assertEquals(2, tm.getSubTasks(2).size());
        assertEquals(3, tm.getHistory().size());
    }

    @Test
    void removeAllSubtasks() {
        for (int i = 0; i < 15; i++) tm.getTaskById(i);           // views history ids { 1, 2, 3, 4, 5 }
        tm.removeAllSubtasks();
        assertEquals(2, tm.getTasks().size());
        assertEquals(1, tm.getEpics().size());
        assertTrue(tm.getSubTasks(2).isEmpty());
        assertEquals(3, tm.getHistory().size());
    }

    @Test
    void removeAllEpics() {
        for (int i = 0; i < 15; i++) tm.getTaskById(i);           // views history ids { 1, 2, 3, 4, 5 }
        tm.removeAllEpics();
        assertEquals(2, tm.getTasks().size());
        assertTrue(tm.getEpics().isEmpty());
        assertTrue(tm.getSubTasks(2).isEmpty());
        assertEquals(2, tm.getHistory().size());
    }

    @Test
    void nullParams() {
        assertThrows(IllegalArgumentException.class, () ->  new InMemoryTaskManager(null, null));
        assertThrows(IllegalArgumentException.class, () ->  new InMemoryTaskManager(new TaskFactory(), null));
        assertThrows(IllegalArgumentException.class, () ->  new InMemoryTaskManager(null, new InMemoryHistoryManager()));
    }

    @Test
    void keepFieldsWhenAdd() {
        Task t1 = new Task("very", "important data", TaskStatus.DONE);
        Task t2 = tm.add(t1);
        assertEquals(t1.getTitle(), t2.getTitle());
        assertEquals(t1.getDescription(), t2.getDescription());
        assertEquals(t1.getStatus(), t2.getStatus());

        Epic e1 = new Epic("very", "important data");
        Epic e2 = tm.add(e1);
        assertEquals(e1.getTitle(), e2.getTitle());
        assertEquals(e1.getDescription(), e2.getDescription());
        assertEquals(e1.getStatus(), e2.getStatus());

        Subtask s1 = new Subtask("very", "important data", TaskStatus.DONE, e2.getId());
        Subtask s2 = tm.add(s1);
        assertEquals(s1.getTitle(), s2.getTitle());
        assertEquals(s1.getDescription(), s2.getDescription());
        assertEquals(s1.getStatus(), s2.getStatus());

        assertEquals(e2.getStatus(), s2.getStatus());
    }



}