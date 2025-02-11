package manager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    protected InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager(new TaskFactory(), new InMemoryHistoryManager(10)));
    }

    @Test
    void nullParams() {
        assertThrows(IllegalArgumentException.class, () ->  new InMemoryTaskManager(null, null));
        assertThrows(IllegalArgumentException.class, () ->  new InMemoryTaskManager(new TaskFactory(), null));
        assertThrows(IllegalArgumentException.class, () ->  new InMemoryTaskManager(null, new InMemoryHistoryManager()));
    }

    @Test
    void continuousHistoryOps() {
        TaskManager tm = new InMemoryTaskManager(new TaskFactory(), new InMemoryHistoryManager(10));
        Random rnd = new Random();
        int choice = 0;
        int lastID = 0;
        int lastEpicID = 0;
        try {
            for (int i = 0; i < 1000; i++) {
                choice = rnd.nextInt(8);
                if (choice == 0) {
                    lastID = tm.add(new Task(0, "t", "", TaskStatus.NEW, null, null)).getId();
                } else if (choice == 1) {
                    lastID = tm.add(new Epic(0, "e", "")).getId();
                    lastEpicID = lastID;
                } else if (choice <= 3 && lastEpicID > 0) {
                    lastID = tm.add(new Subtask(0, lastEpicID, "s", "", TaskStatus.NEW, null, null)).getId();
                } else if (choice <= 5 && lastID > 0) {
                    int rndRes = rnd.nextInt(lastID);
                    Task tsk = tm.getTaskById(rndRes);
                    if (tsk instanceof Epic) {
                        tm.update(new Epic(rndRes, "Epic", String.valueOf(i)));
                    } else if (tsk instanceof Subtask) {
                        tm.update(new Subtask(rndRes, 0, "Subtask", String.valueOf(i), TaskStatus.DONE, null, null));
                    } else {
                        tm.update(new Task(rndRes, "Task", String.valueOf(i), TaskStatus.DONE, null, null));
                    }
                } else {
                    int rndRes = rnd.nextInt(lastID + 1);
                    if (rndRes != lastEpicID) tm.removeById(rndRes);
                }
            }
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
        System.out.println("======================== Корректность истории ========================");
        int historyCounter = 1;
        Task prevTask = null;
        for (Task task : tm.getHistory()) {
            if (prevTask == null) {
                prevTask = task;
            } else {
                assertTrue(prevTask.getDescription().compareTo(task.getDescription()) < 0);
            }
            System.out.println(historyCounter++ + ". " + task.toString());
        }
    }

    @Test
    void user_behaviour_simulation() throws TaskIntersectionException {
        TaskManager tm = new InMemoryTaskManager(new TaskFactory(), new InMemoryHistoryManager(10));
        // Simple task #1
        tm.add(new Task(0, "Купить биткойн", "по сто рублей", TaskStatus.DONE, null, null));
        assertEquals("Купить биткойн", tm.getTaskById(1).getTitle());

        // Simple task #2 - add + update
        Task t2 = tm.add(new Task(0, "Старый тайтл", "метод update не сработал!", TaskStatus.NEW, null, null));
        assertEquals("Старый тайтл", tm.getTaskById(2).getTitle());
        tm.update(new Task(t2.getId(), "Новый тайтл", "метод update успешно отработал", TaskStatus.IN_PROGRESS, null, null));
        assertEquals("Новый тайтл", tm.getTaskById(2).getTitle());

        // Simple task #3 - add + delete
        Task t3 = tm.add(new Task(0, "Удаление таска", "это должно быть удалено", TaskStatus.NEW, null, null));
        assertEquals("Удаление таска", tm.getTaskById(3).getTitle());
        tm.removeById(t3.getId());
        assertNull(tm.getTaskById(3));

        // Epic #1
        Epic e1 = tm.add(new Epic(0, "Наладить жизнь в России", ""));
        assertEquals("Наладить жизнь в России", tm.getTaskById(4).getTitle());
        assertEquals(TaskStatus.NEW, tm.getTaskById(4).getStatus());
        tm.add(new Subtask(0, e1.getId(), "Запретить все плохое", "Составить реестр плохого, принять законы", TaskStatus.DONE, null, null));
        assertEquals("Запретить все плохое", tm.getTaskById(5).getTitle());
        assertEquals(TaskStatus.DONE, tm.getTaskById(4).getStatus());
        tm.add(new Subtask(0, e1.getId(), "Увеличить рождаемость", "вернуть в страну Павла Дурова", TaskStatus.IN_PROGRESS, null, null));
        assertEquals("Увеличить рождаемость", tm.getTaskById(6).getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, tm.getTaskById(4).getStatus());
        tm.add(new Subtask(0, e1.getId(), "Укрепить рубль", "и уменьшить ключевую ставку", TaskStatus.NEW, null, null));
        assertEquals("Укрепить рубль", tm.getTaskById(7).getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, tm.getTaskById(4).getStatus());

        // Epic #2 - add + delete
        Epic e2 = tm.add(new Epic(0, "Проверить удаление эпиков", ""));
        assertEquals("Проверить удаление эпиков", tm.getTaskById(8).getTitle());
        tm.add(new Subtask(0, e2.getId(), "а также", "всех его сабтасков", TaskStatus.NEW, null, null));
        assertEquals("а также", tm.getTaskById(9).getTitle());
        tm.removeById(e2.getId());
        assertNull(tm.getTaskById(8));
        assertNull(tm.getTaskById(9));

        // Epic #3 - add + update + delete subtask
        Epic e3 = tm.add(new Epic(0, "Название эпика надо изменить", ""));
        assertEquals("Название эпика надо изменить", tm.getTaskById(10).getTitle());
        assertEquals(TaskStatus.NEW, tm.getTaskById(10).getStatus());
        Subtask e3s1 = tm.add(new Subtask(0, e3.getId(), "этот сабтаск 11", "нужно изменить", TaskStatus.NEW, null, null));
        assertEquals("этот сабтаск 11", tm.getTaskById(11).getTitle());
        Subtask e3s2 = tm.add(new Subtask(0, e3.getId(), "этот сабтаск 12", "нужно изменить", TaskStatus.NEW, null, null));
        assertEquals("этот сабтаск 12", tm.getTaskById(12).getTitle());
        Subtask e3s3 = tm.add(new Subtask(0, e3.getId(), "этот сабтаск 13", "нужно изменить", TaskStatus.NEW, null, null));
        assertEquals("этот сабтаск 13", tm.getTaskById(13).getTitle());
        Subtask e3s4 = tm.add(new Subtask(0, e3.getId(), "а вот этот сабтаск 14", "нужно удалить", TaskStatus.NEW, null, null));
        assertEquals("а вот этот сабтаск 14", tm.getTaskById(14).getTitle());
        Subtask e3s5 = tm.add(new Subtask(0, e3.getId(), "этот сабтаск 15", "нужно изменить", TaskStatus.NEW, null, null));
        assertEquals("этот сабтаск 15", tm.getTaskById(15).getTitle());

        tm.update(new Epic(e3.getId(), "Стать крутым прогером", "план надежный как швейцарские часы"));
        assertEquals("Стать крутым прогером", tm.getTaskById(10).getTitle());
        tm.update(new Subtask(e3s1.getId(), 0, "Закончить курсы", "например Яндекс Практикум", TaskStatus.IN_PROGRESS, null, null));
        assertEquals("Закончить курсы", tm.getTaskById(11).getTitle());
        tm.update(new Subtask(e3s2.getId(), 0, "Стать джуном", "найти любую работу", TaskStatus.NEW, null, null));
        assertEquals("Стать джуном", tm.getTaskById(12).getTitle());
        tm.update(new Subtask(e3s3.getId(), 0, "Стать мидлом", "найти работу за хорошие деньги", TaskStatus.NEW, null, null));
        assertEquals("Стать мидлом", tm.getTaskById(13).getTitle());
        tm.removeById(e3s4.getId());
        assertNull(tm.getTaskById(14));
        tm.update(new Subtask(e3s5.getId(), 0, "Стать сеньором", "и уехать в долину", TaskStatus.NEW, null, null));
        assertEquals("Стать сеньором", tm.getTaskById(15).getTitle());

        System.out.println("======================== The User Behaviour Imitation ========================");
        tm.getTasks().forEach(System.out::println);
        tm.getEpics().stream()
                .peek(System.out::println)
                .forEach(e -> { tm.getSubTasks(e.getId()).forEach(s -> {
                    System.out.println("\t" + s.toString());
                }); });

        // non-existent tasks
        assertTrue(tm.getSubTasks(444).isEmpty());
        assertNull(tm.getTaskById(4568));
        assertNull(tm.getTaskById(8745));
        assertNull(tm.getTaskById(9852));

        // history
        for (int i = 0; i < 20; i++) tm.getTaskById(i);
        tm.getTaskById(6); tm.getTaskById(6);
        tm.getTaskById(5);
        tm.getTaskById(6); tm.getTaskById(6); tm.getTaskById(6); tm.getTaskById(6); tm.getTaskById(6);
        tm.getTaskById(7);
        List<Task> th = tm.getHistory();
        assertEquals(7, th.get(9).getId());
        assertEquals(6, th.get(8).getId());
        assertEquals(5, th.get(7).getId());
        assertEquals(15, th.get(6).getId());
        assertEquals(13, th.get(5).getId());
        assertEquals(12, th.get(4).getId());
        assertEquals(11, th.get(3).getId());
        assertEquals(10, th.get(2).getId());

        // remove demo
        tm.removeAllTasks();
        assertTrue(tm.getTasks().isEmpty());
        assertTrue(tm.getHistory().stream().noneMatch(t -> !(t instanceof Epic) && !(t instanceof Subtask)));

        tm.removeAllSubtasks();
        tm.getEpics().forEach(e -> { assertTrue(tm.getSubTasks(e.getId()).isEmpty()); });
        assertTrue(tm.getHistory().stream().noneMatch(s -> (s instanceof Subtask)));

        tm.removeAllEpics();
        assertTrue(tm.getEpics().isEmpty());
        assertTrue(tm.getHistory().isEmpty());
    }

    @Test
    void taskIntersections() {
        try {
            Task newtask = new Task(0, "new", "", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 1, 1), Duration.ofHours(5));
            Task task1 = taskManager.add(new Task(0, "1", "", TaskStatus.NEW, LocalDateTime.of(2000, 5, 5, 1, 1), Duration.ofHours(5)));
            assertDoesNotThrow(() -> {taskManager.checkIntersections(newtask);});
            Task task2 = taskManager.add(new Task(0, "2", "", TaskStatus.NEW, LocalDateTime.of(2000, 1, 1, 3, 21), Duration.ofHours(5)));
            assertThrows(TaskIntersectionException.class, () -> {taskManager.checkIntersections(newtask);});
            assertDoesNotThrow(() -> {taskManager.checkIntersections(task1);});
        } catch (TaskIntersectionException e) {
            assertNull(e);
        }
    }

}