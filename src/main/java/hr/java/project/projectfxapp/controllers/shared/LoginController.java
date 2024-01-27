package hr.java.project.projectfxapp.controllers.shared;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.enums.UserRole;
import hr.java.project.projectfxapp.exception.UnsupportedAlgorithmException;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.threads.SerializeChangesThread;
import hr.java.project.projectfxapp.utility.*;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.files.FileReaderUtil;
import hr.java.project.projectfxapp.utility.files.FileWriterUtil;
import hr.java.project.projectfxapp.utility.manager.ChangesManager;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

public class LoginController {

    @FXML
    private BorderPane registerClubBorderPane;
    @FXML
    private BorderPane loginBorderPane;
    @FXML
    private ComboBox<City> cityComboBox;
    @FXML
    private TextField clubNameTextField;
    @FXML
    private TextField houseNumberTextField;
    @FXML
    private PasswordField passwordConfirmPasswordField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private PasswordField passwordRegisterPasswordField;
    @FXML
    private TextField streetNameTextField;
    @FXML
    private TextField usernameRegisterTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Label wrongCredentialsLabel;


    public void initialize() {
        cityComboBox.getItems().setAll(City.values());
    }

    public void login(ActionEvent actionEvent) {
        ChangesManager.setNewChangesIfChangesNotPresent();
        SerializeChangesThread.setAndStartThread();

        String enteredUsername = usernameTextField.getText();
        String enteredPassword = passwordPasswordField.getText();

        List<User> users = FileReaderUtil.getUsers();

        boolean loggedIn = false;

        for (User user : users) {
            try {
                if (isUserCredentialsValid(user, enteredUsername, enteredPassword)) {
                    loggedIn = true;
                    break;
                }
            } catch (UnsupportedAlgorithmException ex) {
                handleUnsupportedAlgorithmException(ex);
            }
        }

        if (!loggedIn) {
            showWrongCredentialsMessage();
        }
    }

    private boolean isUserCredentialsValid(User user, String enteredUsername, String enteredPassword)
            throws UnsupportedAlgorithmException {
        if (user.getUsername().equals(enteredUsername)
                && PasswordUtil.isPasswordCorrect(enteredPassword, user.getHashedPassword())) {

            if (user.getRole().equals(UserRole.ADMIN)) {
                handleLogin(user, ApplicationScreen.MainScreen);
            }
            else if (user.getRole().equals(UserRole.USER)) {
                handleLogin(user, ApplicationScreen.MainScreenForUser);
            }
            return true;
        }
        return false;
    }

    private void handleLogin(User user, ApplicationScreen mainScreen) {
        User currentUser = DatabaseUtil.getCurrentUser(user.getUsername(), user.getHashedPassword()).get();
        if (user.getRole().equals(UserRole.USER)) {
            SessionManager.getInstance().setCurrentClub(DatabaseUtil.getMathClub(currentUser.getMathClubId()).get());
            SessionManager.getInstance().setLoginTime(LocalDateTime.now());
        }

        SessionManager.getInstance().setCurrentUser(currentUser);
        JavaFxProjectApplication.switchScene(mainScreen);
        handleSuccessfulLogin(user);
    }

    private void handleSuccessfulLogin(User user) {
        ValidationProtocol.showSuccessLoginAlert("Prijava uspješna",
                "Prijava korisnika " + user.getUsername() + " je bila bila uspješna\n" +
                        "Dobrodošli u aplikaciju ProjektMatematika");
    }


    private void showWrongCredentialsMessage() {
        wrongCredentialsLabel.setVisible(true);
        wrongCredentialsLabel.setText("Pogrešno korisničko ime ili lozinka");
    }

    public void register(ActionEvent actionEvent) {
        String enteredUsername = usernameRegisterTextField.getText();
        String enteredPassword = passwordRegisterPasswordField.getText();

        try {
            ValidationProtocol.validateRegistrationInformation(usernameRegisterTextField, passwordRegisterPasswordField,
                    passwordConfirmPasswordField, clubNameTextField, streetNameTextField, houseNumberTextField, cityComboBox);

            List<User> users = FileReaderUtil.getUsers();

            if (users.stream().anyMatch(u -> u.getUsername().equals(enteredUsername))) {
                throw new ValidationException("Korisničko ime već postoji");
            }

            MathClub newMathClub = constructNewMathClub();
            Long mathClubId = DatabaseUtil.saveMathClubs(List.of(newMathClub));

            String hashedPassword = PasswordUtil.hashPassword(enteredPassword);
            User registerUser = new User(enteredUsername, hashedPassword, UserRole.USER, mathClubId,
                    new Picture(Constants.DEFAULT_PICTURE_MATH_CLUB));
            users.add(registerUser);
            FileWriterUtil.saveUsers(users);

            boolean positiveConfirmation = DatabaseUtil.saveUser(registerUser);

            if (positiveConfirmation) {
                handleUserRegistrationSuccess(registerUser);
            }

        } catch (ValidationException ex) {
            ValidationProtocol.showErrorAlert("Greška prilikom registracije",
                    "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }  catch (UnsupportedAlgorithmException ex) {
            handleUnsupportedAlgorithmException(ex);
        }
    }

    private MathClub constructNewMathClub() {
        Address address = new Address.AddressBuilder(cityComboBox.getValue())
                .setHouseNumber(houseNumberTextField.getText())
                .setStreet(streetNameTextField.getText())
                .setAddressId(0L)
                .build();

        Long addressId = DatabaseUtil.saveAddress(address);

        return new MathClub(0L, clubNameTextField.getText(),
                new Address.AddressBuilder(cityComboBox.getValue())
                        .setHouseNumber(houseNumberTextField.getText())
                        .setStreet(streetNameTextField.getText())
                        .setAddressId(addressId)
                        .build(),
                new HashSet<>());
    }

    private void handleUserRegistrationSuccess(User registerUser) {
        Change change = Change.create(registerUser, "/",
                "Korisnik " + registerUser.getUsername() + " se registrirao", "Registracija");

        ChangesManager.setNewChangesIfChangesNotPresent().add(change);

        ValidationProtocol.showSuccessAlert("Registracija uspješna", "Registracija korisnika " +
                registerUser.getUsername() + " je uspješna\nMolimo prijavite se sa svojim podacima");
    }



    private void handleUnsupportedAlgorithmException(UnsupportedAlgorithmException ex) {
        ValidationProtocol.showErrorAlert("Greška prilikom prijave",
                "Molimo kontaktirajte administratora",
                "Sustav ne podržava SHA-256");
    }

    public void switchToLogin(ActionEvent actionEvent) {
        loginBorderPane.setVisible(true);
        registerClubBorderPane.setVisible(false);
    }

    public void switchToRegisterForm(ActionEvent actionEvent) {
        loginBorderPane.setVisible(false);
        registerClubBorderPane.setVisible(true);
    }
}
