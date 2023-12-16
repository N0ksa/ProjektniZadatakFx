package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.entities.ClubMembership;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.entities.SubjectGrade;
import hr.java.project.projectfxapp.enums.YearOfStudy;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import hr.java.project.projectfxapp.utility.FileWriterUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.time.LocalDate;
import java.util.*;

public class AddNewStudentController {

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
    private RadioButton drugaGodinaRadioButton;
    @FXML
    private RadioButton trecaGodinaRadioButton;
    @FXML
    private ToggleGroup yearOfStudySelection;
    @FXML
    private RadioButton prvaGodinaRadioButton;
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
                List<YearOfStudy> selectedYears = getSelectedYears(newValue);

                updateStudentGradesTableView(selectedYears);
            }
        });


        List<MathClub> mathClubs = FileReaderUtil.getMathClubsFromFile(FileReaderUtil.getStudentsFromFile(), FileReaderUtil.getAddressesFromFile());
        ObservableList<MathClub> observableMathClubsList = FXCollections.observableList(mathClubs);
        mathClubComboBox.setItems(observableMathClubsList);

    }


    private List<YearOfStudy> getSelectedYears(Toggle toggle) {
        List<YearOfStudy> selectedYears = new ArrayList<>();

        if(toggle == prvaGodinaRadioButton){
            selectedYears.add(YearOfStudy.FIRST_YEAR);

        }
        else if ( toggle == drugaGodinaRadioButton) {
            selectedYears.add(YearOfStudy.FIRST_YEAR);
            selectedYears.add(YearOfStudy.SECOND_YEAR);
        }
        else if (toggle == trecaGodinaRadioButton) {

            selectedYears.add(YearOfStudy.FIRST_YEAR);
            selectedYears.add(YearOfStudy.SECOND_YEAR);
            selectedYears.add(YearOfStudy.THIRD_YEAR);
        }

        return selectedYears;
    }


    private void updateStudentGradesTableView(List<YearOfStudy> selectedYears) {

        studentGradesTableView.getItems().clear();

        for(YearOfStudy yearOfStudy : selectedYears){
            for (String subject : yearOfStudy.getAvailableSubjects()) {
                SubjectGrade subjectGrade = new SubjectGrade(subject, "Unesi ocijenu");
                studentGradesTableView.getItems().add(subjectGrade);
            }
        }

    }


    public void showDatePicker(ActionEvent actionEvent) {
        joinDateDatePicker.setVisible(true);
        chooseDateLabel.setVisible(true);
    }

    public void saveStudent(ActionEvent actionEvent) {

        Long studentId = FileWriterUtil.getNextStudentId();
        String studentName = studentNameTextField.getText();
        String studentSurname = studentSurnameTextField.getText();
        String studentEmail = studentEmailTextField.getText();

        MathClub studentClub = mathClubComboBox.getValue();
        LocalDate joinDate = joinDateDatePicker.getValue();

        Integer yearOfStudy = 0;

        if (prvaGodinaRadioButton.isSelected()){
            yearOfStudy = 1;
        }else if(drugaGodinaRadioButton.isSelected()){
            yearOfStudy = 2;
        }else if (trecaGodinaRadioButton.isSelected()){
            yearOfStudy = 3;
        }

        ClubMembership studentClubMembership = new ClubMembership(studentClub.getId(), joinDate);

        Map<String, Integer> studentGrades = new LinkedHashMap<>();
        for (SubjectGrade subjectAndGrade : studentGradesTableView.getItems()){
            studentGrades.put(subjectAndGrade.getSubject(), Integer.parseInt(subjectAndGrade.getGrade()));
        }

        Student newStudent = new Student(studentId, studentName, studentSurname, studentEmail, yearOfStudy,
                studentGrades, studentClubMembership);


        List<Student> students = FileReaderUtil.getStudentsFromFile();
        students.add(newStudent);

        FileWriterUtil.saveStudentsToFile(students);

    }
}

