package task;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void getSubtasks() {
        Epic epic = new Epic(15, "Epic title", "Epic description");
        Subtask subtask1 = new Subtask(234, 0, "First title", "First description", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3));
        Subtask subtask2 = new Subtask(473, 0, "Second title", "Second description", TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(3));
        assertTrue(epic.getSubtasks().isEmpty());
        epic.linkSubtask(subtask1);
        assertEquals(1, epic.getSubtasks().size());
        epic.linkSubtask(subtask2);
        assertEquals(2, epic.getSubtasks().size());
        assertEquals(subtask1, epic.getSubtasks().get(234));
        assertEquals("First title", epic.getSubtasks().get(234).getTitle());
        assertEquals("Second description", epic.getSubtasks().get(473).getDescription());
    }

    @Test
    void changeStatus() {
        Epic epic = new Epic(1, "e1", "");
        assertEquals(TaskStatus.NEW, epic.getStatus());
        epic.linkSubtask(new Subtask(2, 0, "s2", "", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3)));
        epic.linkSubtask(new Subtask(3, 0, "s3", "", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3)));
        assertEquals(TaskStatus.NEW, epic.getStatus());
        epic.linkSubtask(new Subtask(4, 0, "s4", "", TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(3)));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        Epic epic1 = new Epic(1, "e1", "");
        assertEquals(TaskStatus.NEW, epic1.getStatus());
        epic1.linkSubtask(new Subtask(2, 0, "s2", "", TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(3)));
        epic1.linkSubtask(new Subtask(3, 0, "s3", "", TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(3)));
        epic1.linkSubtask(new Subtask(4, 0, "s4", "", TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(3)));
        assertEquals(TaskStatus.DONE, epic1.getStatus());

        Epic epic2 = new Epic(1, "e1", "");
        assertEquals(TaskStatus.NEW, epic2.getStatus());
        epic2.linkSubtask(new Subtask(2, 0, "s2", "", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(3)));
        epic2.linkSubtask(new Subtask(3, 0, "s3", "", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(3)));
        epic2.linkSubtask(new Subtask(4, 0, "s4", "", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(3)));
        assertEquals(TaskStatus.IN_PROGRESS, epic2.getStatus());

        Epic epic3 = new Epic(1, "e1", "");
        assertEquals(TaskStatus.NEW, epic3.getStatus());
        epic3.linkSubtask(new Subtask(2, 0, "s2", "", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3)));
        epic3.linkSubtask(new Subtask(3, 0, "s3", "", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(3)));
        epic3.linkSubtask(new Subtask(4, 0, "s4", "", TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(3)));
        assertEquals(TaskStatus.IN_PROGRESS, epic3.getStatus());
    }

}