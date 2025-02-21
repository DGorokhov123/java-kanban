package manager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import task.Task;
import task.TaskFactory;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ManagersTest {

    @Test
    void getDefault() throws WrongTaskArgumentException, TaskIntersectionException, TaskNotFoundException {
        TaskManager tm = Managers.createNewInMemory(Managers.getDefaultFactory(), Managers.getDefaultHistory());
        tm.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        assertEquals("task 1", tm.getTaskById(1).getTitle());
        assertEquals("task 1", tm.getHistory().getLast().getTitle());
        for (int i = 0; i < 25; i++) tm.getTaskById(1);
        assertEquals(1, tm.getHistory().size());

        TaskManager tm2 = Managers.getDefault();
        tm2.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        assertEquals("task 1", tm2.getTaskById(1).getTitle());
        assertEquals("task 1", tm2.getHistory().getLast().getTitle());
        for (int i = 0; i < 25; i++) tm2.getTaskById(1);
        assertEquals(1, tm2.getHistory().size());
    }

    @Test
    void getDefaultParams() throws WrongTaskArgumentException, TaskIntersectionException, TaskNotFoundException {
        TaskFactory tf = Managers.getDefaultFactory();
        HistoryManager hm = Managers.getDefaultHistory(3);
        TaskManager tm = Managers.createNewInMemory(tf, hm);
        tm.add(new Task(0, "task 1", "1", TaskStatus.NEW, null, null));
        tm.add(new Task(0, "task 2", "2", TaskStatus.NEW, null, null));
        tm.add(new Task(0, "task 3", "3", TaskStatus.NEW, null, null));
        tm.add(new Task(0, "task 4", "4", TaskStatus.NEW, null, null));
        tm.add(new Task(0, "task 5", "5", TaskStatus.NEW, null, null));
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