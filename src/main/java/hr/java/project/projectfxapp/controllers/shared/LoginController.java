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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LoginController {

    @FXML
    private Button loginButton;
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



    public void login(ActionEvent actionEvent) throws UnsupportedAlgorithmException {


        ChangesManager.getChanges();
        SerializeChangesThread serializeChangesThread = SerializeChangesThread.getInstance();

        String enteredUsername = usernameTextField.getText();
        String enteredPassword = passwordPasswordField.getText();


        List<User> users = FileReaderUtil.getUsers();

        for (User user : users) {

            if (user.getUsername().equals(enteredUsername)
                    && PasswordUtil.isPasswordCorrect(enteredPassword, user.getHashedPassword())
                    && user.getRole().equals(UserRole.ADMIN)) {

               User currentUser = DatabaseUtil.getCurrentUser(enteredUsername, PasswordUtil.hashPassword(enteredPassword)).get();
               SessionManager.getInstance().setCurrentUser(currentUser);

                JavaFxProjectApplication.switchScene(ApplicationScreen.MainScreen);
            }
            else if (user.getUsername().equals(enteredUsername)
                    && PasswordUtil.isPasswordCorrect(enteredPassword, user.getHashedPassword())
                    && user.getRole().equals(UserRole.USER)) {

                User currentUser = DatabaseUtil.getCurrentUser(enteredUsername, PasswordUtil.hashPassword(enteredPassword)).get();
                SessionManager.getInstance().setCurrentClub(DatabaseUtil.getMathClub(currentUser.getMathClubId()).get());
                SessionManager.getInstance().setCurrentUser(currentUser);
                SessionManager.getInstance().setLoginTime(LocalDateTime.now());


                JavaFxProjectApplication.switchScene(ApplicationScreen.MainScreenForUser);
            }

            else{
                wrongCredentialsLabel.setVisible(true);
                wrongCredentialsLabel.setText("Pogrešno korisničko ime ili lozinka");
            }
        }

    }


    public void register(){
        String enteredUsername = usernameRegisterTextField.getText();
        String enteredPassword = passwordRegisterPasswordField.getText();

        try{
            ValidationProtocol.validateRegistrationInformation(usernameRegisterTextField, passwordRegisterPasswordField
                    , passwordConfirmPasswordField, clubNameTextField,
                    streetNameTextField, houseNumberTextField, cityComboBox);



            List<User> users = FileReaderUtil.getUsers();

            for (User user : users){
                if (user.getUsername().equals(enteredUsername)){
                    throw new ValidationException("Korisničko ime već postoji");
                }
            }

            Address.AdressBuilder addressBuilder = new Address.AdressBuilder(cityComboBox.getValue())
                    .setHouseNumber(houseNumberTextField.getText())
                    .setStreet(streetNameTextField.getText())
                    .setAddressId(0L);

            Address address = addressBuilder.build();

            Long addressId =  DatabaseUtil.saveAddress(address);

            addressBuilder.setAddressId(addressId);

            MathClub newMathClub = new MathClub(0L, clubNameTextField.getText(), addressBuilder.build(),
                    new HashSet<>());

            List<MathClub> mathClubs = new ArrayList<>();
            mathClubs.add(newMathClub);


            Long mathClubId = DatabaseUtil.saveMathClubs(mathClubs);

            String hashedPassword = PasswordUtil.hashPassword(enteredPassword);

            User registerUser = new User(enteredUsername, hashedPassword, UserRole.USER, mathClubId,
                    new Picture(Constants.DEFAULT_PICTURE_PATH_USER));


            users.add(registerUser);
            FileWriterUtil.saveUsers(users);

            boolean positiveConfirmation = DatabaseUtil.saveUser(registerUser);

            if(positiveConfirmation) {
                List<Change> changes = SerializationUtil.deserializeChanges();
                Change change = Change.create(registerUser, "/",
                        "Korisnik " + registerUser.getUsername() + " se registrirao", "Registracija");
                changes.add(change);
                SerializationUtil.serializeChanges(changes);


                ValidationProtocol.showSuccessAlert("Registracija uspješna", "Registracija korisnika " +
                        registerUser.getUsername() + " je uspješna\nMolimo prijavite se sa svojim podacima");

            }





        }catch (ValidationException ex){
            ValidationProtocol.showErrorAlert("Greška prilikom registracije", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        } catch (UnsupportedAlgorithmException e) {
            throw new RuntimeException(e);
        }

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
