package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class NavigationForUserController {

    @FXML
    private ImageView clubLogoImageView;

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
}
