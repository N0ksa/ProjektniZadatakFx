package hr.java.project.projectfxapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class MenuController {

    public void showMainScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxProjectApplication.class.getResource("mainScreen.fxml"));
        try{
            Scene scene = new Scene(fxmlLoader.load());
            JavaFxProjectApplication.getMainStage().setTitle("Aplikacija za upravljanje studentskih klubova matematike");
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


}
