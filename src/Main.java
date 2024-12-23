import com.google.gson.Gson;
import manager.*;
import task.*;

import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        TaskFactory taskFactory = Managers.getDefaultFactory();
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager tMan = Managers.getDefault(taskFactory, historyManager);

        //optionalDemo(tMan);
        continuousDemo(tMan);
    }

    private static void continuousDemo(TaskManager tMan) {
        Random rnd = new Random();
        int choice = 0;
        int lastID = 0;
        int lastEpicID = 0;

        for (int i = 0; i < 100000; i++) {
            choice = rnd.nextInt(30);
            if (choice == 0) {
                lastID = tMan.add(new Task("Task", "created on step " + i, TaskStatus.NEW)).getId();
            } else if (choice == 1) {
                lastID = tMan.add(new Epic("Epic", "created on step " + i)).getId();
                lastEpicID = lastID;
            } else if (choice <= 3 && lastEpicID > 0) {
                lastID = tMan.add(new Subtask("Subtask", "created on step " + i, TaskStatus.NEW, lastEpicID)).getId();
            } else if (choice <= 5 && lastID > 0) {
                int rndRes = rnd.nextInt(lastID);
                Task tsk = tMan.getTaskById(rndRes);
                if (tsk instanceof Epic) {
                    tMan.update(new Epic(rndRes, "Epic", "viewed on step " + i));
                } else if (tsk instanceof Subtask) {
                    tMan.update(new Subtask(rndRes, "Subtask", "viewed on step " + i, TaskStatus.DONE));
                } else if (tsk instanceof Task) {
                    tMan.update(new Task(rndRes, "Task", "viewed on step " + i, TaskStatus.DONE));
                }
            } else {
                int rndRes = rnd.nextInt(lastID + 1);
                if (rndRes != lastEpicID) tMan.removeById(rndRes);
            }
        }

        int historyCounter = 1;
        for (Task task : tMan.getHistory()) System.out.println(historyCounter++ + ". " + task.toString());
    }


    private static void optionalDemo(TaskManager tMan) {
        Gson gson = new Gson();

        // Simple task #1 - from json
        String json1 = "{\"id\":0,\"title\":\"Десериализация\",\"description\":\"этот таск добавлен из json\",\"status\":\"IN_PROGRESS\"}";
        tMan.add(gson.fromJson(json1, Task.class));

        // Simple task #2 - add + update
        Task t2 = tMan.add(new Task("Старый тайтл", "метод update не сработал!", TaskStatus.NEW));
        tMan.update(new Task(t2.getId(), "Новый тайтл", "метод update успешно отработал", TaskStatus.IN_PROGRESS));
        tMan.update(new Task(t2.getId(), "Новый тайтл", "метод update успешно отработал", TaskStatus.DONE));

        // Simple task #3 - add + delete
        Task t3 = tMan.add(new Task("Удаление таска", "это должно быть удалено", TaskStatus.NEW));
        tMan.removeById(t3.getId());

        // Epic #1 - from json
        Epic e1 = tMan.add(gson.fromJson("{\"subtasks\":{},\"id\":0," +
                "\"title\":\"Наладить жизнь в России\",\"description\":\"\",\"status\":\"NEW\"}", Epic.class));
        tMan.add(gson.fromJson("{\"epicId\":" + e1.getId() + ",\"id\":0,\"title\":\"Запретить все плохое\"," +
                "\"description\":\"Составить реестр плохого, принять законы\",\"status\":\"DONE\"}", Subtask.class));
        tMan.add(gson.fromJson("{\"epicId\":" + e1.getId() + ",\"id\":0,\"title\":\"Избрать Трампа\"," +
                "\"description\":\"Сделать Дони президентом США\",\"status\":\"DONE\"}", Subtask.class));
        tMan.add(gson.fromJson("{\"epicId\":" + e1.getId() + ",\"id\":0,\"title\":\"Укрепить рубль\"," +
                "\"description\":\"и уменьшить ключевую ставку\",\"status\":\"NEW\"}", Subtask.class));

        // Epic #2 - add + delete
        Epic e2 = tMan.add(new Epic("Проверить удаление эпиков", ""));
        tMan.add(new Subtask("а также", "всех его сабтасков", TaskStatus.NEW, e2.getId()));
        tMan.removeById(e2.getId());

        // Epic #3 - add + update + delete subtask
        Epic e3 = tMan.add(new Epic("Название эпика надо изменить", ""));
        Subtask e3s1 = tMan.add(new Subtask("этот сабтаск", "нужно изменить", TaskStatus.NEW, e3.getId()));
        Subtask e3s2 = tMan.add(new Subtask("этот сабтаск", "нужно изменить", TaskStatus.NEW, e3.getId()));
        Subtask e3s3 = tMan.add(new Subtask("этот сабтаск", "нужно изменить", TaskStatus.NEW, e3.getId()));
        Subtask e3s4 = tMan.add(new Subtask("а вот этот сабтаск", "нужно удалить", TaskStatus.NEW, e3.getId()));
        Subtask e3s5 = tMan.add(new Subtask("этот сабтаск", "нужно изменить", TaskStatus.NEW, e3.getId()));
        tMan.update(new Epic(e3.getId(), "Стать крутым прогером", "план надежный как швейцарские часы"));
        tMan.update(new Subtask(e3s1.getId(), "Закончить курсы", "например Яндекс Практикум", TaskStatus.IN_PROGRESS));
        tMan.update(new Subtask(e3s2.getId(), "Стать джуном", "найти любую работу", TaskStatus.NEW));
        tMan.update(new Subtask(e3s3.getId(), "Стать мидлом", "найти работу за деньги", TaskStatus.NEW));
        tMan.removeById(e3s4.getId());
        tMan.update(gson.fromJson("{\"epicId\":0,\"id\":" + e3s5.getId() + ",\"title\":\"Стать сеньором\"," +
                "\"description\":\"и уехать на Бали\",\"status\":\"NEW\"}", Subtask.class));


        // Show information
        System.out.println("----------------------------ВСЕ ПРОСТЫЕ ТАСКИ - getTasks() ---------------------------");
        for (Task task : tMan.getTasks())  System.out.println(task);

        System.out.println("----------------------------ВСЕ ЭПИКИ С САБТАСКАМИ - getEpics() + getSubTasks()---------");
        for (Epic epic : tMan.getEpics()) {
            System.out.println(epic);
            for (Subtask subtask : tMan.getSubTasks(epic.getId()))  System.out.println("\t" + subtask.toString());
        }

        System.out.println("----------------------------САБТАСК НЕСУЩЕСТВУЮЩЕГО ЭПИКА - getSubTasks(444)---------");
        for (Subtask subtask : tMan.getSubTasks(444))  System.out.println("\t" + subtask.toString());

        System.out.println("----------------------------НЕСУЩЕСТВУЮЩИЕ ТАСК, САБ, ЭПИК---------------------------");
        System.out.print(tMan.getTaskById(4568));            System.out.print(" ");
        System.out.print(tMan.getTaskById(8745));            System.out.print(" ");
        System.out.println(tMan.getTaskById(9852));

        System.out.println("---------------------ИСТОРИЯ ПРОСМОТРОВ - getHistory() ----------------------");
        for (int i = 0; i < 20; i++) tMan.getTaskById(i);
        tMan.getTaskById(6); tMan.getTaskById(6);
        tMan.getTaskById(5);
        tMan.getTaskById(6); tMan.getTaskById(6); tMan.getTaskById(6); tMan.getTaskById(6); tMan.getTaskById(6);
        tMan.getTaskById(7);
        int historyCounter = 1;
        for (Task task : tMan.getHistory()) System.out.println(historyCounter++ + ". " + task.toString());

        System.out.println("---------------------ВСЕ ПРОСТЫЕ ТАСКИ - после removeAllTasks() ----------------------");
        tMan.removeAllTasks();
        for (Task task : tMan.getTasks()) System.out.println(task);

        System.out.println("---------------------ИСТОРИЯ ПРОСМОТРОВ - после removeAllTasks() ----------------------");
        historyCounter = 1;
        for (Task task : tMan.getHistory()) System.out.println(historyCounter++ + ". " + task.toString());

        System.out.println("---------------------ВСЕ ЭПИКИ С САБТАСКАМИ - после removeAllSubtasks() ----------------------");
        tMan.removeAllSubtasks();
        for (Epic epic : tMan.getEpics()) {
            System.out.println(epic);
            for (Subtask subtask : tMan.getSubTasks(epic.getId()))  System.out.println("\t" + subtask.toString());
        }

        System.out.println("---------------------ИСТОРИЯ ПРОСМОТРОВ - после removeAllSubtasks() ----------------------");
        historyCounter = 1;
        for (Task task : tMan.getHistory()) System.out.println(historyCounter++ + ". " + task.toString());

        System.out.println("---------------------ВСЕ ЭПИКИ - после removeAllEpics() ----------------------");
        tMan.removeAllEpics();
        for (Epic epic : tMan.getEpics())  System.out.println(epic);

        System.out.println("---------------------ИСТОРИЯ ПРОСМОТРОВ - после removeAllEpics() ----------------------");
        historyCounter = 1;
        for (Task task : tMan.getHistory()) System.out.println(historyCounter++ + ". " + task.toString());

    }
}
