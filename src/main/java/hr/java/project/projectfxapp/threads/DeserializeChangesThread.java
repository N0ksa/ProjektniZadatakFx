package hr.java.project.projectfxapp.threads;

import hr.java.project.projectfxapp.entities.Change;
import javafx.application.Platform;

import java.util.List;

public class DeserializeChangesThread extends ChangesManagerThread implements Runnable {


        private List<Change> changes;

        @Override
        public void run() {
            Platform.runLater(() -> {

                this.changes = super.readChangesFromFile();
            });
        }

        public List<Change> getChanges(){
            return changes;
        }

}
