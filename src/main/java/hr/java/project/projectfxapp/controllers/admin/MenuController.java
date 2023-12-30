package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class MenuController {

    public void showMainScreen(){
        JavaFxProjectApplication.switchScene(ApplicationScreen.MainScreen);

    }
    public void showStudentsScreen(){
        JavaFxProjectApplication.switchScene(ApplicationScreen.Students);
    }

    public void showClubsScreen(){
        JavaFxProjectApplication.switchScene(ApplicationScreen.Clubs);
    }

    public void showProjectsScreen(){
      JavaFxProjectApplication.switchScene(ApplicationScreen.Projects);
    }

    public void showCompetitionsScreen(){
        JavaFxProjectApplication.switchScene(ApplicationScreen.Competition);
    }


    public void showAddNewStudent(ActionEvent actionEvent) {
        JavaFxProjectApplication.switchScene(ApplicationScreen.AddNewStudent);
    }

    public void showAddNewMathClub(ActionEvent actionEvent) {
       JavaFxProjectApplication.switchScene(ApplicationScreen.AddNewMathClub);
    }

    public void showAddNewCompetition(ActionEvent actionEvent) {
        JavaFxProjectApplication.switchScene(ApplicationScreen.AddNewCompetition);
    }

    public void showAddNewProject(ActionEvent actionEvent) {
       JavaFxProjectApplication.switchScene(ApplicationScreen.AddNewProject);
    }
}
