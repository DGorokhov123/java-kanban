package task;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void getEpicId() {
        Epic epic = new Epic(15, "Epic title", "Epic description");
        Subtask subtask = new Subtask(234, 0, "Subtask title", "Subtask description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3));
        Subtask subtask75 = new Subtask(0, 75, "Subtask title", "Subtask description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3));
        assertEquals(75, subtask75.getEpicId());
    }

    @Test
    void getAndSetEpic() {
        Epic epic = new Epic(15, "Epic title", "Epic description");
        Subtask subtask = new Subtask(234, 0, "Subtask title", "Subtask description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3));
        assertNull(subtask.getEpic());
        subtask.setEpic(epic);
        assertEquals(epic, subtask.getEpic());
        assertEquals(epic.getTitle(), subtask.getEpic().getTitle());
    }

    @Test
    void linkedEpic() {
        Epic epic = new Epic(1, "Epic title", "Epic description");
        Subtask subtask = new Subtask(2, 0, "Subtask title", "Subtask description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3));
        assertTrue(epic.getSubtasks().isEmpty());
        assertNull(subtask.getEpic());
        epic.linkSubtask(subtask);
        assertEquals(epic, subtask.getEpic());
        assertEquals(subtask, epic.getSubtasks().get(subtask.getId()));
    }

    @Test
    void hasIntersection() {
        Subtask subtask1 = new Subtask(1, 0, "s1", "", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 1, 1), Duration.ofHours(5));
        Subtask subtask2 = new Subtask(2, 0, "s2", "", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 3, 21), Duration.ofHours(5));
        Subtask subtask3 = new Subtask(3, 0, "s3", "", TaskStatus.NEW, LocalDateTime.of(2000, 5, 5, 1, 1), Duration.ofHours(5));
        assertTrue(subtask1.hasIntersection(subtask2));
        assertTrue(subtask2.hasIntersection(subtask1));
        assertFalse(subtask1.hasIntersection(subtask3));
        assertFalse(subtask3.hasIntersection(subtask1));
        Task task1 = new Task(1, "t1", "", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 1, 1), Duration.ofHours(5));
        Task task2 = new Task(2, "t2", "", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 3, 21), Duration.ofHours(5));
        Task task3 = new Task(3, "t3", "", TaskStatus.NEW, LocalDateTime.of(2000, 5, 5, 1, 1), Duration.ofHours(5));
        assertTrue(task1.hasIntersection(subtask2));
        assertTrue(task2.hasIntersection(subtask1));
        assertFalse(task1.hasIntersection(subtask3));
        assertFalse(task3.hasIntersection(subtask1));
        assertTrue(subtask1.hasIntersection(task2));
        assertTrue(subtask2.hasIntersection(task1));
        assertFalse(subtask1.hasIntersection(task3));
        assertFalse(subtask3.hasIntersection(task1));
    }

}