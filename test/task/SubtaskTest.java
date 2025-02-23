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

}