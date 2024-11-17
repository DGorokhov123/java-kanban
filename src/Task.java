import java.util.Objects;

/**
 * Далее в таких комментах - цитаты из ТЗ
 * Простейший кирпичик трекера — задача (англ. task)
 * Для эталонного решения мы выбрали создание публичного не абстрактного класса Task,
 * который представляет отдельно стоящую задачу.
 * В нашем задании класс Task можно использовать сам по себе, не делая его абстрактным.
 */
public class Task {
    /**
     * Уникальный идентификационный номер задачи, по которому её можно будет найти.
     */
    protected final int id;
    /**
     * Название, кратко описывающее суть задачи (например, «Переезд»).
     */
    protected String title;
    /**
     * Описание, в котором раскрываются детали.
     */
    protected String description;
    /**
     * Статус, отображающий её прогресс. Вы будете выделять следующие этапы жизни задачи, используя enum:
     * 1. NEW — задача только создана, но к её выполнению ещё не приступили.
     * 2. IN_PROGRESS — над задачей ведётся работа.
     * 3. DONE — задача выполнена.
     */
    protected TaskStatus status;


    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
     */
    @Override
    public String toString() {
        return "Task #" + id + "\t[" + status + "]\t" + title + " (" + description + ")";
    }

    /**
     * Также советуем применить знания о методах equals() и hashCode(), чтобы реализовать идентификацию задачи по её id.
     * При этом две задачи с одинаковым id должны выглядеть для менеджера как одна и та же.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
