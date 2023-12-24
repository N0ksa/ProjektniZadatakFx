package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.filter.StudentFilter;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentsSearchController {

    @FXML
    private  TextField studentSurnameTextField;
    @FXML
    private TextField studentNameTextField;
    @FXML
    private ComboBox <MathClub> clubComboBox;
    @FXML
    private TableView<Student> studentsTableView;
    @FXML
    private TableColumn<Student,String> studentNameTableColumn;
    @FXML
    private TableColumn<Student, String> studentSurnameTableColumn;
    @FXML
    private TableColumn<Student, String> studentJoinDateTableColumn;
    @FXML
    private TableColumn<Student, String> studentClubTableColumn;
    @FXML
    private TableColumn<Student, String> studentAverageGradeTableColumn;
    @FXML
    private TableColumn<Student, String> studentEmailTableColumn;
    @FXML
    private TableColumn<Student, String> studentYearOfStudyTableColumn;



    public void initialize(){

        List<MathClub> mathClubsList = DatabaseUtil.getMathClubs();
        ObservableList<MathClub> observableMathClubsList = FXCollections.observableList(mathClubsList);

        clubComboBox.setItems(observableMathClubsList);

        studentsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        studentNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        studentSurnameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getSurname());
            }
        });

        studentJoinDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                LocalDate joinDate = param.getValue().getClubMembership().getJoinDate();

                if (Optional.ofNullable(joinDate).isPresent()){

                    return new ReadOnlyStringWrapper(param.getValue().getClubMembership().getJoinDate()
                            .format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex())));
                }else{
                    return new ReadOnlyStringWrapper("/");
                }

            }
        });



        studentClubTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                Optional<MathClub> clubOfStudent = mathClubsList.stream()
                        .filter(mathClub -> mathClub.hasMember(param.getValue()))
                        .findFirst();

                return new ReadOnlyStringWrapper(clubOfStudent.map(MathClub::getName).orElse("Nije član kluba"));
            }
        });


        studentAverageGradeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {


                return new ReadOnlyStringWrapper(String.format("%.1f", param.getValue().calculateAverageGrade()));
            }
        });


        studentEmailTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getEmail());
            }
        });

        studentYearOfStudyTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getYearOfStudy().toString());
            }
        });



    }

    public void studentSearch(){

        String studentName = studentNameTextField.getText();
        String studentSurname = studentSurnameTextField.getText();
        MathClub mathclub = clubComboBox.getValue();

        StudentFilter studentFilter = new StudentFilter(studentName, studentSurname, mathclub);
        List<Student> filteredStudents = DatabaseUtil.getStudentsByFilter(studentFilter);

        ObservableList<Student> observableStudentList = FXCollections.observableList(filteredStudents);
        studentsTableView.setItems(observableStudentList);
    }


    public void deleteStudent(ActionEvent actionEvent) {

        Student studentForDeletion = studentsTableView.getSelectionModel().getSelectedItem();

        if (Optional.ofNullable(studentForDeletion).isPresent()) {
            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda brisanja",
                    "Jeste li sigurni da želite obrisati studenta?",
                    "Ova radnja je nepovratna.");

            if (positiveConfirmation) {
                boolean successfulDeletion = DatabaseUtil.deleteStudent(studentForDeletion);
                if (successfulDeletion) {
                    ValidationProtocol.showSuccessAlert("Brisanje uspješno",
                            "Uspješno ste obrisali studenta : " + studentForDeletion.getName() + " " +
                                    studentForDeletion.getSurname());
                } else {
                    ValidationProtocol.showErrorAlert("Brisanje neuspješno",
                            "Student " + studentForDeletion.getName() + " " + studentForDeletion.getSurname() +
                                    " nije obrisan", "Nažalost studenta nije moguće obrisati");
                }

            }

        }
    }

    public void reset(ActionEvent actionEvent) {
        studentNameTextField.setText("");
        studentsTableView.getItems().clear();
        clubComboBox.getSelectionModel().clearSelection();
    }
}
