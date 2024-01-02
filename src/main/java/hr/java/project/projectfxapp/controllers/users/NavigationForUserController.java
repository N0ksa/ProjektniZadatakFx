package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import javafx.event.ActionEvent;

public class NavigationForUserController {

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

    public void showCompetitionsForClub(ActionEvent actionEvent) {
    }
}
