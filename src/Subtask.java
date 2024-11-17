/**
 * Далее в таких комментах - цитаты из ТЗ
 * Иногда для выполнения какой-нибудь масштабной задачи её лучше разбить на подзадачи (англ. subtask).
 * Для эталонного решения мы выбрали создание публичного не абстрактного класса Task,
 * который представляет отдельно стоящую задачу. Его данные наследуют подклассы Subtask и Epic.
 * Для подклассов Subtask и Epic наследуем сразу имплементацию, поскольку нам понадобится такое расширение
 * функциональности, которое совместимо с базовым классом и не отличается от него по поведению.
 */
public class Subtask extends Task{
    /**
     * Для каждой подзадачи известно, в рамках какого эпика она выполняется.
     */
    private Epic epic;

    public Subtask(int id, String title, String description) {
        super(id, title, description);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    /**
     * Когда меняется статус любой подзадачи в эпике, вам необходимо проверить, что статус эпика изменится
     * соответствующим образом. При этом изменение статуса эпика может и не произойти, если в нём,
     * к примеру, всё ещё есть незакрытые задачи.
     */
    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
        if (epic != null)  epic.calculateStatus();
    }

    /**
     * Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
     */
    @Override
    public String toString() {
        String epicId = (epic == null) ? "null" : Integer.toString(epic.getId());
        return "\tSubtask #" + id + ", epic #" + epicId + "\t[" + status + "]\t"
                + title + " (" + description + ")";
    }
}
