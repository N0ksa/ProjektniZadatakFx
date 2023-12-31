package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.entities.SubjectGrade;
import hr.java.project.projectfxapp.enums.YearOfStudy;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.SessionManager;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpdateMemberInformationController {

    @FXML
    private RadioButton drugaGodinaRadioButton;

    @FXML
    private RadioButton femaleGenderRadioButton;

    @FXML
    private ToggleGroup genderSelection;

    @FXML
    private RadioButton maleGenderRadioButton;

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

    @FXML
    private ToggleGroup yearOfStudySelection;

    private static String imagePath = "/images/question_mark_person_logo.png";



    public void initialize() {
        Student memberToUpdate = SessionManager.getInstance().getCurrentStudent();
        studentNameTextField.setText(memberToUpdate.getName());
        studentSurnameTextField.setText(memberToUpdate.getSurname());
        studentEmailTextField.setText(memberToUpdate.getEmail());
        if (memberToUpdate.getGender().equals("Male")) {
            maleGenderRadioButton.setSelected(true);
        } else {
            femaleGenderRadioButton.setSelected(true);
        }

        if (memberToUpdate.getYearOfStudy() == 1) {
            prvaGodinaRadioButton.setSelected(true);
        } else if (memberToUpdate.getYearOfStudy() == 2) {
            prvaGodinaRadioButton.disableProperty().set(true);
            drugaGodinaRadioButton.setSelected(true);
        } else if (memberToUpdate.getYearOfStudy() == 3) {
            prvaGodinaRadioButton.disableProperty().set(true);
            drugaGodinaRadioButton.disableProperty().set(true);
            trecaGodinaRadioButton.setSelected(true);
        }

        studentImageView.setImage(new Image(getClass().getResource(memberToUpdate.getPicture().getPicturePath()).toExternalForm()));

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

        List<SubjectGrade> subjectGrades = memberToUpdate.getGrades().entrySet().stream()
                .map(entry -> new SubjectGrade(entry.getKey(), String.valueOf(entry.getValue())))
                .collect(Collectors.toList());
        studentGradesTableView.getItems().addAll(subjectGrades);

        yearOfStudySelection.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            Integer selectedYear = getYearOfStudy();

            if (newValue != null) {
                YearOfStudy newYearOfStudy = getSelectedYears(newValue);

                studentGradesTableView.getItems().clear();

                if (selectedYear.equals(memberToUpdate.getYearOfStudy())) {
                    studentGradesTableView.getItems().addAll(subjectGrades);
                } else if (selectedYear > memberToUpdate.getYearOfStudy()) {

                    List<SubjectGrade> additionalSubjects = newYearOfStudy.getCombinedSubjectsUpToYear()
                            .stream()
                            .filter(subject -> subjectGrades.stream().noneMatch(grade -> grade.getSubject().equals(subject)))
                            .map(subject -> new SubjectGrade(subject, "Unesi ocijenu"))
                            .collect(Collectors.toList());

                    studentGradesTableView.getItems().addAll(subjectGrades);
                    studentGradesTableView.getItems().addAll(additionalSubjects);
                }
            }
        });
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


    public void changeImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odaberi sliku");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String imageName = selectedFile.getName();

            String relativePath = "/images/" + imageName;

            Image newImage = new Image(getClass().getResource(relativePath).toExternalForm());
            studentImageView.setImage(newImage);
            imagePath = relativePath;
        }
    }



    public void saveMember(ActionEvent event) {

        try {
            ValidationProtocol.validateClubMember(studentNameTextField, studentSurnameTextField,
                    genderSelection,studentEmailTextField, studentGradesTableView, yearOfStudySelection);

            Student memberToUpdate = SessionManager.getInstance().getCurrentStudent();

           boolean positiveConfirmation =  ValidationProtocol.showConfirmationDialog
                   ("Potvrda ažuriranja člana", "Ažuriranje člana",
                    "Jeste li sigurni da želite ažurirati člana " + memberToUpdate.getName() +
                            " " + memberToUpdate.getSurname() + "?" +
                            "\nAko ste sigurni pritisnite Da");


            if (positiveConfirmation) {

                boolean updateSuccessful = updateMember(memberToUpdate);

                if (updateSuccessful){
                    ValidationProtocol.showSuccessAlert("Ažuriranje člana je bilo uspješno",
                            "Član " + memberToUpdate.getName() + " " + memberToUpdate.getSurname() + " uspješno se ažurirao!");
                }
                else{
                    ValidationProtocol.showErrorAlert("Greška pri ažuriranju", "Ažuriranje člana nije uspjelo",
                            "Pokušajte ponovno");
                }



            }

        } catch (ValidationException ex){
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }

    }

    private boolean updateMember(Student memberToUpdate) {

        memberToUpdate.setName(studentNameTextField.getText());
        memberToUpdate.setSurname(studentSurnameTextField.getText());
        memberToUpdate.setEmail(studentEmailTextField.getText());

        if (maleGenderRadioButton.isSelected()) {
            memberToUpdate.setGender("Male");
        } else {
            memberToUpdate.setGender("Female");
        }

        memberToUpdate.setYearOfStudy(getYearOfStudy());

        Map<String, Integer> studentGrades = new LinkedHashMap<>();
        for (SubjectGrade subjectAndGrade : studentGradesTableView.getItems()){
            studentGrades.put(subjectAndGrade.getSubject(), Integer.parseInt(subjectAndGrade.getGrade()));
        }

        memberToUpdate.setGrades(studentGrades);

        memberToUpdate.getPicture().setPicturePath(imagePath);

        return DatabaseUtil.updateStudent(memberToUpdate);
    }

}
