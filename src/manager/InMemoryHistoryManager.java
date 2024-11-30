package manager;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    LinkedList<Task> history = new LinkedList<>();
    final int historySize;

    public InMemoryHistoryManager(int historySize) {
        if (historySize < 1) throw new IllegalArgumentException("Parameter 'historySize' cannot be less than 1");
        this.historySize = historySize;
    }

    @Override
    public int add(Task task) {
        if (task == null) return -1;
        history.addLast(task);
        if (history.size() > historySize) history.pollFirst();
        return 0;
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
