package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{

    final private Map<Integer, Node> history = new HashMap<>();
    final private int historySize;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        this.historySize = 0;                                // Endless history
    }

    public InMemoryHistoryManager(int historySize) {
        if (historySize < 1) throw new IllegalArgumentException("Size of history cannot be less than 1");
        this.historySize = historySize;
    }

    @Override
    public boolean add(Task task) {
        if (task == null) return false;
        if (history.containsKey(task.getId()))  remove(task.getId());                       // O(1) + O(1)
        linkLast(task);                                                                     // O(1)
        history.put(task.getId(), tail);                                                    // O(1)
        if (historySize > 0 && history.size() > historySize)  remove(head.task.getId());    // O(1)
        return true;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node curNode = head;
        while (curNode != null) {                                         // O(n)
            historyList.add(curNode.task);
            curNode = curNode.next;
        }
        return historyList;
    }

    @Override
    public boolean remove(int id) {
        Node nodeToRemove = history.get(id);       // O(1)
        if (nodeToRemove == null) return false;
        removeNode(nodeToRemove);                  // O(1)
        history.remove(id);                        // O(1)
        return true;
    }

    private boolean linkLast(Task task) {
        if (task == null) return false;
        if (tail == null) {                               // empty chain
            head = new Node(null, task, null);
            tail = head;
        } else {
            tail.next = new Node(tail, task, null);
            tail = tail.next;
        }
        return true;
    }

    private boolean removeNode(Node node) {
        if (node == null) return false;
        if (node == head && head == tail) {                         // single element in chain
            head = null;
            tail = null;
        } else if (node == head) {                                  // first in chain
            head = head.next;
            head.prev = null;
        } else if (node == tail) {                                  // last in chain
            tail = tail.prev;
            tail.next = null;
        } else if (node.prev != null && node.next != null) {        // in the middle of the chain
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else {                                                    // incorrect node not from chain
            return false;
        }
        return true;
    }

    private static class Node {
        Task task;
        Node next;
        Node prev;
        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }


}
