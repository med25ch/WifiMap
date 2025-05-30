package src.main.java.org.example.domain.historymanager;

import org.example.domain.savemanager.Project;

import java.util.ArrayList;
import java.util.List;

import static org.example.domain.savemanager.SaveManager.deepCopy;

public class HistoryManager {
    private final List<Project> history = new ArrayList<>();
    private int idx = -1;

    public HistoryManager() {}

    public void addState(Project state) {
        if (idx > -1 && idx != history.size() - 1) {
            history.subList(idx + 1, history.size()).clear();
        }
        history.add(deepCopy(state));
        idx = history.size() -1;
    }

    public Project getCurrentState() throws IndexOutOfBoundsException {
        return deepCopy(history.get(idx));
    }

    public Project getLastState() throws IndexOutOfBoundsException {
        if (idx <= 0) throw new IndexOutOfBoundsException();
        idx -= 1;
        return deepCopy(history.get(idx));
    }

    public Project getNextState() throws IndexOutOfBoundsException {
        if (idx >= history.size()-1) throw new IndexOutOfBoundsException();
        idx += 1;
        return deepCopy(history.get(idx));

    }

    public void reset() {
        history.clear();
        idx = -1;
    }
}
