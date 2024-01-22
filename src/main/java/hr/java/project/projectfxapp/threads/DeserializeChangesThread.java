package hr.java.project.projectfxapp.threads;

import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.utility.ChangesManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

import java.util.List;

public class DeserializeChangesThread extends ChangesManagerThread implements Runnable {

    private static DeserializeChangesThread instance;

    private Thread thread;

    private TableView<Change> changesTableView;

    private DeserializeChangesThread() {}

    public static synchronized DeserializeChangesThread getInstance() {
        if (instance == null) {
            instance = new DeserializeChangesThread();
        }
        return instance;
    }

    @Override
    public void run() {

        while(true){
            try{
                Platform.runLater(() -> {
                    List<Change> changes = super.readChangesFromFile();
                    changesTableView.setItems(FXCollections.observableArrayList(changes));
                });

                Thread.sleep(60000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void setChangesTableView(TableView<Change> changesTableView) {
        this.changesTableView = changesTableView;
    }


    public void startThread() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
        else{
            Platform.runLater(() -> {
                List<Change> changes = super.readChangesFromFile();
                changesTableView.setItems(FXCollections.observableArrayList(changes));
            });
        }
    }

}
