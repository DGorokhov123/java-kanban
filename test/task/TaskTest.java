package task;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void getIdAndConstructorWithoutId() {
        Task task = new Task(123, "task title", "task description", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 1, 1), Duration.ofHours(5));
        assertEquals(123, task.getId());
        Task task1 = new Task(0, "task title", "task description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3));
        assertEquals(0, task1.getId());
    }

    @Test
    void update() {
        Task task = new Task(123, "task title", "task description", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 1, 1), Duration.ofHours(5));
        boolean up1 = task.update(null);                                                // task = null
        assertFalse(up1);
        assertEquals(123, task.getId());
        assertEquals("task title", task.getTitle());
        assertEquals("task description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());

        Task task2 = new Task(0, "third title", "third description", TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(3));
        boolean up2 = task.update(task2);
        assertTrue(up2);
        assertEquals(123, task.getId());
        assertEquals("third title", task.getTitle());
        assertEquals("third description", task.getDescription());
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void testEquals() {
        Task task = new Task(123, "task title", "task description", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 1, 1), Duration.ofHours(5));
        assertTrue(task.equals(task));               // the same object check
        assertFalse(task.equals(null));              // null check
        assertFalse(task.equals(new Object()));      // different class check
        assertFalse(task.equals(new Task(35, "task title", "task description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3))));     // different ID same fields
        assertTrue(task.equals(new Task(123, "task title", "task description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3))));     // same ID and fields
        assertTrue(task.equals(new Task(123, "third title", "third description", TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(3))));  // same ID different fields
    }

    @Test
    void testHashCode() {
        Task task = new Task(123, "task title", "task description", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 1, 1), Duration.ofHours(5));
        assertEquals(123, task.hashCode());
    }

    @Test
    void hasIntersection() {
        Task task1 = new Task(1, "t1", "", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 1, 1), Duration.ofHours(5));
        Task task2 = new Task(2, "t2", "", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 3, 21), Duration.ofHours(5));
        Task task3 = new Task(3, "t3", "", TaskStatus.NEW, LocalDateTime.of(2000, 5, 5, 1, 1), Duration.ofHours(5));
        assertTrue(task1.hasIntersection(task2));
        assertTrue(task2.hasIntersection(task1));
        assertFalse(task1.hasIntersection(task3));
        assertFalse(task3.hasIntersection(task1));
    }

}