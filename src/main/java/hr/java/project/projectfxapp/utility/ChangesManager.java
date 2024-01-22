package hr.java.project.projectfxapp.utility;

import hr.java.project.projectfxapp.entities.Change;

import java.util.ArrayList;
import java.util.List;

public class ChangesManager {
    private static List<Change> changes;

    public ChangesManager() {
    }

    public static List<Change> getChanges() {
        if (changes == null) {
            changes = new ArrayList<>();
        }
        return changes;
    }

    public static void clearChanges() {
       changes.clear();
    }
}


