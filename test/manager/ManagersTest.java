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
        assertEquals(10, tm.getHistory().size());
    }

    @Test
    void getDefaultParams() {
        TaskFactory tf = Managers.getDefaultFactory();
        HistoryManager hm = Managers.getDefaultHistory(3);
        TaskManager tm = Managers.getDefault(tf, hm);
        tm.add(new Task("task 1", "1", TaskStatus.NEW));
        assertEquals("task 1", tm.getTaskById(1).getTitle());
        assertEquals("task 1", tm.getHistory().getLast().getTitle());
        for (int i = 0; i < 25; i++) tm.getTaskById(1);
        assertEquals(3, tm.getHistory().size());
    }

}