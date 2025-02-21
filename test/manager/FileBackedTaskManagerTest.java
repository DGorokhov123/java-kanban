package manager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import task.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    protected FileBackedTaskManagerTest() throws IOException {
        super(FileBackedTaskManager.loadFromFile(File.createTempFile("tman1", ".tmp")));
    }

    @Test
    void readFromCSV() throws TaskNotFoundException {
        TaskManager tMan = FileBackedTaskManager.loadFromFile(new File("testfile.csv"));
        assertEquals("Task", tMan.getTaskById(1).getTitle());
        assertEquals("Epic", tMan.getTaskById(2).getTitle());
        assertEquals("Subtask", tMan.getTaskById(3).getTitle());
        assertEquals("Epic", ((Subtask) tMan.getTaskById(3)).getEpic().getTitle()  );
    }

    @Test
    void addUpdateRemove() throws IOException, TaskNotFoundException, WrongTaskArgumentException, TaskIntersectionException {
        File file = File.createTempFile("tman", ".tmp");

        // ############################## ADD ##############################

        TaskManager tMan = FileBackedTaskManager.loadFromFile(file);
        tMan.add(new Task(0, "t1", "", TaskStatus.NEW, null, null));
        Epic e1 = tMan.add(new Epic(0, "e1", ""));
        tMan.add(new Subtask(0, e1.getId(), "s1", "", TaskStatus.DONE, null, null));

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        assertEquals("e1", tMan.getTaskById(2).getTitle());
        assertEquals("s1", tMan.getTaskById(3).getTitle());
        assertEquals("e1", ((Subtask) tMan.getTaskById(3)).getEpic().getTitle()  );

        tMan = FileBackedTaskManager.loadFromFile(file);

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        assertEquals("e1", tMan.getTaskById(2).getTitle());
        assertEquals("s1", tMan.getTaskById(3).getTitle());
        assertEquals("e1", ((Subtask) tMan.getTaskById(3)).getEpic().getTitle()  );

        tMan.add(new Task(0, "t2", "", TaskStatus.NEW, null, null));
        Epic e2 = tMan.add(new Epic(0, "e2", ""));
        tMan.add(new Subtask(0, e2.getId(), "s2", "", TaskStatus.DONE, null, null));

        assertEquals("t2", tMan.getTaskById(4).getTitle());
        assertEquals("e2", tMan.getTaskById(5).getTitle());
        assertEquals("s2", tMan.getTaskById(6).getTitle());
        assertEquals("e2", ((Subtask) tMan.getTaskById(6)).getEpic().getTitle()  );

        tMan = FileBackedTaskManager.loadFromFile(file);

        assertEquals("t2", tMan.getTaskById(4).getTitle());
        assertEquals("e2", tMan.getTaskById(5).getTitle());
        assertEquals("s2", tMan.getTaskById(6).getTitle());
        assertEquals("e2", ((Subtask) tMan.getTaskById(6)).getEpic().getTitle()  );

        // ############################## UPDATE ##############################

        tMan.update(new Task(4, "ut2", "", TaskStatus.IN_PROGRESS, null, null));
        tMan.update(new Epic(5, "ue2", ""));
        tMan.update(new Subtask(6, 0, "us2", "", TaskStatus.DONE, null, null));

        assertEquals("ut2", tMan.getTaskById(4).getTitle());
        assertEquals("ue2", tMan.getTaskById(5).getTitle());
        assertEquals("us2", tMan.getTaskById(6).getTitle());
        assertEquals("ue2", ((Subtask) tMan.getTaskById(6)).getEpic().getTitle()  );

        tMan = FileBackedTaskManager.loadFromFile(file);

        assertEquals("ut2", tMan.getTaskById(4).getTitle());
        assertEquals("ue2", tMan.getTaskById(5).getTitle());
        assertEquals("us2", tMan.getTaskById(6).getTitle());
        assertEquals("ue2", ((Subtask) tMan.getTaskById(6)).getEpic().getTitle()  );

        // ############################## REMOVE ##############################

        tMan.removeById(6);
        tMan.removeById(5);
        tMan.removeById(4);

        TaskManager finalTMan = tMan;
        assertThrows(TaskNotFoundException.class, () -> {
            finalTMan.getTaskById(4);
        });
        assertThrows(TaskNotFoundException.class, () -> {
            finalTMan.getTaskById(5);
        });
        assertThrows(TaskNotFoundException.class, () -> {
            finalTMan.getTaskById(6);
        });

        tMan = FileBackedTaskManager.loadFromFile(file);

        TaskManager final1TMan = tMan;
        assertThrows(TaskNotFoundException.class, () -> {
            final1TMan.getTaskById(4);
        });
        assertThrows(TaskNotFoundException.class, () -> {
            final1TMan.getTaskById(5);
        });
        assertThrows(TaskNotFoundException.class, () -> {
            final1TMan.getTaskById(6);
        });

        // ############################## REMOVE ALL ##############################

        tMan.removeAllSubtasks();

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        assertEquals("e1", tMan.getTaskById(2).getTitle());
        TaskManager final2TMan = tMan;
        assertThrows(TaskNotFoundException.class, () -> {
            final2TMan.getTaskById(3);
        });

        tMan = FileBackedTaskManager.loadFromFile(file);

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        assertEquals("e1", tMan.getTaskById(2).getTitle());
        TaskManager final3TMan = tMan;
        assertThrows(TaskNotFoundException.class, () -> {
            final3TMan.getTaskById(3);
        });

        tMan.removeAllEpics();

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        TaskManager final4TMan = tMan;
        assertThrows(TaskNotFoundException.class, () -> {
            final4TMan.getTaskById(2);
        });

        tMan = FileBackedTaskManager.loadFromFile(file);

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        TaskManager final5TMan = tMan;
        assertThrows(TaskNotFoundException.class, () -> {
            final5TMan.getTaskById(2);
        });

        tMan.removeAllTasks();

        TaskManager final6TMan = tMan;
        assertThrows(TaskNotFoundException.class, () -> {
            final6TMan.getTaskById(1);
        });

        tMan = FileBackedTaskManager.loadFromFile(file);      // empty file

        TaskManager final7TMan = tMan;
        assertThrows(TaskNotFoundException.class, () -> {
            final7TMan.getTaskById(1);
        });
    }

    @Test
    void fileBackedMakeFile() throws TaskIntersectionException, WrongTaskArgumentException, TaskNotFoundException {
        FileBackedTaskManager tm = new FileBackedTaskManager(new TaskFactory(), new InMemoryHistoryManager(10), new File("file1.csv"));

        tm.add(new Task(0, "Праздновать новый год", "всю ночь", TaskStatus.DONE, LocalDateTime.of(2024,12,31,21,0), Duration.ofHours(12)));
        tm.add(new Task(0, "Отоспаться после празднования", "", TaskStatus.NEW, LocalDateTime.of(2025,1,1,9,0), Duration.ofHours(12)));
        tm.add(new Task(0, "Похудеть и накачаться", "", TaskStatus.NEW, null, null));
        tm.add(new Task(0, "Неплохо бы еще попраздновать", "", TaskStatus.NEW, LocalDateTime.of(2025,1,3,21,0), Duration.ofHours(12)));
        tm.add(new Task(0, "И уже окончательно отоспаться", "", TaskStatus.NEW, LocalDateTime.of(2025,1,4,9,0), Duration.ofHours(12)));
        tm.add(new Task(0, "Найти новую работу", "", TaskStatus.NEW, null, null));
        Epic e1 = tm.add(new Epic(0, "Начать новую жизнь", "с нового года"));
        tm.add(new Subtask(0, e1.getId(), "Встать в 7 утра", "сделать зарядку", TaskStatus.NEW, LocalDateTime.of(2025,1,2,7,0), Duration.ofHours(1)));
        tm.add(new Subtask(0, e1.getId(), "программировать", "крутейший пет-проект", TaskStatus.NEW, LocalDateTime.of(2025, 1, 2, 8 ,0), Duration.ofHours(6)));
        tm.add(new Subtask(0, e1.getId(), "сходить погулять", "с детьми", TaskStatus.NEW, LocalDateTime.of(2025, 1, 2, 15 ,0), Duration.ofHours(2)));
        tm.add(new Task(0, "Выпьем пивка", "под киношечку", TaskStatus.NEW, LocalDateTime.of(2025,1,2,19,0), Duration.ofHours(2)));

        FileBackedTaskManager tm2 = (FileBackedTaskManager) Managers.createNewFromFile(new TaskFactory(), new InMemoryHistoryManager(10), new File("file1.csv"));
        List<Task> tm1Tasks = tm.getAllRecords();
        List<Task> tm2Tasks = tm2.getAllRecords();
        for (int i = 0; i < tm1Tasks.size(); i++) {
            assertEquals(tm1Tasks.get(i).getId(), tm2Tasks.get(i).getId());
            assertEquals(tm1Tasks.get(i).getTitle(), tm2Tasks.get(i).getTitle());
            assertEquals(tm1Tasks.get(i).getDescription(), tm2Tasks.get(i).getDescription());
            assertEquals(tm1Tasks.get(i).getStartTime(), tm2Tasks.get(i).getStartTime());
            assertEquals(tm1Tasks.get(i).getStatus(), tm2Tasks.get(i).getStatus());
            assertEquals(tm1Tasks.get(i).getDuration(), tm2Tasks.get(i).getDuration());
            assertEquals(tm1Tasks.get(i).getClass(), tm2Tasks.get(i).getClass());
        }
        // Show information
        System.out.println("======================== Запись и чтение из файла ========================");
        tm2.getPrioritizedTasks().forEach(System.out::println);
    }

    @Test
    void loadException() throws IOException {
        File file = File.createTempFile("tman3", ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ololo\ntrololo");
        } catch (IOException e) {
            // do nothing
        }
        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    void saveException() throws IOException {
        File file = File.createTempFile("tman4", ".tmp");
        file.setReadOnly();
        TaskManager tMan = FileBackedTaskManager.loadFromFile(file);
        Task tsk = new Task(0, "t1", "", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(3));
        assertThrows(ManagerSaveException.class, () -> tMan.add(tsk));
    }


}