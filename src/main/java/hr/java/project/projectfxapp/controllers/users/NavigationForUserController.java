package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.LoginStatistics;
import hr.java.project.projectfxapp.entities.User;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.threads.SerializeChangesThread;
import hr.java.project.projectfxapp.utility.files.SerializationUtil;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.time.LocalDateTime;

public class NavigationForUserController {

    @FXML
    private Label clockLabel;

    @FXML
    private ImageView clubLogoImageView;




    public void initialize(){
        ClockThread clockThread = ClockThread.getInstance();
        clockThread.setLabelToUpdate(clockLabel);
        clockThread.startThread();

        User currentUser = SessionManager.getInstance().getCurrentUser();
        setClubLogoImage(currentUser.getPicture().getPicturePath());
    }


    public void showUserMainScreen(ActionEvent actionEvent) {
        JavaFxProjectApplication.switchScene(ApplicationScreen.MainScreenForUser);
    }

    public void showClubMembers(ActionEvent actionEvent) {

        JavaFxProjectApplication.switchScene(ApplicationScreen.ClubMembers);
    }

    public void showCompetitions(ActionEvent actionEvent) {
        JavaFxProjectApplication.switchScene(ApplicationScreen.CompetitionsUser);
    }

    public void showProjects(ActionEvent actionEvent) {
        JavaFxProjectApplication.switchScene(ApplicationScreen.ProjectsUser);
    }


    public void showSettings(ActionEvent actionEvent) {
        JavaFxProjectApplication.switchScene(ApplicationScreen.Settings);
    }

    public void showLogin(ActionEvent actionEvent) {

        boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog(
                "Odjava", "Odjava", "Jeste li sigurni da se želite odjaviti?" +
                        "\nPritisnite Da za odjavu");

        if (positiveConfirmation){
            JavaFxProjectApplication.switchScene(ApplicationScreen.Login);
            SerializeChangesThread serializeChangesThread = SerializeChangesThread.setAndStartThread();
            serializeChangesThread.executeTaskManually();

            SessionManager.getInstance().setLogoutTime(LocalDateTime.now());
            LoginStatistics loginStatistic = SessionManager.getInstance().recordLoginStatistics();
            SerializationUtil.addAndSerializeLoginStatistics(loginStatistic);


        }

    }

    public void setClubLogoImage(String imagePath) {
        File newFile = new File(imagePath);
        Image image = new Image(newFile.toURI().toString());
        clubLogoImageView.setImage(image);
    }
}
