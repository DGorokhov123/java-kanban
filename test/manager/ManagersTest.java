package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskFactory;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void getDefault() {
        TaskManager tm = Managers.getDefault();
        tm.add(new Task("task 1", "1", TaskStatus.NEW));
        assertEquals("task 1", tm.getTaskById(1).getTitle());
        assertEquals("task 1", tm.getHistory().getLast().getTitle());
        for (int i = 0; i < 25; i++) tm.getTaskById(1);
        assertEquals(1, tm.getHistory().size());
    }

    @Test
    void getDefaultParams() {
        TaskFactory tf = Managers.getDefaultFactory();
        HistoryManager hm = Managers.getDefaultHistory(3);
        TaskManager tm = Managers.getDefault(tf, hm);
        tm.add(new Task("task 1", "1", TaskStatus.NEW));
        tm.add(new Task("task 2", "2", TaskStatus.NEW));
        tm.add(new Task("task 3", "3", TaskStatus.NEW));
        tm.add(new Task("task 4", "4", TaskStatus.NEW));
        tm.add(new Task("task 5", "5", TaskStatus.NEW));
        assertEquals("task 1", tm.getTaskById(1).getTitle());
        assertEquals("task 1", tm.getHistory().getLast().getTitle());
        assertEquals("task 5", tm.getTaskById(5).getTitle());
        assertEquals("task 5", tm.getHistory().getLast().getTitle());
        for (int i = 0; i < 25; i++) tm.getTaskById((i % 2) * 4 + 1);
        assertEquals(2, tm.getHistory().size());
        for (int i = 0; i < 25; i++) tm.getTaskById(i % 5 + 1);
        assertEquals(3, tm.getHistory().size());
    }

}