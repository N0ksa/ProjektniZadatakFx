package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.ClubMembership;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.entities.SubjectGrade;
import hr.java.project.projectfxapp.enums.YearOfStudy;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddNewStudentUserController {

    @FXML
    private ToggleGroup yearOfStudySelection;

    @FXML
    private RadioButton drugaGodinaRadioButton;

    @FXML
    private RadioButton prvaGodinaRadioButton;

    @FXML
    private TextField studentEmailTextField;

    @FXML
    private TableView<SubjectGrade> studentGradesTableView;

    @FXML
    private ImageView studentImageView;

    @FXML
    private TextField studentNameTextField;

    @FXML
    private TextField studentSurnameTextField;

    @FXML
    private TableColumn<SubjectGrade, String> subjectGradeTableColumn;

    @FXML
    private TableColumn<SubjectGrade, String> subjectNameTableColumn;

    @FXML
    private RadioButton trecaGodinaRadioButton;





    public void initialize(){


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

        studentImageView.setImage(new Image(getClass().getResource("/images/question_mark_person_logo.png")
                .toExternalForm()));

    }


   public void addImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odaberi sliku");


         fileChooser.getExtensionFilters().addAll(
                 new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));


        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {

            Image newImage = new Image(selectedFile.toURI().toString());
            studentImageView.setImage(newImage);
        }

    }


    public void saveMember(ActionEvent event) {
        try{

            ValidationProtocol.validateClubMember(studentNameTextField, studentSurnameTextField, studentEmailTextField
                    ,studentGradesTableView, yearOfStudySelection);

            Long studentId = 0L;
            String studentName = studentNameTextField.getText();
            String studentSurname = studentSurnameTextField.getText();
            String studentEmail = studentEmailTextField.getText();

            MathClub studentClub = null;
            LocalDate joinDate = LocalDate.now();

            Integer yearOfStudy = 0;

            if (prvaGodinaRadioButton.isSelected()){
                yearOfStudy = 1;
            }else if(drugaGodinaRadioButton.isSelected()){
                yearOfStudy = 2;
            }else if (trecaGodinaRadioButton.isSelected()){
                yearOfStudy = 3;
            }

            ClubMembership studentClubMembership = new ClubMembership(0L,studentClub.getId(), joinDate);

            Map<String, Integer> studentGrades = new LinkedHashMap<>();
            for (SubjectGrade subjectAndGrade : studentGradesTableView.getItems()){
                studentGrades.put(subjectAndGrade.getSubject(), Integer.parseInt(subjectAndGrade.getGrade()));
            }

            Student newStudent = new Student(studentId, studentName, studentSurname, studentEmail, yearOfStudy,
                    studentGrades, studentClubMembership);


            List<Student> students = new ArrayList<>();
            students.add(newStudent);
            DatabaseUtil.saveStudents(students);


            ValidationProtocol.showSuccessAlert("Spremanje novog studenta je bilo uspješno",
                    "Student " + newStudent.getName() + " " + newStudent.getSurname() + " uspješno se spremio!");



        }
        catch (ValidationException ex){
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }

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


}
