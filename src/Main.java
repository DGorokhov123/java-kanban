import manager.*;
import task.*;

/**
 * Далее в таких комментах - цитаты из ТЗ
 */
public class Main {

    /**
     * Вы можете добавить консольный вывод для самопроверки в класcе Main, но на работу методов он влиять не должен.
     */
    public static void main(String[] args) {
        TaskFactory tFac = new TaskFactory();
        TaskManager tMan = new TaskManager();
        Epic e;
        Subtask s;
        Task t;

        // generate some task data
        t = tFac.newTask();
        t.setTitle("Переезд");
        t.setDescription("Собрать коробки, Упаковать кошку, Сказать слова прощания");
        t.setStatus(TaskStatus.DONE);
        int taskToShow = t.getId();
        tMan.addTask(t);

        e = tFac.newEpic("Наладить жизнь в России");
        System.out.println("-----------------------ПУСТОЙ ЭПИК-------------------------");
        System.out.println(e);
        int russiaId = e.getId();
        s = tFac.newSubtask("Запретить все плохое", "Составить реестр плохого, принять законы");
        int allBadId = s.getId();
        System.out.println("-----------------------ПУСТОЙ САБТАСК-------------------------");
        System.out.println(s);
        int russiaSubtaskToShow = s.getId();
        e.addSubtask(s);
        s = tFac.newSubtask("Избрать Трампа", "Сделать Дони президентом США");
        int trampId = s.getId();
        e.addSubtask(s);
        s = tFac.newSubtask("Снизить налоги", "и упразднить Роскомнадзор");
        int russiaSubtaskToRemove = s.getId();
        e.addSubtask(s);
        e.addSubtask(tFac.newSubtask("Рожать больше детей", "Достичь показателя 3 ребенка на 1 женщину"));
        tMan.addTask(e);

        //tMan.removeAllTasks();     // you can remove all the tasks above

        tMan.addTask(tFac.newTask("Купить машину", "Выбрать модель и заказать в автосалоне"));

        t = tFac.newTask("Бессмысленное занятие", "Удалим его поскорее");
        int taskToDelete = t.getId();
        tMan.addTask(t);

        e = tFac.newEpic("Стать крутым прогером");
        s = tFac.newSubtask("Закончить курсы", "например Яндекс Практикум");
        s.setStatus(TaskStatus.IN_PROGRESS);
        e.addSubtask(s);
        e.addSubtask(s);
        e.addSubtask(s);
        e.addSubtask(tFac.newSubtask("Стать джуном", "найти любую работу"));
        e.addSubtask(tFac.newSubtask("Стать мидлом", "найти работу за деньги"));
        e.addSubtask(tFac.newSubtask("Стать сеньором", "и уехать на Бали"));
        tMan.addTask(e);

        e = tFac.newEpic("Этот эпик мы будем удалять");
        int epicIdToRemove = e.getId();
        e.addSubtask(tFac.newSubtask("Неважно", "что мы тут напишем"));
        e.addSubtask(tFac.newSubtask("все равно это", "будет удалено"));
        tMan.addTask(e);

        // add and remove epic without subtasks
        e = tFac.newEpic("Это эпик без сабтасков");
        int epicWithoutSubtasks = e.getId();
        tMan.addTask(e);
        tMan.removeTaskById(epicWithoutSubtasks);

        // change subtask status
        tMan.getTaskById(allBadId).setStatus(TaskStatus.DONE);

        // get epic by id and change subtask status
        tMan.getSubTasks(russiaId).get(1).setStatus(TaskStatus.DONE);

        // remove epic, task, subtask
        tMan.removeTaskById(epicIdToRemove);
        tMan.removeTaskById(taskToDelete);
        tMan.removeTaskById(russiaSubtaskToRemove);

        System.out.println("----------------------------ВСЕ ТАСКИ С ПОДТАСКАМИ - getTasks() ---------------------------");
        for (Task task : tMan.getTasks())  System.out.println(task);

        System.out.println("----------------------------КОНКРЕТНЫЙ ТАСК ПО ID---------------------------");
        System.out.println(tMan.getTaskById(taskToShow));
        System.out.println("----------------------------КОНКРЕТНЫЙ САБТАСК ПО ID---------------------------");
        System.out.println(tMan.getTaskById(russiaSubtaskToShow));
        System.out.println("----------------------------КОНКРЕТНЫЙ ЭПИК ПО ID---------------------------");
        System.out.println(tMan.getTaskById(russiaId));

        System.out.println("----------------------------НЕСУЩЕСТВУЮЩИЕ ТАСК, САБ, ЭПИК---------------------------");
        System.out.println(tMan.getTaskById(4568));
        System.out.println(tMan.getTaskById(8745));
        System.out.println(tMan.getTaskById(9852));

        System.out.println("----------------------------ВСЕ ЭПИКИ - getEpics() ---------------------------");
        for (Epic epic : tMan.getEpics()) System.out.println(epic);

        System.out.println("--------------------ВСЕ САБТАСКИ (Россия) - getSubTasks(int epicId) ------------------------");
        for (Subtask subtask : tMan.getSubTasks(russiaId)) System.out.println(subtask);


    }
}
