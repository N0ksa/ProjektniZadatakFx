package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class MenuController {

    public void showMainScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("mainScreen.fxml"));
        try{
            Scene scene = new Scene(fxmlLoader.load());
            JavaFxProjectApplication.getMainStage().setTitle("ProjectApp");
            JavaFxProjectApplication.getMainStage().setScene(scene);
            JavaFxProjectApplication.getMainStage().show();


        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public void showStudentsScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("students.fxml"));

        try {

            Scene scene = new Scene(fxmlLoader.load());
            JavaFxProjectApplication.getMainStage().setTitle("Students");
            JavaFxProjectApplication.getMainStage().setScene(scene);
            JavaFxProjectApplication.getMainStage().show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void showClubsScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("clubs.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());

            JavaFxProjectApplication.getMainStage().setTitle("Clubs");
            JavaFxProjectApplication.getMainStage().setScene(scene);
            JavaFxProjectApplication.getMainStage().show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void showProjectsScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("projects.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());
            JavaFxProjectApplication.getMainStage().setTitle("Projects");
            JavaFxProjectApplication.getMainStage().setScene(scene);
            JavaFxProjectApplication.getMainStage().show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void showCompetitionsScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("competitions.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());
            JavaFxProjectApplication.getMainStage().setTitle("Competitions");
            JavaFxProjectApplication.getMainStage().setScene(scene);
            JavaFxProjectApplication.getMainStage().show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void showAddNewStudent(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("addNewStudent.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());
            JavaFxProjectApplication.getMainStage().setTitle("Add new student");
            JavaFxProjectApplication.getMainStage().setScene(scene);
            JavaFxProjectApplication.getMainStage().show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showAddNewMathClub(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("addNewMathClub.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());
            JavaFxProjectApplication.getMainStage().setTitle("Add new math club");
            JavaFxProjectApplication.getMainStage().setScene(scene);
            JavaFxProjectApplication.getMainStage().show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showAddNewCompetition(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("addNewCompetition.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());
            JavaFxProjectApplication.getMainStage().setTitle("Dodaj novo natjecanje");
            JavaFxProjectApplication.getMainStage().setScene(scene);
            JavaFxProjectApplication.getMainStage().show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showAddNewProject(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("addNewProject.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());
            JavaFxProjectApplication.getMainStage().setTitle("Dodaj novi projekt");
            JavaFxProjectApplication.getMainStage().setScene(scene);
            JavaFxProjectApplication.getMainStage().show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
