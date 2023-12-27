package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.User;
import hr.java.project.projectfxapp.enums.ChangeApplicationScreen;
import hr.java.project.projectfxapp.enums.UserRole;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import hr.java.project.projectfxapp.utility.FileWriterUtil;
import hr.java.project.projectfxapp.utility.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;






   public void login(ActionEvent actionEvent) {

       /*String enteredUsername = usernameTextField.getText();
       String enteredPassword = passwordPasswordField.getText();
       User loginUser = new User(enteredUsername, enteredPassword, UserRole.ADMIN);

       List<User> users = new ArrayList<>();
       users.add(loginUser);
       FileWriterUtil.saveUsers(users);*/

        String enteredUsername = usernameTextField.getText();
        String enteredPassword = passwordPasswordField.getText();
        User loginUser = new User(enteredUsername, enteredPassword, UserRole.ADMIN);

        List<User> users = FileReaderUtil.getUsers();

        for (User user : users) {

            if (user.getUsername().equals(loginUser.getUsername())
                    && PasswordUtil.isPasswordCorrect(loginUser.getPassword(), user.getPassword())
                    && loginUser.getRole().equals(UserRole.ADMIN)) {
                JavaFxProjectApplication.switchScene(ChangeApplicationScreen.MainScreen);
            }
        }

    }

}
