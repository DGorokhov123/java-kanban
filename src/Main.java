import com.google.gson.Gson;
import manager.*;
import task.*;

public class Main {

    public static void main(String[] args) {
        TaskFactory taskFactory = Managers.getDefaultFactory();
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager tMan = Managers.getDefault(taskFactory, historyManager);

        optionalDemo(tMan);
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

        System.out.println("----------------------------НЕСУЩЕСТВУЮЩИЕ ТАСК, САБ, ЭПИК---------------------------");
        System.out.print(tMan.getTaskById(4568));
        System.out.print(tMan.getTaskById(8745));
        System.out.println(tMan.getTaskById(9852));

        System.out.println("---------------------ИСТОРИЯ ПРОСМОТРОВ - getHistory() ----------------------");
        for (int i = 0; i < 20; i++) tMan.getTaskById(i);
        tMan.getTaskById(15);
        tMan.getTaskById(15);
        int historyCounter = 1;
        if (tMan.getHistory() != null) for (Task task : tMan.getHistory()) {
            System.out.println(historyCounter + ". " + task.toString());
            historyCounter++;
        } else System.out.println("Пустой список");

        System.out.println("---------------------ВСЕ ПРОСТЫЕ ТАСКИ - после removeAllTasks() ----------------------");
        tMan.removeAllTasks();
        if (tMan.getTasks() != null) for (Task task : tMan.getTasks()) System.out.println(task);
        else System.out.println("Пустой список");

        System.out.println("---------------------ВСЕ ЭПИКИ - после removeAllSubtasks() ----------------------");
        tMan.removeAllSubtasks();
        if (tMan.getEpics() != null) for (Epic epic : tMan.getEpics())  System.out.println(epic);
        else System.out.println("Пустой список");

        System.out.println("---------------------ВСЕ ЭПИКИ - после removeAllEpics() ----------------------");
        tMan.removeAllEpics();
        if (tMan.getEpics() != null) for (Epic epic : tMan.getEpics())  System.out.println(epic);
        else System.out.println("Пустой список");
    }
}
