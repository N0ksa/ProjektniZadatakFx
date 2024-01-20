package hr.java.project.projectfxapp.threads;

import hr.java.project.projectfxapp.utility.DatabaseUtil;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClockThread.class);
    private final Label labelToUpdate;

    private static ClockThread instance;

    public ClockThread(Label labelToUpdate) {
        this.labelToUpdate = labelToUpdate;
    }


    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(1000);

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                String formattedTime = timeFormat.format(new Date());
                String formattedDate = dateFormat.format(new Date());

                Platform.runLater(() -> labelToUpdate.setText(formattedTime + " " + formattedDate));


            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
