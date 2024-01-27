package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.UserRole;
import hr.java.project.projectfxapp.exception.UnsupportedAlgorithmException;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.utility.*;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.files.FileReaderUtil;
import hr.java.project.projectfxapp.utility.files.FileWriterUtil;
import hr.java.project.projectfxapp.utility.manager.ChangesManager;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

public class AddNewMathClubController {
    @FXML
    private Label clockLabel;
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

        ClockThread clockThread = ClockThread.getInstance();
        clockThread.setLabelToUpdate(clockLabel);

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

            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda unosa",
                    "Jeste li sigurni da želite registrirati novog korisnika?",
                    "Ako želite registrirati korisnika " + newUserUsernameTextField.getText() + "\nPritisnite Da za potvrdu");

            if (positiveConfirmation){
                MathClub newMathClub = createNewMathClub();
                List<MathClub> mathClubs = new ArrayList<>();
                mathClubs.add(newMathClub);

                Long mathClubId = DatabaseUtil.saveMathClubs(mathClubs);

                String hashedPassword = PasswordUtil.hashPassword(passwordPasswordField.getText());

                User registerUser = new User(newUserUsernameTextField.getText(), hashedPassword, UserRole.USER, mathClubId,
                        new Picture(Constants.DEFAULT_PICTURE_MATH_CLUB));

                users.add(registerUser);
                FileWriterUtil.saveUsers(users);

                boolean success = DatabaseUtil.saveUser(registerUser);
                if (success){

                    User currentUser = SessionManager.getInstance().getCurrentUser();
                    Change change = Change.create(currentUser, "/",
                            "Dodan novi korisnik: " + registerUser.getUsername(), "Korisnik");
                    ChangesManager.getChanges().add(change);


                    ValidationProtocol.showSuccessAlert("Spremanje novog korisnika je bilo uspješno",
                            "Klub " + newMathClub.getName()  + " uspješno se spremio!");
                }
                else{
                    ValidationProtocol.showErrorAlert("Greška pri spremanju", "Greška pri spremanju novog korisnika",
                            "Došlo je do greške pri spremanju novog korisnika");
                }

            }



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
