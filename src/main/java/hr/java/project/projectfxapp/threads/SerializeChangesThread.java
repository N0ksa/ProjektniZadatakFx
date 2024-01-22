package hr.java.project.projectfxapp.threads;

import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.threads.ChangesManagerThread;
import hr.java.project.projectfxapp.utility.ChangesManager;
import javafx.application.Platform;

import java.util.List;

public class SerializeChangesThread extends ChangesManagerThread implements Runnable {

    private static SerializeChangesThread instance;
    private Thread thread;

    private SerializeChangesThread() {}

    public static synchronized SerializeChangesThread getInstance() {
        if (instance == null || !instance.isThreadAlive()) {
            instance = new SerializeChangesThread();
            instance.startThread();
        }
        return instance;
    }

    private void startThread() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Platform.runLater(() -> {
                    List<Change> changes = super.readChangesFromFile();
                    changes.addAll(ChangesManager.getChanges());
                    super.writeChangesToFile(changes);
                    ChangesManager.clearChanges();
                });

                Thread.sleep(120000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void executeTaskManually() {
        Platform.runLater(() -> {
            List<Change> changes = super.readChangesFromFile();
            changes.addAll(ChangesManager.getChanges());
            super.writeChangesToFile(changes);
            ChangesManager.clearChanges();
        });
    }

    private boolean isThreadAlive() {
        return thread != null && thread.isAlive();
    }
}
