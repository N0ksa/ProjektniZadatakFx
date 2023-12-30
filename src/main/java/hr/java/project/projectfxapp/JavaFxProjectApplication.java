package hr.java.project.projectfxapp;

import hr.java.project.projectfxapp.enums.ApplicationScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JavaFxProjectApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(JavaFxProjectApplication.class);

    public static Stage mainStage;
    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("login.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ProjektMatematika");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static Stage getMainStage(){
        return mainStage;
    }
    public static void main(String[] args) {
        launch();
    }



    public static void switchScene(ApplicationScreen screen) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource(screen.getPathOfFxml()));

            Scene scene = new Scene(fxmlLoader.load());

            mainStage.setTitle(screen.getTitle());
            mainStage.setScene(scene);
            mainStage.setResizable(screen.isResizable());
            mainStage.show();

        } catch (IOException e) {
            logger.error("Pogreška prilikom dohvaćanja scene: " + screen.getTitle(), e);
            throw new RuntimeException(e);
        }
    }


    public static void showPopup(ApplicationScreen screen) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource(screen.getPathOfFxml()));
            Scene popupScene = new Scene(fxmlLoader.load());

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle(screen.getTitle());
            popupStage.setScene(popupScene);
            popupStage.setResizable(screen.isResizable());
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}