package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.time.Clock;
import java.util.Optional;

public class NavigationForUserController {

    @FXML
    private Label clockLabel;

    @FXML
    private ImageView clubLogoImageView;



    public void initialize(){
        ClockThread clockThread = new ClockThread(clockLabel);
        Thread thread = new Thread(clockThread);
        thread.start();
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
        }

    }
}
