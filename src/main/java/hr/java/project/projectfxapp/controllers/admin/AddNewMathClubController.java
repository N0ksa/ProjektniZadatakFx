package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.UserRole;
import hr.java.project.projectfxapp.exception.UnsupportedAlgorithmException;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AddNewMathClubController {

    @FXML
    private TextField newUserUsernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private PasswordField confirmPasswordPasswordField;
    @FXML
    private ComboBox<Address> clubAddressComboBox;
    @FXML
    private TextField clubNameTextField;



    public void initialize(){

        List<Address> availableAddresses = DatabaseUtil.getAddresses();
        ObservableList<Address> availableAddressesObservableList = FXCollections.observableList(availableAddresses);
        clubAddressComboBox.setItems(availableAddressesObservableList);
    }

    public void saveMathClubs(ActionEvent actionEvent) {

        try{
            
            ValidationProtocol.validateNewMathClub(clubNameTextField, clubAddressComboBox, newUserUsernameTextField,
                    passwordPasswordField, confirmPasswordPasswordField);

            List<User> users = FileReaderUtil.getUsers();

            for (User user : users){
                if (user.getUsername().equals(newUserUsernameTextField.getText())){
                    throw new ValidationException("Korisničko ime već postoji");
                }
            }

            MathClub newMathClub = createNewMathClub();
            List<MathClub> mathClubs = new ArrayList<>();
            mathClubs.add(newMathClub);
            Long mathClubId = DatabaseUtil.saveMathClubs(mathClubs);

            String hashedPassword = PasswordUtil.hashPassword(passwordPasswordField.getText());

            User registerUser = new User(newUserUsernameTextField.getText(), hashedPassword, UserRole.USER, mathClubId,
                    new Picture(Constants.DEFAULT_PICTURE_PATH_USER));

            users.add(registerUser);
            FileWriterUtil.saveUsers(users);

            DatabaseUtil.saveUser(registerUser);

            ValidationProtocol.showSuccessAlert("Spremanje novog korisnika je bilo uspješno",
                    "Klub " + newMathClub.getName()  + " uspješno se spremio!");

        }
        catch (ValidationException ex){
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());

        } catch (UnsupportedAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    private MathClub createNewMathClub() {
        Long mathClubId = 0L;
        String clubName = clubNameTextField.getText();
        Address clubAddress = clubAddressComboBox.getValue();
        Set<Student> clubMembers = new HashSet<>();

        return new MathClub(mathClubId, clubName, clubAddress, clubMembers);
    }
}
