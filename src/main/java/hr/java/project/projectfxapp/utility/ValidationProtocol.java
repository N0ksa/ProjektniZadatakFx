package hr.java.project.projectfxapp.utility;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.exception.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        validateDatePicker(competitionDateDatePicker, "Odaberite datum natjecanja", errors);
        validateTimeTextField(competitionTimeTextField,"Unesite vijeme natjecanja", errors);
        validateTextField(competitionBuildingNameTextField, "Unesite naziv zgrade", errors);
        validateTextField(competitionHallNameTextField, "Unesite naziv dvorane", errors);

        validateCompetitionResultsTableView(competitionResultsTableView,
                "Mora biti unešen rezultat za barem jednog natjecatelja", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    private static void validateCompetitionResultsTableView(TableView<CompetitionResult> competitionResultsTableView,
                                                            String errorMessage, List<String> errors) {


        List<CompetitionResult> competitionResultsList = competitionResultsTableView.getItems();

        if(competitionResultsList.isEmpty()){
            errors.add(errorMessage);
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


    public static void validateNewMathClub(TextField mathClubNameTextField, ComboBox<Address> mathClubAddressComboBox,
                                           ListView<Student> selectedMembers, DatePicker membersJoinDateDatePicker)
            throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(mathClubNameTextField, "Unesite naziv matematičkog kluba", errors);
        validateComboBox(mathClubAddressComboBox, "Odaberite adresu matematičkog kluba", errors);
        validateList(selectedMembers.getItems(), "Odaberite barem jednog člana kluba", errors);
        validateDatePicker(membersJoinDateDatePicker, "Odaberite datum pridruživanja klubu", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    public static void validateProject(TextField nameTextField, TextArea descriptionTextArea,
                                       ComboBox<MathClub> mathClubsComboBox,
                                       Set<Student> selectedStudents) throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(nameTextField, "Unesite naziv projekta", errors);
        validateTextArea(descriptionTextArea, "Unesite opis projekta", errors);
        validateComboBox(mathClubsComboBox, "Odaberite matematički klub", errors);
        validateSet(selectedStudents, "Odaberite barem jednog sudionika projekta", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }

    public static void validateStudent(TextField nameTextField, TextField surnameTextField,
                                       TextField emailTextField, ComboBox<MathClub> mathClubComboBox,
                                       DatePicker joinDateDatePicker, TableColumn<SubjectGrade, String> subjectGrades,
                                       ToggleGroup yearOfStudyToggleGroup) throws ValidationException {

        List<String> errors = new ArrayList<>();

        validateTextField(nameTextField, "Unesite ime studenta", errors);
        validateTextField(surnameTextField, "Unesite prezime studenta", errors);
        validateEmail(emailTextField, errors);
        validateComboBox(mathClubComboBox, "Odaberite matematički klub", errors);
        validateDatePicker(joinDateDatePicker, "Odaberite datum pridruživanja klubu", errors);
        validateList(subjectGrades., "Unesite barem jednu ocjenu", errors);
        validateToggleGroup(yearOfStudyToggleGroup, "Odaberite godinu studija", errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(LINE_SEPARATOR, errors));
        }
    }



    private static void validateEmail(TextField emailTextField, List<String> errors) {
        String email = emailTextField.getText().trim();
        if (email.isEmpty()) {
            errors.add("Unesite email adresu");
        }
        else if (!email.matches(ValidationRegex.VALID_WEB_ADDRESS.getRegex())){
            errors.add("Unesite ispravnu email adresu");
        }
    }


    private static void validateTextField(TextField textField, String errorMessage, List<String> errors) {
        String text = textField.getText().trim();
        if (text.isEmpty()) {
            errors.add(errorMessage);
        }
    }

    private static void validateTimeTextField(TextField timeTextField, String errorMessage, List<String> errors) {
        String timeText = timeTextField.getText().trim();

        if (timeText.isEmpty()) {
            errors.add(errorMessage);
        }

        try{
            LocalTime time = LocalTime.parse(timeText, DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_TIME_REGEX.getRegex()));

        }catch (DateTimeParseException ex){
            errors.add("Molim unesite vrijeme u ispravnom formatu - " + ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex());
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

    private static void validateDatePicker(DatePicker datePicker, String errorMessage, List<String> errors) {
        if (datePicker.getValue() == null) {
            errors.add(errorMessage);
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

    private static <T> void validateSet(Set<T> set, String errorMessage, List<String> errors) {
        if (set.isEmpty()) {
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
}
