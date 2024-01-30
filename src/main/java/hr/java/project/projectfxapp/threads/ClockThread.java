package hr.java.project.projectfxapp.threads;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClockThread.class);
    private static ClockThread instance;
    private Label labelToUpdate;
    private Thread thread;

    private ClockThread() {}
    public static synchronized ClockThread getInstance() {
        if (instance == null) {
            instance = new ClockThread();
        }
        return instance;
    }
    public void setLabelToUpdate(Label labelToUpdate) {
        this.labelToUpdate = labelToUpdate;
    }

    public void startThread() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                String formattedTime = timeFormat.format(new Date());
                String formattedDate = dateFormat.format(new Date());

                Platform.runLater(() -> labelToUpdate.setText(formattedTime + " " + formattedDate));

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
