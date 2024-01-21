package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.FileCopier;
import hr.java.project.projectfxapp.entities.User;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.exception.UnsupportedAlgorithmException;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SettingsController {

    @FXML
    private Label clubNameLabel;
    @FXML
    private ImageView usernameProfilePictureImageView;
    @FXML
    private PasswordField changePasswordPasswordField;

    @FXML
    private TextField changeUserNameTextField;

    @FXML
    private PasswordField confirmChangePasswordPasswordField;

    @FXML
    private PasswordField enterOldPasswordPasswordField;

    @FXML
    private PasswordField enterPasswordForUsernameChangePasswordField;

    private String imagePath = Constants.DEFAULT_PICTURE_PATH_USER;



    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);


    public void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        setUserPicture(currentUser);
        clubNameLabel.setText(SessionManager.getInstance().getCurrentClub().getName());;

    }

    private void setUserPicture(User currentUser) {
        String picturePath = currentUser.getPicture().getPicturePath();
        if (!picturePath.isEmpty()){
            File pictureFile = new File(picturePath);
            Image image = new Image(pictureFile.toURI().toString());
            usernameProfilePictureImageView.setImage(image);
        }

    }

    public void confirmPasswordChange(ActionEvent event) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        try {
            ValidationProtocol.validatePasswordChange(currentUser,
                    changePasswordPasswordField, confirmChangePasswordPasswordField, enterOldPasswordPasswordField);

            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog
                    ("Potvrda promjene lozinke", "Promjena lozinke",
                            "Jeste li sigurni da želite promijeniti lozinku?" +
                                    "\nAko ste sigurni pritisnite Da");

            if (positiveConfirmation) {

                boolean updateSuccessful = updatePassword(currentUser);

                if (updateSuccessful) {
                    ValidationProtocol.showSuccessAlert("Ažuriranje lozinke je uspjelo",
                            "Lozinka je uspješno promijenjena");



                    JavaFxProjectApplication.switchScene(ApplicationScreen.Login);

                } else {
                    ValidationProtocol.showErrorAlert("Greška pri ažuriranju", "Ažuriranje lozinke nije uspjelo",
                            "Pokušajte ponovno");
                }
            }

        }catch (ValidationException ex) {
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }
    }



    public void confirmUsernameChange (ActionEvent event){
        User currentUser = SessionManager.getInstance().getCurrentUser();

        try {
            ValidationProtocol.validateUsernameChange(currentUser,
                    changeUserNameTextField, enterPasswordForUsernameChangePasswordField);

            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog
                    ("Potvrda promjene korisničkog imena", "Promjena korisničkog imena",
                            "Jeste li sigurni da želite promijeniti korisničko ime iz " + currentUser.getUsername() +
                                    " u " + changeUserNameTextField.getText() + "?" +
                                    "\nAko ste sigurni pritisnite Da");

            if (positiveConfirmation) {

                boolean updateSuccessful = updateUsername(currentUser);

                if (updateSuccessful) {
                    ValidationProtocol.showSuccessAlert("Ažuriranje korisničkog imena je uspjelo",
                            "Korisničko ime je uspješno promijenjeno u " + changeUserNameTextField.getText());


                    JavaFxProjectApplication.switchScene(ApplicationScreen.Login);

                } else {
                    ValidationProtocol.showErrorAlert("Greška pri ažuriranju", "Ažuriranje korisničkog imena nije uspjelo",
                            "Pokušajte ponovno");
                }

            }


        } catch (ValidationException ex) {
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());

        }

    }


    public void changeProfilePicture(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odaberi sliku");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (Optional.ofNullable(selectedFile).isPresent()) {
            try {
                String destinationDirectory = "src/main/resources/images/";


                FileCopier<File> fileCopier = new FileUtils();
                fileCopier.copyToDirectory(selectedFile, destinationDirectory);


                File imageFile = new File(destinationDirectory + selectedFile.getName());
                Image image = new Image(imageFile.toURI().toString());
                usernameProfilePictureImageView.setImage(image);


                imagePath = destinationDirectory + selectedFile.getName();

            } catch (IOException ex) {
                logger.error("Greška prilikom kopiranja slike", ex);
            }
        }
    }





    private boolean updateUsername (User currentUser){

        boolean updateSuccess =  DatabaseUtil.updateUsername(currentUser, changeUserNameTextField.getText());
        if (updateSuccess){
            List<User> users = DatabaseUtil.getUsers();
            FileWriterUtil.saveUsers(users);
            return updateSuccess;
        }

        return updateSuccess;
    }


    private boolean updatePassword (User currentUser) {

        boolean updateSuccessful = false;

        try {
            updateSuccessful = DatabaseUtil.updatePassword(currentUser, PasswordUtil.hashPassword(changePasswordPasswordField.getText()));

            if(updateSuccessful){
                List<User> users = DatabaseUtil.getUsers();
                FileWriterUtil.saveUsers(users);
            }

        } catch (UnsupportedAlgorithmException ex) {
            updateSuccessful = false;
            logger.error("Nepodržani algoritam za hashiranje lozinke", ex);
        }

        return updateSuccessful;
    }

    public void saveNewClubImage(ActionEvent actionEvent) {

        boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog
                ("Potvrda promjene slike", "Promjena slike",
                        "Jeste li sigurni da želite promijeniti sliku?" +
                                "\nAko ste sigurni pritisnite Da");

        if (positiveConfirmation) {

            User currentUser = SessionManager.getInstance().getCurrentUser();
            String newImagePath = imagePath;
            currentUser.getPicture().setPicturePath(newImagePath);

            boolean updateSuccessful = DatabaseUtil.updateUserProfilePicture(currentUser, newImagePath);

            if (updateSuccessful){
                ValidationProtocol.showSuccessAlert("Ažuriranje slike je uspjelo",
                        "Slika je uspješno promijenjena");
            }
            else{
                ValidationProtocol.showErrorAlert("Greška pri ažuriranju", "Ažuriranje slike nije uspjelo",
                        "Pokušajte ponovno");
            }
        }

        JavaFxProjectApplication.switchScene(ApplicationScreen.Login);

    }
}

