package task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskFactoryTest {

    TaskFactory taskFactory;

    @BeforeEach
    void setUp() {
        taskFactory = new TaskFactory();
    }

    @Test
    void newTaskEpicSubtask() {
        Task t1 = new Task("First title", "First description", TaskStatus.DONE);
        Task t2 = taskFactory.newTask(t1);
        assertEquals(1, t2.getId());
        assertEquals("First title", t2.getTitle());
        assertEquals("First description", t2.getDescription());
        assertEquals(TaskStatus.DONE, t2.getStatus());

        Epic e1 = new Epic("Second title", "Second description");
        Epic e2 = taskFactory.newEpic(e1);
        assertEquals(2, e2.getId());
        assertEquals("Second title", e2.getTitle());
        assertEquals("Second description", e2.getDescription());
        assertEquals(TaskStatus.NEW, e2.getStatus());

        Subtask s1 = new Subtask("Third title", "Third description", TaskStatus.IN_PROGRESS, 2);
        Subtask s2 = taskFactory.newSubtask(s1);
        assertEquals(3, s2.getId());
        assertEquals("Third title", s2.getTitle());
        assertEquals("Third description", s2.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, s2.getStatus());
    }

    @Test
    void nullTests() {
        assertNull(taskFactory.newTask(null));
        assertNull(taskFactory.newEpic(null));
        assertNull(taskFactory.newSubtask(null));
    }

}