package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.User;
import hr.java.project.projectfxapp.exception.UnsupportedAlgorithmException;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

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

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    private static String imagePath = Constants.DEFAULT_PICTURE_PATH_USER;


    public void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        setUserPicture(currentUser);
        clubNameLabel.setText(SessionManager.getInstance().getCurrentClub().getName());;

    }

    private void setUserPicture(User currentUser) {
        String picturePath = currentUser.getPicture().getPicturePath();
        if (picturePath != null) {
            Image image = new Image(getClass().getResource(picturePath).toExternalForm());
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

        // Set the initial directory to the "images" directory in resources
        try {
            URL resourceUrl = getClass().getResource("/images");
            File initialDirectory = new File(resourceUrl.toURI());
            fileChooser.setInitialDirectory(initialDirectory);

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                String imageName = selectedFile.getName();
                String relativePath = "/images/" + imageName;

                Image newImage = new Image(getClass().getResource(relativePath).toExternalForm());
                usernameProfilePictureImageView.setImage(newImage);
                imagePath = relativePath;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            // Handle the exception as needed
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
        User currentUser = SessionManager.getInstance().getCurrentUser();
        String newImagePath = imagePath;

        boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog
                ("Potvrda promjene slike", "Promjena slike",
                        "Jeste li sigurni da želite promijeniti sliku?" +
                                "\nAko ste sigurni pritisnite Da");

        if (positiveConfirmation) {

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

    }
}


