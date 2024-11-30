package task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    Epic epic;
    Subtask subtask;

    @BeforeEach
    void setUp() {
        epic = new Epic(15, "Epic title", "Epic description");
        subtask = new Subtask(234, "Subtask title", "Subtask description", TaskStatus.NEW);
    }

    @Test
    void getEpicId() {
        Subtask subtask75 = new Subtask("Subtask title", "Subtask description", TaskStatus.NEW, 75);
        assertEquals(75, subtask75.getEpicId());
    }

    @Test
    void getAndSetEpic() {
        assertNull(subtask.getEpic());
        subtask.setEpic(epic);
        assertEquals(epic, subtask.getEpic());
        assertEquals(epic.getTitle(), subtask.getEpic().getTitle());
    }

    @Test
    void getAndSetStatus() {
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, subtask.getStatus());
        epic.linkSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        subtask.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, subtask.getStatus());
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void testToStringAndShortConstructor() {
        epic.linkSubtask(subtask);
        assertEquals("Subtask #234, epic #15\t[NEW]\tSubtask title (Subtask description)", subtask.toString());
        Subtask s2 = new Subtask("Second title", "Second description", TaskStatus.DONE, 94);
        assertEquals("Subtask #0, epic #null\t[DONE]\tSecond title (Second description)", s2.toString());
    }
}