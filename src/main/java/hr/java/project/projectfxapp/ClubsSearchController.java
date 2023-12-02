package hr.java.project.projectfxapp;

import hr.java.project.projectfxapp.entities.Address;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClubsSearchController {
    @FXML
    private TextField clubNameTextField;
    @FXML
    private TableView clubsTableView;
    @FXML
    private TableColumn clubNameTableColumn;
    @FXML
    private TableColumn clubAddressTableColumn;
    @FXML
    private TableColumn clubMembersTableColumn;


    private static List<MathClub> mathClubs;


    public void initialize(){
        mathClubs = FileReaderUtil.getMathClubsFromFile(FileReaderUtil.getStudentsFromFile(), FileReaderUtil.getAddressesFromFile());


        clubNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathClub,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathClub, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });


        clubAddressTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathClub,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathClub, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getAddress().toString());
            }
        });

        clubMembersTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathClub,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathClub, String> param) {
                MathClub mathClub = param.getValue();
                List<Student> students = new ArrayList<>(mathClub.getStudents());

                String studentNames = students.stream()
                        .map(student -> student.getName() + " " + student.getSurname())
                        .collect(Collectors.joining(", "));


                return new ReadOnlyStringWrapper(studentNames);
            }
        });


    }

    public void clubSearch(ActionEvent actionEvent) {
        String clubName = clubNameTextField.getText();
        List<MathClub> filteredMathClubs = mathClubs.stream()
                .filter(mathClub -> mathClub.getName()
                        .contains(clubName))
                .collect(Collectors.toList());

        ObservableList<MathClub> observableMathClubList = FXCollections.observableList(filteredMathClubs);
        clubsTableView.setItems(observableMathClubList);
    }
}
