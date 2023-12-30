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

    public void showAddNewProjectForClub(ActionEvent actionEvent) {
        JavaFxProjectApplication.switchScene(ApplicationScreen.AddNewProjectUser);
    }

    public void showAddNewCompetitionForClub(ActionEvent actionEvent) {
        JavaFxProjectApplication.showPopup(ApplicationScreen.AddNewCCompetitionUser);
    }

    public void showProjectsForClub(ActionEvent actionEvent) {
    }

    public void showCompetitionsForClub(ActionEvent actionEvent) {
    }
}
