package hr.java.project.projectfxapp.threads;

import hr.java.project.projectfxapp.entities.Change;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetMostRecentChangeThread extends ChangesManagerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClockThread.class);

    private static GetMostRecentChangeThread instance;

    private TableView<Change> tableViewToUpdate;
    private Thread thread;

    private GetMostRecentChangeThread() {};

    public static synchronized GetMostRecentChangeThread getInstance() {
        if (instance == null) {
            instance = new GetMostRecentChangeThread();
        }
        return instance;
    }

    public void setTableViewToUpdate(TableView<Change> tableViewToUpdate) {
        this.tableViewToUpdate = tableViewToUpdate;
    }

    public boolean startThread() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
            return true;
        }

        return false;
    }


    public void executeTaskManually() {
        Platform.runLater(() -> {
            List<Change> changes = super.readChangesFromFile();
            if (!changes.isEmpty()){
                Change mostRecentChange = changes.get(changes.size() - 1);
                tableViewToUpdate.setItems(FXCollections.observableList(List.of(mostRecentChange)));
            }
        });
    }


    @Override
    public void run() {
        while (true) {
            try {

                Platform.runLater(() -> {
                    List<Change> changes = super.readChangesFromFile();
                    if (!changes.isEmpty()){
                        Change mostRecentChange = changes.get(changes.size() - 1);
                        tableViewToUpdate.setItems(FXCollections.observableList(List.of(mostRecentChange)));
                    }
                });

                Thread.sleep(120000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
