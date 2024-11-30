package task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void setUp() {
        epic = new Epic(15, "Epic title", "Epic description");
        subtask1 = new Subtask(234, "First title", "First description", TaskStatus.NEW);
        subtask2 = new Subtask(473, "Second title", "Second description", TaskStatus.DONE);
    }

    @Test
    void getSubtasks() {
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
    void linkUnlinkAndChangeStatus() {
        assertEquals(TaskStatus.NEW, epic.getStatus());
        epic.linkSubtask(subtask1);
        assertEquals(TaskStatus.NEW, epic.getStatus());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        epic.linkSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        epic.unlinkSubtask(234);
        assertEquals(TaskStatus.DONE, epic.getStatus());
        epic.unlinkSubtask(473);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void testToStringAndShortConstructor() {
        Epic epic2 = new Epic("Any title", "Any description");
        assertEquals("Epic #0\t[NEW]\tAny title (Any description)", epic2.toString());
        assertEquals("Epic #15\t[NEW]\tEpic title (Epic description)", epic.toString());
        epic.setDescription("");
        assertEquals("Epic #15\t[NEW]\tEpic title", epic.toString());
    }

}