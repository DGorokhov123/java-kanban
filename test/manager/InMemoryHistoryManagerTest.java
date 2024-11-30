package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addAndGet() {
        InMemoryHistoryManager hm = new InMemoryHistoryManager(3);
        assertTrue(hm.getHistory().isEmpty());

        hm.add(new Task(123, "task 1", "0", TaskStatus.NEW));
        assertEquals(1, hm.getHistory().size());
        assertEquals("task 1", hm.getHistory().get(0).getTitle());

        hm.add(new Epic(250, "epic 1", "1"));
        assertEquals(2, hm.getHistory().size());
        assertEquals("task 1", hm.getHistory().get(0).getTitle());
        assertEquals("epic 1", hm.getHistory().get(1).getTitle());

        hm.add(new Subtask(327, "subtask 1", "2", TaskStatus.IN_PROGRESS));
        assertEquals(3, hm.getHistory().size());
        assertEquals("task 1", hm.getHistory().get(0).getTitle());
        assertEquals("epic 1", hm.getHistory().get(1).getTitle());
        assertEquals("subtask 1", hm.getHistory().get(2).getTitle());

        hm.add(new Subtask(327, "subtask 1", "2", TaskStatus.IN_PROGRESS));
        assertEquals(3, hm.getHistory().size());
        assertEquals("1", hm.getHistory().get(0).getDescription());
        assertEquals("2", hm.getHistory().get(1).getDescription());
        assertEquals("2", hm.getHistory().get(2).getDescription());

        hm.add(new Subtask(958, "subtask 2", "3", TaskStatus.DONE));
        assertEquals(3, hm.getHistory().size());
        assertEquals("2", hm.getHistory().get(0).getDescription());
        assertEquals("2", hm.getHistory().get(1).getDescription());
        assertEquals("3", hm.getHistory().get(2).getDescription());
    }

    @Test
    void negativeOrZeroSize() {
        try {
            InMemoryHistoryManager hm = new InMemoryHistoryManager(0);
        } catch (Exception e) {
            assertEquals("Parameter 'historySize' cannot be less than 1", e.getMessage());
        }
    }

}