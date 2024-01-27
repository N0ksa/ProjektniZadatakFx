package hr.java.project.projectfxapp.utility.manager;

import hr.java.project.projectfxapp.entities.Change;

import java.util.ArrayList;
import java.util.List;

public class ChangesManager {
    private static List<Change> changes;

    public ChangesManager() {
    }

    public static List<Change> setNewChangesIfChangesNotPresent() {
        if (changes == null) {
            changes = new ArrayList<>();
        }
        return changes;
    }

    public static void clearChanges() {
       changes.clear();
    }
}


