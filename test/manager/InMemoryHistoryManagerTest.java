package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addAndGetAndRemove() {
        InMemoryHistoryManager hm = new InMemoryHistoryManager(4);                 // new history manager
        assertTrue(hm.getHistory().isEmpty());

        hm.add(new Task(123, "task 1", "0", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3)));                     // task # 0
        assertEquals(1, hm.getHistory().size());
        assertEquals("task 1", hm.getHistory().getFirst().getTitle());

        hm.add(new Epic(250, "epic 1", "1"));                                     // task # 1
        assertEquals(2, hm.getHistory().size());
        assertEquals("task 1", hm.getHistory().get(0).getTitle());
        assertEquals("epic 1", hm.getHistory().get(1).getTitle());

        hm.add(new Subtask(327, 0, "subtask 1", "2", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(3)));       // task # 2
        assertEquals(3, hm.getHistory().size());
        assertEquals("task 1", hm.getHistory().get(0).getTitle());
        assertEquals("epic 1", hm.getHistory().get(1).getTitle());
        assertEquals("subtask 1", hm.getHistory().get(2).getTitle());

        hm.add(new Subtask(327, 0, "subtask 1", "2", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(3)));       // task # 2 - duplicate
        assertEquals(3, hm.getHistory().size());
        assertEquals("0", hm.getHistory().get(0).getDescription());
        assertEquals("1", hm.getHistory().get(1).getDescription());
        assertEquals("2", hm.getHistory().get(2).getDescription());

        hm.add(new Subtask(958, 0, "subtask 2", "3", TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(3)));              // task # 3
        assertEquals(4, hm.getHistory().size());
        assertEquals("0", hm.getHistory().get(0).getDescription());
        assertEquals("1", hm.getHistory().get(1).getDescription());
        assertEquals("2", hm.getHistory().get(2).getDescription());
        assertEquals("3", hm.getHistory().get(3).getDescription());

        hm.add(new Subtask(327, 0, "subtask 1", "2", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(3)));       // task # 2 - duplicate
        assertEquals(4, hm.getHistory().size());
        assertEquals("0", hm.getHistory().get(0).getDescription());
        assertEquals("1", hm.getHistory().get(1).getDescription());
        assertEquals("3", hm.getHistory().get(2).getDescription());
        assertEquals("2", hm.getHistory().get(3).getDescription());

        hm.add(new Subtask(512, 0, "subtask 3", "4", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(3)));       // task # 4 - oversize
        assertEquals(4, hm.getHistory().size());
        assertEquals("1", hm.getHistory().get(0).getDescription());
        assertEquals("3", hm.getHistory().get(1).getDescription());
        assertEquals("2", hm.getHistory().get(2).getDescription());
        assertEquals("4", hm.getHistory().get(3).getDescription());

        hm.remove(2);                                                             // nonexistent id
        assertEquals(4, hm.getHistory().size());

        hm.remove(327);                                                           // task # 2 is removed
        assertEquals(3, hm.getHistory().size());
        assertEquals("1", hm.getHistory().get(0).getDescription());
        assertEquals("3", hm.getHistory().get(1).getDescription());
        assertEquals("4", hm.getHistory().get(2).getDescription());

        hm.remove(250);                                                           // task # 1 is removed
        assertEquals(2, hm.getHistory().size());
        assertEquals("3", hm.getHistory().get(0).getDescription());
        assertEquals("4", hm.getHistory().get(1).getDescription());

    }

    @Test
    void negativeOrZeroSize() {
        assertThrows(IllegalArgumentException.class, () ->  new InMemoryHistoryManager(0));
    }

}