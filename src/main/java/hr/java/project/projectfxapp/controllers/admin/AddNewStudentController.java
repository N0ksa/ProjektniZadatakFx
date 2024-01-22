package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.Gender;
import hr.java.project.projectfxapp.enums.YearOfStudy;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AddNewStudentController {

    @FXML
    private RadioButton femaleGenderRadioButton;
    @FXML
    private ToggleGroup genderSelection;
    @FXML
    private RadioButton maleGenderRadioButton;
    @FXML
    private Label chooseDateLabel;
    @FXML
    private DatePicker joinDateDatePicker;
    @FXML
    private ComboBox<MathClub> mathClubComboBox;
    @FXML
    private TableColumn<SubjectGrade, String> subjectNameTableColumn;
    @FXML
    private TableColumn<SubjectGrade, String> subjectGradeTableColumn;
    @FXML
    private RadioButton  prvaGodinaRadioButton, drugaGodinaRadioButton, trecaGodinaRadioButton;
    @FXML
    private ToggleGroup yearOfStudySelection;
    @FXML
    private TableView<SubjectGrade> studentGradesTableView;
    @FXML
    private TextField studentEmailTextField;
    @FXML
    private TextField studentSurnameTextField;
    @FXML
    private TextField studentNameTextField;

    public void initialize() {

        studentGradesTableView.setEditable(true);
        subjectNameTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getSubject()));

        subjectGradeTableColumn.setCellValueFactory(param -> param.getValue().gradeProperty());
        subjectGradeTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        subjectGradeTableColumn.setOnEditCommit(event -> {

            String newGrade = event.getNewValue();

            int index = event.getTablePosition().getRow();

            SubjectGrade subjectGrade = event.getTableView().getItems().get(index);

            subjectGrade.setGrade(newGrade);
        });


        yearOfStudySelection.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                YearOfStudy selectedYear = getSelectedYears(newValue);

                updateStudentGradesTableView(selectedYear);
            }
        });


        List<MathClub> mathClubs = DatabaseUtil.getMathClubs();

        ObservableList<MathClub> observableMathClubsList = FXCollections.observableList(mathClubs);
        mathClubComboBox.setItems(observableMathClubsList);

    }


    private YearOfStudy getSelectedYears(Toggle toggle) {

        YearOfStudy selectedYear = YearOfStudy.FIRST_YEAR;

        if ( toggle == drugaGodinaRadioButton) {
            selectedYear = YearOfStudy.SECOND_YEAR;
        }
        else if (toggle == trecaGodinaRadioButton) {
            selectedYear = YearOfStudy.THIRD_YEAR;
        }

        return selectedYear;
    }


    private void updateStudentGradesTableView(YearOfStudy yearOfStudy) {

        studentGradesTableView.getItems().clear();
        for (String subject : yearOfStudy.getCombinedSubjectsUpToYear()) {
            SubjectGrade subjectGrade = new SubjectGrade(subject, "Unesi ocijenu");
            studentGradesTableView.getItems().add(subjectGrade);
        }

    }


    public void showDatePicker(ActionEvent actionEvent) {

        MathClub selectedMathClub = mathClubComboBox.getSelectionModel().getSelectedItem();

        if (Optional.ofNullable(selectedMathClub).isEmpty()){
            joinDateDatePicker.setVisible(false);
            chooseDateLabel.setVisible(false);

        }
        else{
            joinDateDatePicker.setVisible(true);
            chooseDateLabel.setVisible(true);
        }

    }

    public void saveStudent(ActionEvent actionEvent) {

        try {

            ValidationProtocol.validateStudent(studentNameTextField, studentSurnameTextField, genderSelection,
                    studentEmailTextField, mathClubComboBox, joinDateDatePicker, studentGradesTableView,
                    yearOfStudySelection);

            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda unosa",
                    "Jeste li sigurni da želite unijeti novog studenta?",
                    "Ako želite unijeti studenta: " + studentNameTextField.getText() + " "
                            + studentSurnameTextField.getText() +
                            "\nPritisnite Da za potvrdu");

            if (positiveConfirmation) {
                Student newStudent = buildNewStudent();
                List<Student> students = new ArrayList<>();
                students.add(newStudent);

                boolean success = DatabaseUtil.saveStudents(students);
                if (success) {

                    User currentUser = SessionManager.getInstance().getCurrentUser();
                    Change change = Change.create(currentUser, "/",
                            "Dodan student: " + newStudent.getName() + " " + newStudent.getSurname(),
                            "Student");

                    ChangesManager.getChanges().add(change);

                    ValidationProtocol.showSuccessAlert("Spremanje novog studenta je bilo uspješno",
                            "Student " + newStudent.getName() + " " + newStudent.getSurname() +
                                    " uspješno se spremio!");

                } else {
                    ValidationProtocol.showErrorAlert("Greška pri spremanju", "Greška pri spremanju studenta",
                            "Došlo je do greške pri spremanju studenta u bazu podataka");
                }

            }
        }
        catch (ValidationException ex){
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }


    }

    private Student buildNewStudent() {

        Long studentId = 0L;
        String studentName = studentNameTextField.getText();
        String studentSurname = studentSurnameTextField.getText();
        String studentEmail = studentEmailTextField.getText();
        Student.StudentBuilder studentBuilder = new Student.StudentBuilder(studentId, studentName, studentSurname)
                .email(studentEmail);

        MathClub studentClub = mathClubComboBox.getValue();
        LocalDate joinDate = joinDateDatePicker.getValue();


        Integer yearOfStudy = getYearOfStudy();
        studentBuilder.yearOfStudy(yearOfStudy);

        ClubMembership studentClubMembership = new ClubMembership(0L,studentClub.getId(), joinDate);
        studentBuilder.clubMembership(studentClubMembership);

        Map<String, Integer> studentGrades = new LinkedHashMap<>();
        for (SubjectGrade subjectAndGrade : studentGradesTableView.getItems()){
            studentGrades.put(subjectAndGrade.getSubject(), Integer.parseInt(subjectAndGrade.getGrade()));
        }
        studentBuilder.grades(studentGrades);


        Gender gender = getStudentGender();
        studentBuilder.gender(gender.getGender());
        return studentBuilder.build();
    }

    private Gender getStudentGender() {
        if (femaleGenderRadioButton.isSelected()){
            return Gender.Female;
        }
        else{
            return Gender.Male;
        }
    }

    private Integer getYearOfStudy() {
        Integer yearOfStudy = 0;

        if (prvaGodinaRadioButton.isSelected()){
            yearOfStudy = 1;
        }else if(drugaGodinaRadioButton.isSelected()){
            yearOfStudy = 2;
        }else if (trecaGodinaRadioButton.isSelected()){
            yearOfStudy = 3;
        }
        return yearOfStudy;
    }


}

