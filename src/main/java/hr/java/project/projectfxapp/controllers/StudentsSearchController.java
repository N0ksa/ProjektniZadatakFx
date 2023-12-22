package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private TextField studentNameTextField;
    @FXML
    private ComboBox <String> clubComboBox;
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

        List<MathClub> mathClubs = DatabaseUtil.getMathClubs();

        ObservableList <String> obeservableMathClubs = FXCollections.observableList(mathClubs.stream()
                .map(mathClub -> mathClub.getName())
                .collect(Collectors.toList()));

        clubComboBox.setItems(obeservableMathClubs);
        clubComboBox.getItems().add(0, "Svi klubovi");

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
                Optional<MathClub> clubOfStudent = mathClubs.stream()
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
        String clubName = clubComboBox.getValue();

        List<Student> students = DatabaseUtil.getStudents();
        List<Student> filteredStudents = students.stream()
                .filter(student -> student.getName().contains(studentName) || student.getSurname().contains(studentName))
                .filter(student -> {

                    if (Optional.ofNullable(clubName).isEmpty() || clubName.equalsIgnoreCase("svi klubovi")) {
                        return true;
                    }

                    List<MathClub> mathClubs = FileReaderUtil.getMathClubsFromFile(FileReaderUtil.getStudentsFromFile()
                            , FileReaderUtil.getAddressesFromFile());

                    Optional<MathClub> clubOfStudent = mathClubs.stream()
                            .filter(mathClub -> mathClub.hasMember(student))
                            .findFirst();

                    return clubOfStudent.map(mathClub -> mathClub.getName().equals(clubName)).orElse(false);
                })
                .collect(Collectors.toList());

        ObservableList<Student> observableStudentList = FXCollections.observableList(filteredStudents);
        studentsTableView.setItems(observableStudentList);
    }













}
