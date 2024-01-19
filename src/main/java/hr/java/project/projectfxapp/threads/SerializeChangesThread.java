package hr.java.project.projectfxapp.threads;

import hr.java.project.projectfxapp.entities.Change;
import javafx.application.Platform;

import java.util.List;

public class SerializeChangesThread extends ChangesManagerThread implements Runnable {

    private List<Change> changes;

    public SerializeChangesThread(List<Change> changes){
        this.changes = changes;
    }

    @Override
    public void run() {
        Platform.runLater(() -> super.writeChangesToFile(changes));
    }
}
