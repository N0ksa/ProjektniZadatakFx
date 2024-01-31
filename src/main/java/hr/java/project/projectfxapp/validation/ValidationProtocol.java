package hr.java.project.projectfxapp.validation;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.files.FileReaderUtil;
import hr.java.project.projectfxapp.utility.files.PasswordUtil;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidationProtocol {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static void validateCompetition(TextField competitionNameTextField,
                                           TextArea competitionDescriptionTextArea,
                                           ComboBox<Address> competitionAddressComboBox,
                                           DatePicker competitionDateDatePicker,
                                           TextField competitionTimeTextField,
                                           TextField competitionBuildingNameTextField,
                                           TextField competitionHallNameTextField,
                                           TableView<CompetitionResult> competitionResultsTableView,
                                           ComboBox<MathClub> organizer) throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(competitionNameTextField, "Unesite naziv natjecanja", errors);
        validateTextArea(competitionDescriptionTextArea, "Unesite opis natjecanja", errors);
        validateComboBox(competitionAddressComboBox, "Odaberite adresu natjecanja", errors);
        validateComboBox(organizer, "Odaberite organizatora natjecanja", errors);
        validateDatePicker(competitionDateDatePicker, errors);
        validateTimeTextField(competitionTimeTextField, errors);
        validateTextField(competitionBuildingNameTextField, "Unesite naziv zgrade", errors);
        validateTextField(competitionHallNameTextField, "Unesite naziv dvorane", errors);

        validateCompetitionResultsTableView(competitionResultsTableView,
                errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }


    public static void validateCompetitionForUser(TextField competitionNameTextField,
                                                  TextArea competitionDescriptionTextArea,
                                                  DatePicker dateOfCompetitionDatePicker,
                                                  TextField timeOfCompetitionTextField,
                                                  TextField auditoriumBuildingNameTextField,
                                                  TextField auditoriumHallNameTextField,
                                                  ListView<Student> competitionParticipantsListView)
            throws ValidationException{

        List<String> errors = new ArrayList<>();
        validateTextField(competitionNameTextField, "Unesite naziv natjecanja", errors);
        validateTextArea(competitionDescriptionTextArea, "Unesite opis natjecanja", errors);
        validateDatePicker(dateOfCompetitionDatePicker, errors);
        validateTimeTextField(timeOfCompetitionTextField, errors);
        validateTextField(auditoriumBuildingNameTextField, "Unesite naziv zgrade", errors);
        validateTextField(auditoriumHallNameTextField, "Unesite naziv dvorane", errors);
        validateList(competitionParticipantsListView.getItems(),
                "Odaberite barem jednog sudionika natjecanja", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }

    }

    public static void validateUpdateCompetition(TextField competitionNameTextField,
                                                 TextArea competitionDescriptionTextArea,
                                                 ComboBox<City> cityComboBox,
                                                 DatePicker dateOfCompetitionDatePicker,
                                                 TextField timeOfCompetitionTextField,
                                                 TextField auditoriumBuildingNameTextField,
                                                 TextField auditoriumHallNameTextField,
                                                 TableView<CompetitionResult> competitionResultsTableView)
                         throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(competitionNameTextField, "Unesite naziv natjecanja", errors);
        validateTextArea(competitionDescriptionTextArea, "Unesite opis natjecanja", errors);
        validateComboBox(cityComboBox, "Odaberite grad", errors);
        validateDatePicker(dateOfCompetitionDatePicker, errors);
        validateTimeTextField(timeOfCompetitionTextField, errors);
        validateTextField(auditoriumBuildingNameTextField, "Unesite naziv zgrade", errors);
        validateTextField(auditoriumHallNameTextField, "Unesite naziv dvorane", errors);
        validateCompetitionResultsTableView(competitionResultsTableView, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }

    }

    private static void validateCompetitionResultsTableView(TableView<CompetitionResult> competitionResultsTableView,
                                                            List<String> errors) {


        List<CompetitionResult> competitionResultsList = competitionResultsTableView.getItems();

        for(CompetitionResult competitionResult : competitionResultsList){
            if (competitionResult.score().compareTo(BigDecimal.ZERO) < 0){
                String  negativeScoreErrorMessage = "Za studenta " +
                        competitionResult.participant() +
                        " unijeli ste negativan broj bodova.";

                errors.add(negativeScoreErrorMessage);
            }

            if (competitionResult.score().compareTo(BigDecimal.valueOf(100)) > 0){
                String overOneHundredScoreMessage = "Za studenta " +
                        competitionResult.participant() +
                        " unijeli ste više od 100 bodova.";

                errors.add(overOneHundredScoreMessage);
            }
        }

    }


    public static void validateNewMathClub(TextField mathClubNameTextField, ComboBox<Address> mathClubAddressComboBox,
                                           TextField usernameRegisterTextField, PasswordField passwordRegisterPasswordField,
                                           PasswordField passwordConfirmPasswordField) throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(mathClubNameTextField, "Unesite naziv matematičkog kluba", errors);
        validateComboBox(mathClubAddressComboBox, "Odaberite adresu matematičkog kluba", errors);
        validateUsername(usernameRegisterTextField, errors);
        validatePasswordField(passwordRegisterPasswordField, passwordConfirmPasswordField, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    public static void validateProject(TextField projectNameTextField, TextArea projectDescriptionTextArea,
                                       ListView<MathClub> mathClubsListView,
                                       ListView<Student> selectedStudents, ComboBox <MathClub> organizerComboBox,
                                       DatePicker startOfProjectDatePicker, ComboBox<Address> projectAddressComboBox)
            throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(projectNameTextField, "Unesite naziv projekta", errors);
        validateTextArea(projectDescriptionTextArea, "Unesite opis projekta", errors);
        validateListView(mathClubsListView, "Odaberite matematički klub", errors);
        validateListView(selectedStudents, "Odaberite barem jednog sudionika projekta", errors);
        validateComboBox(organizerComboBox, "Odaberite organizatora projekta", errors);
        validateComboBox(projectAddressComboBox, "Odaberite adresu projekta", errors);
        validateDatePicker(startOfProjectDatePicker, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }



    public static void validateProjectForUser(TextField projectNameTextField, TextArea projectDescriptionTextArea,
                                              ListView<Student> projectMathClubsParticipantsListView,
                                              DatePicker beginningDateOfProjectDatePicker, ComboBox<City> cityComboBox,
                                              TextField streetNameTextField, TextField houseNumberTextField)
            throws ValidationException{

        List<String> errors = new ArrayList<>();
        validateTextField(projectNameTextField, "Unesite naziv projekta", errors);
        validateTextArea(projectDescriptionTextArea, "Unesite opis projekta", errors);
        validateDatePicker(beginningDateOfProjectDatePicker, errors);
        validateList(projectMathClubsParticipantsListView.getItems(), "Odaberite barem jednog sudionika projekta", errors);
        validateComboBox(cityComboBox, "Odaberite grad", errors);
        validateTextField(streetNameTextField, "Unesite naziv ulice", errors);
        validateHouseNumber(houseNumberTextField, errors);

        if(!errors.isEmpty()){
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    public static void validateUpdateProject(TextField newProjectNameTextField, TextArea projectDescriptionTextArea,
                                             TextField streetNameTextField, TextField houseNumberTextField,
                                             ComboBox<City> cityComboBox) throws  ValidationException{

        List<String> errors = new ArrayList<>();
        validateTextField(newProjectNameTextField, "Unesite naziv projekta", errors);
        validateTextArea(projectDescriptionTextArea, "Unesite opis projekta", errors);
        validateTextField(streetNameTextField, "Unesite naziv ulice", errors);
        validateHouseNumber(houseNumberTextField, errors);
        validateComboBox(cityComboBox, "Odaberite grad", errors);

        if (!errors.isEmpty()){
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    public static void validateClubMember(TextField nameTextField, TextField surnameTextField, ToggleGroup genderSelection,
                                          TextField emailTextField, TableView<SubjectGrade> studentGradesTableView,
                                          ToggleGroup yearOfStudyToggleGroup) throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(nameTextField, "Unesite ime studenta", errors);
        validateTextField(surnameTextField, "Unesite prezime studenta", errors);
        validateToggleGroup(genderSelection, "Odaberite spol", errors);
        validateEmail(emailTextField, errors);
        validateStudentGradesList(studentGradesTableView, errors);
        validateToggleGroup(yearOfStudyToggleGroup, "Odaberite godinu studija", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    public static void validateStudent(TextField nameTextField, TextField surnameTextField,
                                      ToggleGroup genderToggleGroup, TextField emailTextField, ComboBox<MathClub> mathClubComboBox,
                                       DatePicker joinDateDatePicker, TableView<SubjectGrade> studentGradesTableView,
                                       ToggleGroup yearOfStudyToggleGroup) throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(nameTextField, "Unesite ime studenta", errors);
        validateTextField(surnameTextField, "Unesite prezime studenta", errors);
        validateToggleGroup(genderToggleGroup, "Odaberite spol", errors);
        validateEmail(emailTextField, errors);
        validateComboBox(mathClubComboBox, "Odaberite matematički klub", errors);
        validateDatePickerForAddingNewStudent(mathClubComboBox, joinDateDatePicker,
                errors);
        validateStudentGradesList(studentGradesTableView, errors);
        validateToggleGroup(yearOfStudyToggleGroup, "Odaberite godinu studija", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    private static void validateStudentGradesList(TableView<SubjectGrade> studentGradesTableView, List<String> error) {
        for (SubjectGrade subjectGrade : studentGradesTableView.getItems()){
            try{
               Integer parsedGrade = Integer.parseInt(subjectGrade.getValue());

               if (!subjectGrade.getValue().equals(String.valueOf(parsedGrade))){
                   error.add("Molim unesite cijeli broj za predmet " + subjectGrade.getKey());

               }else if (parsedGrade.compareTo(1) < 0 || parsedGrade.compareTo(5) > 0){
                   error.add("Molim unesite ocijenu u rasponu od 1-5 za predmet " + subjectGrade.getKey());
               }



            }catch (NumberFormatException ex){
                error.add("Molim unesite ispravnu ocijenu za predmet " + subjectGrade.getKey());
            }
        }
    }


    public static void validateRegistrationInformation(TextField usernameRegisterTextField,
                                                       PasswordField passwordRegisterPasswordField,
                                                       PasswordField passwordConfirmPasswordField,
                                                       TextField clubNameTextField,
                                                       TextField streetNameTextField,
                                                       TextField houseNumberTextField,
                                                       ComboBox<City> cityComboBox) throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateUsername(usernameRegisterTextField, errors);
        validatePasswordField(passwordRegisterPasswordField, passwordConfirmPasswordField, errors);
        validateTextField(clubNameTextField, "Unesite naziv matematičkog kluba", errors);
        validateComboBox(cityComboBox, "Odaberite grad", errors);
        validateTextField(streetNameTextField, "Unesite naziv ulice", errors);
        validateHouseNumber(houseNumberTextField, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    private static void validateUsername(TextField usernameRegisterTextField, List<String> errors) {
        if (usernameRegisterTextField.getText().isEmpty()){
            errors.add("Unesite korisničko ime");
        }
        else if (!usernameRegisterTextField.getText().matches(ValidationRegex.VALID_USERNAME_PATTERN.getRegex())){
            errors.add("Korisničko ime mora sadržavati barem 4 znakova i ne smije sadržavati specijalne znakove");
        }
    }


    public static void validateUsernameChange(User currentUser, TextField changeUserNameTextField,
                                              PasswordField enterPasswordForUsernameChangePasswordField)
            throws ValidationException {

        List<String> errors = new ArrayList<>();
        validateUserNameTextField(currentUser, changeUserNameTextField, errors);
        validatePasswordFieldForUsernameChange(currentUser, enterPasswordForUsernameChangePasswordField, errors);

        if(!errors.isEmpty()){
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }

    }

    private static void validatePasswordFieldForUsernameChange(User currentUser,
                                                               PasswordField enterPasswordForUsernameChangePasswordField,
                                                               List<String> errors) {

        if (enterPasswordForUsernameChangePasswordField.getText().isEmpty()){
            errors.add("Lozinka ne smije biti prazna");
        }
        else if (!PasswordUtil.isPasswordCorrect(enterPasswordForUsernameChangePasswordField.getText(),
                currentUser.getHashedPassword())){
            errors.add("Unijeli ste krivu lozinku. Molim pokušajte ponovno.");
        }

    }

    private static void validateUserNameTextField(User currentUser, TextField changeUserNameTextField,
                                                  List<String> errors) {

        if (changeUserNameTextField.getText().isEmpty()){
            errors.add("Korisničko ime ne smije biti prazno");

        }
        else{
            if (!changeUserNameTextField.getText().matches(ValidationRegex.VALID_USERNAME_PATTERN.getRegex())){
                errors.add("Korisničko ime mora sadržavati barem 4 znakova i ne smije sadržavati specijalne znakove");
            }
            else if (changeUserNameTextField.getText().equals(currentUser.getUsername())){
                errors.add("Unesite novo korisničko ime");
            }

            else{
                List<User> users = FileReaderUtil.getUsers();
                for (User user : users){
                    if (user.getUsername().equals(changeUserNameTextField.getText())){
                        errors.add("Korisničko ime već postoji");
                        break;
                    }
                }
            }


        }


    }

    public static void validatePasswordChange(User currentUser, PasswordField changePasswordPasswordField,
                                              PasswordField confirmChangePasswordPasswordField,
                                              PasswordField enterOldPasswordPasswordField) throws ValidationException {

        if (enterOldPasswordPasswordField.getText().isEmpty()){
            throw new ValidationException("Unesite staru lozinku");
        }
        else if (!PasswordUtil.isPasswordCorrect(enterOldPasswordPasswordField.getText(),
                currentUser.getHashedPassword())){
            throw new ValidationException("Unijeli ste krivu lozinku. Molim pokušajte ponovno.");
        }

        List<String> errors = new ArrayList<>();
        validatePasswordField(changePasswordPasswordField, confirmChangePasswordPasswordField, errors);




    }

    private static void validateHouseNumber(TextField houseNumberTextField, List<String> errors) {
        if (houseNumberTextField.getText().isEmpty()){

            errors.add("Unesite kućni broj");

        }else if (!houseNumberTextField.getText().matches(ValidationRegex.VALID_HOUSE_NUMBER.getRegex())){
            errors.add("Unesite kućni broj u ispravnom formatu");
        }
    }

    private static void validatePasswordField(PasswordField passwordRegisterPasswordField,
                                              PasswordField passwordConfirmPasswordField, List<String> errors) {
        if (passwordRegisterPasswordField.getText().isEmpty()){
            errors.add("Unesite lozinku");
        }
        else if (passwordRegisterPasswordField.getText().length() < 8){
            errors.add("Lozinka mora sadržavati barem 8 znakova");
        }
        else if (!passwordRegisterPasswordField.getText().equals(passwordConfirmPasswordField.getText())){
            errors.add("Lozinke se ne podudaraju");
        }

        if (passwordConfirmPasswordField.getText().isEmpty()){
            errors.add("Potvrdite lozinku");
        }

    }


    private static void validateEmail(TextField emailTextField, List<String> errors) {
        String email = emailTextField.getText().trim();
        if (email.isEmpty()) {
            errors.add("Unesite email adresu");
        }
        else if (!email.matches(ValidationRegex.VALID_EMAIL_ADDRESS.getRegex())){
            errors.add("Unesite ispravnu email adresu");
        }
    }


    private static void validateTextField(TextField textField, String errorMessage, List<String> errors) {
        String text = textField.getText().trim();
        if (text.isEmpty()) {
            errors.add(errorMessage);
        }
    }

    private static void validateTimeTextField(TextField timeTextField, List<String> errors) {
        String timeText = timeTextField.getText().trim();

        if (timeText.isEmpty()) {
            errors.add("Unesite vijeme natjecanja");
        }

        try{
            LocalTime.parse(timeText,
                    DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_TIME_REGEX.getRegex()));

        }catch (DateTimeParseException ex){
            errors.add("Molim unesite vrijeme u ispravnom formatu - " +
                    ValidationRegex.VALID_LOCAL_TIME_REGEX.getRegex());
        }

    }

    private static void validateTextArea(TextArea textArea, String errorMessage, List<String> errors) {
        String text = textArea.getText().trim();
        if (text.isEmpty()) {
            errors.add(errorMessage);
        }
    }


      private static <T> void validateComboBox(ComboBox<T> comboBox, String errorMessage, List<String> errors) {
        if (comboBox.getValue() == null) {
            errors.add(errorMessage);
        }
    }

    private static <T> void validateListView(ListView<T> listView, String errorMessage, List<String> errors) {
        if (listView.getSelectionModel().getSelectedItems().isEmpty()) {
            errors.add(errorMessage);
        }
    }


    private static void validateDatePickerForAddingNewStudent(ComboBox<MathClub> selectedMathClub,DatePicker datePicker,
                                                              List<String> errors){


        MathClub selectedMathClubForStudent = selectedMathClub.getValue();

        if (Optional.ofNullable(selectedMathClubForStudent).isPresent()){

            if (!selectedMathClubForStudent.getId().equals(0L) && Optional.ofNullable(datePicker.getValue()).isEmpty()){
                errors.add("Odaberite datum učlanjivanja studenta u studentski klub");
            }
        }


    }

    private static void validateDatePicker(DatePicker datePicker, List<String> errors) {
        if (datePicker.getValue() == null || datePicker.getValue().toString().isEmpty()) {
            errors.add("Odaberite datum");
        }

    }

    private static <T> void validateList(List<T> list, String errorMessage, List<String> errors) {
        if (list.isEmpty()) {
            errors.add(errorMessage);
        }
    }


    private static void validateToggleGroup(ToggleGroup toggleGroup, String errorMessage, List<String> errors) {
        if (toggleGroup.getSelectedToggle() == null) {
            errors.add(errorMessage);
        }
    }



    public static void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public static void showSuccessAlert(String header, String content){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spremanje uspješno");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();

    }

    public static void showSuccessLoginAlert(String header, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prijava uspješna");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();

    }



    public static boolean showConfirmationDialog(String dialogTitle, String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(dialogTitle);
        alert.setHeaderText(title);
        alert.setContentText(content);


        ButtonType buttonTypeYes = new ButtonType("Da");
        ButtonType buttonTypeNo = new ButtonType("Ne", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);


        Optional<ButtonType> result = alert.showAndWait();


        return result.isPresent() && result.get() == buttonTypeYes;
    }


    public static void validateAddress(TextField streetNameTextField, TextField houseNumberTextField,
                                       ComboBox<City> cityPickerComboBox) throws ValidationException {

            List<String> errors = new ArrayList<>();

            validateTextField(streetNameTextField, "Unesite naziv ulice", errors);
            validateHouseNumber(houseNumberTextField, errors);
            validateComboBox(cityPickerComboBox, "Odaberite grad", errors);

            if (!errors.isEmpty()){
                throw new ValidationException(String.join(LINE_SEPARATOR, errors));
            }
    }
}
