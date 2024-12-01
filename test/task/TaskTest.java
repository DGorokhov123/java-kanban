package task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class TaskTest {

    Task task;

    @BeforeEach
    void setUp() {
        task = new Task(123, "task title", "task description", TaskStatus.NEW);
    }

    @Test
    void getIdAndConstructorWithoutId() {
        assertEquals(123, task.getId());
        Task task1 = new Task("task title", "task description", TaskStatus.NEW);
        assertEquals(0, task1.getId());
    }

    @Test
    void getAndSetTitle() {
        assertEquals("task title", task.getTitle());
        task.setTitle("Yet another title");
        assertEquals("Yet another title", task.getTitle());
    }

    @Test
    void getAndSetDescription() {
        assertEquals("task description", task.getDescription());
        task.setDescription("Yet another description");
        assertEquals("Yet another description", task.getDescription());
    }

    @Test
    void getAndSetStatus() {
        assertEquals(TaskStatus.NEW, task.getStatus());
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void update() {
        boolean up1 = task.update(null);                                                // task = null
        assertFalse(up1);
        assertEquals(123, task.getId());
        assertEquals("task title", task.getTitle());
        assertEquals("task description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());

        Task task2 = new Task("third title", "third description", TaskStatus.DONE);      // Update correctly
        boolean up2 = task.update(task2);
        assertTrue(up2);
        assertEquals(123, task.getId());
        assertEquals("third title", task.getTitle());
        assertEquals("third description", task.getDescription());
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void testToString() {
        assertEquals("Task #123\t[NEW]\ttask title (task description)", task.toString());
    }

    @Test
    void testEquals() {
        assertTrue(task.equals(task));               // the same object check
        assertFalse(task.equals(null));              // null check
        assertFalse(task.equals(new Object()));      // different class check
        assertFalse(task.equals(new Task(35, "task title", "task description", TaskStatus.NEW)));     // different ID same fields
        assertTrue(task.equals(new Task(123, "task title", "task description", TaskStatus.NEW)));     // same ID and fields
        assertTrue(task.equals(new Task(123, "third title", "third description", TaskStatus.DONE)));  // same ID different fields
    }

    @Test
    void testHashCode() {
        assertEquals(123, task.hashCode());
    }
}