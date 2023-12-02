package hr.java.project.projectfxapp;

import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CompetitionsSearchController {


    @FXML
    private TextField competitionNameTextField;
    @FXML
    private DatePicker competitionDatePicker;
    @FXML
    private TableView competitionTableView;
    @FXML
    private TableColumn competitionNameTableColumn;
    @FXML
    private TableColumn competitionDescriptionTableColumn;
    @FXML
    private TableColumn competitionAddressTableColumn;
    @FXML
    private TableColumn competitionTimeTableColumn;
    @FXML
    private TableColumn competitionDateTableColumn;
    @FXML
    private TableColumn competitionWinnerTableColumn;

    private static List<Competition> competitions;

    public void initialize(){
        competitions = FileReaderUtil.getMathCompetitionsFromFile(FileReaderUtil.getStudentsFromFile(), FileReaderUtil.getAddressesFromFile());

        competitionNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });


        competitionDescriptionTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getDescription());
            }
        });

        competitionAddressTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getAddress().toString());
            }
        });


        competitionDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getTimeOfCompetition().toLocalDate().toString());
            }
        });

        competitionTimeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                Competition competition = param.getValue();
                LocalDateTime dateTime = competition.getTimeOfCompetition();

                int hours = dateTime.getHour();
                int minutes = dateTime.getMinute();
                int seconds = dateTime.getSecond();

                String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                return new ReadOnlyStringWrapper(formattedTime);
            }
        });


        competitionWinnerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().findWinner()
                        .map(student -> student.getName() + " " + student.getSurname()).orElse("Nema pobjednika"));
            }
        });

    }


    public void searchCompetitions(){
        LocalDate dateOfCompetition = competitionDatePicker.getValue();
        String competitionName = competitionNameTextField.getText();

        List<Competition> filteredCompetitions = competitions.stream()
                .filter(competition -> competition.getName().contains(competitionName))
                .filter(competition -> dateOfCompetition == null || competition.getTimeOfCompetition().toLocalDate().equals(dateOfCompetition))
                .collect(Collectors.toList());

        ObservableList<Competition> observableCompetitionsList = FXCollections.observableList(filteredCompetitions);
        competitionTableView.setItems(observableCompetitionsList);

    }

    public void resetCompetitions(){
        competitionNameTextField.setText("");
        competitionTableView.getItems().clear();
        competitionDatePicker.setValue(null);
    }
}
