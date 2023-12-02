package hr.java.project.projectfxapp;

import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentsSearchController {

    @FXML
    private TextField studentNameTextField;
    @FXML
    private ComboBox <String> clubComboBox;
    @FXML
    private TableView studentsTableView;
    @FXML
    private TableColumn studentNameTableColumn;
    @FXML
    private TableColumn studentSurnameTableColumn;
    @FXML
    private TableColumn studentJoinDateTableColumn;
    @FXML
    private TableColumn studentClubTableColumn;
    @FXML
    private TableColumn studentAverageGradeTableColumn;
    @FXML
    private TableColumn studentEmailTableColumn;
    @FXML
    private TableColumn studentYearOfStudyTableColumn;

    private static List<MathClub> mathClubs;
    private static List<Student> students  = FileReaderUtil.getStudentsFromFile();


    public void initialize(){
        mathClubs = FileReaderUtil.getMathClubsFromFile(FileReaderUtil.getStudentsFromFile(), FileReaderUtil.getAddressesFromFile());
        students  = FileReaderUtil.getStudentsFromFile();

        ObservableList <String> obeservableMathClubs = FXCollections.observableList(mathClubs.stream()
                .map(mathClub -> mathClub.getName())
                .collect(Collectors.toList()));

        clubComboBox.setItems(obeservableMathClubs);
        clubComboBox.getItems().add(0, "Svi klubovi");



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
                return new ReadOnlyStringWrapper(param.getValue().getClubMembership().getJoinDate().toString());
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


                return new ReadOnlyStringWrapper(param.getValue().calculateAverageGrade().toString());
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


        List<Student> filteredStudents = students.stream()
                .filter(student -> student.getName().contains(studentName) || student.getSurname().contains(studentName))
                .filter(student -> {

                    if (Optional.ofNullable(clubName).isEmpty() || clubName.equalsIgnoreCase("svi klubovi")) {
                        return true;
                    }

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
