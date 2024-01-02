package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class CompetitionsUserController {

    @FXML
    private TableColumn<Competition, String> competitionAddressTableColumn;

    @FXML
    private TableColumn<Competition, String> competitionDateTableColumn;

    @FXML
    private TableColumn<Competition, String> competitionDescriptionTableColumn;

    @FXML
    private TableColumn<Competition, String> competitionNameTableColumn;

    @FXML
    private TableView<Competition> competitionTableView;

    @FXML
    private TableColumn<Competition, String> competitionTimeTableColumn;

    @FXML
    private TableColumn<Competition, String> competitionWinnerTableColumn;

    @FXML
    private TextField filterCompetitions;



    public void initialize() {
        List<Competition> competitionList = DatabaseUtil.getCompetitions();

        FilteredList<Competition> filteredCompetitions = getCompetitionsFilteredList(competitionList);
        initializeCompetitionsTableView(filteredCompetitions);
    }




    private FilteredList<Competition> getCompetitionsFilteredList(List<Competition> competitions) {

        FilteredList<Competition> filteredCompetitions = new FilteredList<>(FXCollections.observableList(competitions), p -> true);

        filterCompetitions.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCompetitions.setPredicate(competition -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return competition.getName().toLowerCase().contains(lowerCaseFilter)
                        || competition.getDescription().toLowerCase().contains(lowerCaseFilter)
                        || competition.getAddress().toString().contains(lowerCaseFilter)
                        ||competition.getTimeOfCompetition().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")).contains(lowerCaseFilter)
                        ||competition.getTimeOfCompetition().format(DateTimeFormatter.ofPattern("HH:mm")).contains(lowerCaseFilter)
                        ||competition.findWinner().isPresent() && competition.findWinner().get().getName().toLowerCase().contains(lowerCaseFilter);
            });
        });
        return filteredCompetitions;
    }


    private void initializeCompetitionsTableView(ObservableList<Competition> competitions) {
        competitionNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        competitionDescriptionTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getDescription());
            }
        });

        competitionAddressTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getAddress().toString());
            }
        });

        competitionDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
                return new ReadOnlyStringWrapper(param.getValue().getTimeOfCompetition().format(formatter));
            }
        });

        competitionTimeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return new ReadOnlyStringWrapper(param.getValue().getTimeOfCompetition().format(formatter));
            }
        });

        competitionWinnerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                String winner;
                if (param.getValue().findWinner().isPresent()) {
                    winner = param.getValue().findWinner().get().getName() + " " +
                            param.getValue().findWinner().get().getSurname();
                } else {
                    winner = "Nema pobjednika";
                }
                return new ReadOnlyStringWrapper(winner);
            }
        });


        competitionTableView.setItems(competitions);
    }

}
