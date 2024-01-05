package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.CompetitionResult;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class CompetitionsUserController {

    @FXML
    private Label currentClubNameTextField;
    @FXML
    private BarChart<String, Integer> numberOfParticipantsInCompetitionBarChart;
    @FXML
    private LineChart<String, BigDecimal> averageCompetitionScoreLineChart;
    @FXML
    private TableColumn<Competition, String> competitionOrganizerTableColumn;

    @FXML
    private TableColumn<Competition, String> competitionStatusTableColumn;
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
        currentClubNameTextField.setText(SessionManager.getInstance().getCurrentClub().getName());
        List<Competition> competitionList = DatabaseUtil.getCompetitions();

        FilteredList<Competition> filteredCompetitions = getCompetitionsFilteredList(competitionList);
        initializeCompetitionsTableView(filteredCompetitions);

        initializeBarChart(competitionList);
        initializeLineChart(competitionList);

    }

    private void initializeLineChart(List<Competition> competitionList) {
        averageCompetitionScoreLineChart.getData().clear();

        averageCompetitionScoreLineChart.getYAxis().setLabel("Prosječni rezultat");
        averageCompetitionScoreLineChart.getYAxis().setTickLabelGap(1);


        XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
        series.setName("Prosječni rezultat");

        for (Competition competition : competitionList) {
            BigDecimal averageScore = competition.getAverageScoreForCompetition();
            if (averageScore.compareTo(BigDecimal.ZERO) > 0) {
                series.getData().add(new XYChart.Data<>(competition.getName(), averageScore));
            }
        }

        averageCompetitionScoreLineChart.getData().add(series);
    }

    private void initializeBarChart(List<Competition> competitionList) {
        numberOfParticipantsInCompetitionBarChart.getData().clear();

        numberOfParticipantsInCompetitionBarChart.getYAxis().setTickLabelGap(1);
        numberOfParticipantsInCompetitionBarChart.getYAxis().setLabel("Broj natjecatelja");


        for (Competition competition : competitionList) {

            int numberOfParticipants = competition.getNumberOfParticipants();
            if (numberOfParticipants > 0) {
                XYChart.Series<String, Integer> series = new XYChart.Series<>();
                series.setName(competition.getName());
                series.getData().add(new XYChart.Data<>("Natjecanja", numberOfParticipants));
                numberOfParticipantsInCompetitionBarChart.getData().add(series);
            }
        }



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
                        ||competition.findWinner().isPresent() && competition.findWinner().get().getName().toLowerCase().contains(lowerCaseFilter)
                        ||competition.getStatus().getStatusDescription().toLowerCase().contains(lowerCaseFilter)
                        ||competition.getOrganizer().getName().toLowerCase().contains(lowerCaseFilter);
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
                String winnerString;
                if (param.getValue().getStatus().getStatusDescription().equals("Planirano")) {
                    winnerString = "Nema pobjednika";
                } else {
                    Optional<Student> winner = param.getValue().findWinner();
                    winnerString = winner.map(student -> student.getName() + " " + student.getSurname()).orElse("Nema pobjednika");
                }

                return new ReadOnlyStringWrapper(winnerString);
            }
        });

        competitionOrganizerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getOrganizer().getName());
            }
        });

        competitionStatusTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getStatus().getStatusDescription());
            }
        });


        competitionTableView.setRowFactory(tv -> {
            TableRow<Competition> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Competition selectedCompetition = row.getItem();
                    handleCompetitionDoubleClick(selectedCompetition);
                }
            });
            return row;
        });


        competitionTableView.setItems(competitions);
    }

    private void handleCompetitionDoubleClick(Competition selectedCompetition) {
        SessionManager.getInstance().setCurrentCompetition(selectedCompetition);
        JavaFxProjectApplication.showPopup(ApplicationScreen.CompetitionDetailsCard);
    }

    public void addNewCompetitionUser(ActionEvent actionEvent) {
        JavaFxProjectApplication.showPopup(ApplicationScreen.AddNewCompetitionUser);

    }

    public void addMembersToCompetitionUser(ActionEvent actionEvent) {
        if(competitionTableView.getSelectionModel().getSelectedItem() != null) {

            Competition selectedCompetition = competitionTableView.getSelectionModel().getSelectedItem();
            SessionManager.getInstance().setCurrentCompetition(selectedCompetition);

            if (Optional.ofNullable(selectedCompetition).isPresent()) {
                if (selectedCompetition.getStatus().getStatusDescription().equals("Planirano")) {
                    JavaFxProjectApplication.showPopup(ApplicationScreen.RegisterMembersIntoCompetition);
                }
            }
        }

    }

    public void updateCompetitionUser(ActionEvent actionEvent) {

        if(competitionTableView.getSelectionModel().getSelectedItem() != null) {
            Competition selectedCompetition = competitionTableView.getSelectionModel().getSelectedItem();
            SessionManager.getInstance().setCurrentCompetition(selectedCompetition);

            if (selectedCompetition != null || selectedCompetition.getStatus().getStatusDescription().equals("Planirano")) {
                JavaFxProjectApplication.showPopup(ApplicationScreen.UpdateCompetitionUser);
            }
        }

    }
}
