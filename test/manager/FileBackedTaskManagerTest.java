package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void readFromCSV() {
        TaskManager tMan = FileBackedTaskManager.loadFromFile(new File("testfile.csv"));
        assertEquals("Task", tMan.getTaskById(1).getTitle());
        assertEquals("Epic", tMan.getTaskById(2).getTitle());
        assertEquals("Subtask", tMan.getTaskById(3).getTitle());
        assertEquals("Epic", ((Subtask) tMan.getTaskById(3)).getEpic().getTitle()  );
    }

    @Test
    void addUpdateRemove() throws IOException {
        File file = File.createTempFile("tman", ".tmp");

        // ############################## ADD ##############################

        TaskManager tMan = FileBackedTaskManager.loadFromFile(file);
        tMan.add(new Task("t1", "", TaskStatus.NEW));
        Epic e1 = tMan.add(new Epic("e1", ""));
        tMan.add(new Subtask("s1", "", TaskStatus.DONE, e1.getId()));

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        assertEquals("e1", tMan.getTaskById(2).getTitle());
        assertEquals("s1", tMan.getTaskById(3).getTitle());
        assertEquals("e1", ((Subtask) tMan.getTaskById(3)).getEpic().getTitle()  );

        tMan = FileBackedTaskManager.loadFromFile(file);

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        assertEquals("e1", tMan.getTaskById(2).getTitle());
        assertEquals("s1", tMan.getTaskById(3).getTitle());
        assertEquals("e1", ((Subtask) tMan.getTaskById(3)).getEpic().getTitle()  );

        tMan.add(new Task("t2", "", TaskStatus.NEW));
        Epic e2 = tMan.add(new Epic("e2", ""));
        tMan.add(new Subtask("s2", "", TaskStatus.DONE, e2.getId()));

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

        tMan.update(new Task(4, "ut2", "", TaskStatus.IN_PROGRESS));
        tMan.update(new Epic(5, "ue2", ""));
        tMan.update(new Subtask(6, "us2", "", TaskStatus.DONE));

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

        assertNull(tMan.getTaskById(4));
        assertNull(tMan.getTaskById(5));
        assertNull(tMan.getTaskById(6));

        tMan = FileBackedTaskManager.loadFromFile(file);

        assertNull(tMan.getTaskById(4));
        assertNull(tMan.getTaskById(5));
        assertNull(tMan.getTaskById(6));

        // ############################## REMOVE ALL ##############################

        tMan.removeAllSubtasks();

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        assertEquals("e1", tMan.getTaskById(2).getTitle());
        assertNull(tMan.getTaskById(3));

        tMan = FileBackedTaskManager.loadFromFile(file);

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        assertEquals("e1", tMan.getTaskById(2).getTitle());
        assertNull(tMan.getTaskById(3));

        tMan.removeAllEpics();

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        assertNull(tMan.getTaskById(2));

        tMan = FileBackedTaskManager.loadFromFile(file);

        assertEquals("t1", tMan.getTaskById(1).getTitle());
        assertNull(tMan.getTaskById(2));

        tMan.removeAllTasks();

        assertNull(tMan.getTaskById(1));

        tMan = FileBackedTaskManager.loadFromFile(file);      // empty file

        assertNull(tMan.getTaskById(1));
    }

}