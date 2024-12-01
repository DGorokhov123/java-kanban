package manager;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    // Ну конечно private, спасибо что заметили!
    final private LinkedList<Task> history = new LinkedList<>();
    final private int historySize;

    public InMemoryHistoryManager(int historySize) {
        if (historySize < 1) throw new IllegalArgumentException("Parameter 'historySize' cannot be less than 1");
        this.historySize = historySize;
    }

    @Override
    public boolean add(Task task) {
        if (task == null) return false;
        history.addLast(task);
        if (history.size() > historySize) history.pollFirst();
        return true;
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
