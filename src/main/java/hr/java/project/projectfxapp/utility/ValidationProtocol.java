package hr.java.project.projectfxapp.utility;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.exception.ValidationException;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidationProtocol {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static void validateCompetition(TextField competitionNameTextField, TextArea competitionDescriptionTextArea,
                                           ComboBox<Address> competitionAddressComboBox, DatePicker competitionDateDatePicker,
                                           TextField competitionTimeTextField, TextField competitionBuildingNameTextField,
                                           TextField competitionHallNameTextField,
                                           TableView<CompetitionResult> competitionResultsTableView) throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(competitionNameTextField, "Unesite naziv natjecanja", errors);
        validateTextArea(competitionDescriptionTextArea, "Unesite opis natjecanja", errors);
        validateComboBox(competitionAddressComboBox, "Odaberite adresu natjecanja", errors);
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

    private static void validateCompetitionResultsTableView(TableView<CompetitionResult> competitionResultsTableView,
                                                            List<String> errors) {


        List<CompetitionResult> competitionResultsList = competitionResultsTableView.getItems();

        if(competitionResultsList.isEmpty()){
            errors.add("Mora biti unešen rezultat za barem jednog natjecatelja");
        }
        else{
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

    }


    public static void validateNewMathClub(TextField mathClubNameTextField, ComboBox<Address> mathClubAddressComboBox)
            throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(mathClubNameTextField, "Unesite naziv matematičkog kluba", errors);
        validateComboBox(mathClubAddressComboBox, "Odaberite adresu matematičkog kluba", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    public static void validateProject(TextField projectNameTextField, TextArea projectDescriptionTextArea,
                                       ListView<MathClub> mathClubsListView,
                                       ListView<Student> selectedStudents) throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(projectNameTextField, "Unesite naziv projekta", errors);
        validateTextArea(projectDescriptionTextArea, "Unesite opis projekta", errors);
        validateListView(mathClubsListView, "Odaberite matematički klub", errors);
        validateListView(selectedStudents, "Odaberite barem jednog sudionika projekta", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    public static void validateStudent(TextField nameTextField, TextField surnameTextField,
                                       TextField emailTextField, ComboBox<MathClub> mathClubComboBox,
                                       DatePicker joinDateDatePicker, TableView<SubjectGrade> studentGradesTableView,
                                       ToggleGroup yearOfStudyToggleGroup) throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(nameTextField, "Unesite ime studenta", errors);
        validateTextField(surnameTextField, "Unesite prezime studenta", errors);
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
               Integer parsedGrade = Integer.parseInt(subjectGrade.getGrade());

               if (!subjectGrade.getGrade().equals(String.valueOf(parsedGrade))){
                   error.add("Molim unesite cijeli broj za predmet " + subjectGrade.getSubject());

               }else if (parsedGrade.compareTo(1) < 0 || parsedGrade.compareTo(5) > 0){
                   error.add("Molim unesite ocijenu u rasponu od 1-5 za predmet " + subjectGrade.getSubject());
               }



            }catch (NumberFormatException ex){
                error.add("Molim unesite ispravnu ocijenu za predmet " + subjectGrade.getSubject());
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
            LocalTime time = LocalTime.parse(timeText, DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_TIME_REGEX.getRegex()));

        }catch (DateTimeParseException ex){
            errors.add("Molim unesite vrijeme u ispravnom formatu - " + ValidationRegex.VALID_LOCAL_TIME_REGEX.getRegex());
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
        if (datePicker.getValue() == null) {
            errors.add("Odaberite datum natjecanja");
        }
        else{
            try{
                LocalDate.parse(datePicker.getValue().toString());
            }
            catch (DateTimeParseException ex){
                errors.add("Unesi datum u ispravnom formatu");
            }
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

}
