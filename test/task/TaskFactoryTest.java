package task;

import exception.WrongCSVLineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskFactoryTest {

    TaskFactory taskFactory;

    @BeforeEach
    void setUp() {
        taskFactory = new TaskFactory();
    }

    @Test
    void newTaskEpicSubtask() {
        Task t1 = new Task(0, "First title", "First description", TaskStatus.DONE, LocalDateTime.now(), Duration.ofHours(3));
        Task t2 = taskFactory.newTask(t1);
        assertEquals(1, t2.getId());
        assertEquals("First title", t2.getTitle());
        assertEquals("First description", t2.getDescription());
        assertEquals(TaskStatus.DONE, t2.getStatus());

        Epic e1 = new Epic(0, "Second title", "Second description");
        Epic e2 = taskFactory.newEpic(e1);
        assertEquals(2, e2.getId());
        assertEquals("Second title", e2.getTitle());
        assertEquals("Second description", e2.getDescription());
        assertEquals(TaskStatus.NEW, e2.getStatus());

        Subtask s1 = new Subtask(0, 2, "Third title", "Third description", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(3));
        Subtask s2 = taskFactory.newSubtask(s1);
        assertEquals(3, s2.getId());
        assertEquals("Third title", s2.getTitle());
        assertEquals("Third description", s2.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, s2.getStatus());
    }

    @Test
    void nullTests() {
        assertNull(taskFactory.newTask(null));
        assertNull(taskFactory.newEpic(null));
        assertNull(taskFactory.newSubtask(null));
    }

    @Test
    void fromToCSVLine() throws WrongCSVLineException {
        LocalDateTime time = LocalDateTime.now();
        Task t1 = taskFactory.newTask( new Task(0, "First title", "First description", TaskStatus.DONE, time, Duration.ofHours(3)) );
        Epic e1 = taskFactory.newEpic( new Epic(0, "Second title", "Second description") );
        Subtask s1 = taskFactory.newSubtask( new Subtask(0, e1.getId(), "Third title", "Third description", TaskStatus.IN_PROGRESS, time.plusDays(1), Duration.ofHours(3)) );
        e1.linkSubtask(s1);
        String t1Line = t1.toCSVLine();
        String e1Line = e1.toCSVLine();
        String s1line = s1.toCSVLine();
        taskFactory.clear();
        Task t2 = taskFactory.fromCSVLine(t1Line);
        Task e2 = taskFactory.fromCSVLine(e1Line);
        Task s2 = taskFactory.fromCSVLine(s1line);
        assertEquals(t1.getTitle(), t2.getTitle());
        assertEquals(t1.getDescription(), t2.getDescription());
        assertEquals(t1.getStatus(), t2.getStatus());
        assertEquals(t1.getStartTime().format(Task.DATE_TIME_FORMATTER), t2.getStartTime().format(Task.DATE_TIME_FORMATTER));
        assertEquals(t1.getDuration(), t2.getDuration());
        assertEquals(t1.getEndTime().format(Task.DATE_TIME_FORMATTER), t2.getEndTime().format(Task.DATE_TIME_FORMATTER));
        assertEquals(t1.getId(), t2.getId());

    }

    @Test
    void throwExceptions() {
        String good = "\"1\",\"TASK\",\"Праздновать новый год\",\"DONE\",\"всю ночь\",\"31.12.2024 21:00\",\"43200\"";
        String lackOfElements = "\"1\",\"TASK\",\"Праздновать новый год\",\"DONE\",\"всю ночь\",\"31.12.2024 21:00\"";
        String badID = "\"b\",\"TASK\",\"Праздновать новый год\",\"DONE\",\"всю ночь\",\"31.12.2024 21:00\",\"43200\"";
        String badType = "\"1\",\"TUSK\",\"Праздновать новый год\",\"DONE\",\"всю ночь\",\"31.12.2024 21:00\",\"43200\"";
        String badStatus = "\"1\",\"TASK\",\"Праздновать новый год\",\"DUNE\",\"всю ночь\",\"31.12.2024 21:00\",\"43200\"";
        String badTime = "\"1\",\"TASK\",\"Праздновать новый год\",\"DONE\",\"всю ночь\",\"31.12202421:00\",\"43200\"";
        String badDura = "\"1\",\"TASK\",\"Праздновать новый год\",\"DONE\",\"всю ночь\",\"31.12.2024 21:00\",\"43k00\"";
        String noQuote = "\"1\",\"TASK,\"Праздновать новый год\",\"DONE\",\"всю ночь\",\"31.12.2024 21:00\",\"43200\"";
        String noComma = "\"1\",\"TASK\",\"Праздновать новый год\",\"DONE\"\"всю ночь\",\"31.12.2024 21:00\",\"43200\"";
        String nonsense = "sdfrkgjvnltg";
        String empty = "";
        assertDoesNotThrow(() -> taskFactory.fromCSVLine(good));
        assertThrows(WrongCSVLineException.class, () -> taskFactory.fromCSVLine(lackOfElements));
        assertThrows(WrongCSVLineException.class, () -> taskFactory.fromCSVLine(badID));
        assertThrows(WrongCSVLineException.class, () -> taskFactory.fromCSVLine(badType));
        assertThrows(WrongCSVLineException.class, () -> taskFactory.fromCSVLine(badStatus));
        assertThrows(WrongCSVLineException.class, () -> taskFactory.fromCSVLine(badTime));
        assertThrows(WrongCSVLineException.class, () -> taskFactory.fromCSVLine(badDura));
        assertThrows(WrongCSVLineException.class, () -> taskFactory.fromCSVLine(noQuote));
        assertThrows(WrongCSVLineException.class, () -> taskFactory.fromCSVLine(noComma));
        assertThrows(WrongCSVLineException.class, () -> taskFactory.fromCSVLine(nonsense));
        assertThrows(WrongCSVLineException.class, () -> taskFactory.fromCSVLine(empty));
    }

}