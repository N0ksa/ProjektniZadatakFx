package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.entities.User;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.Status;
import hr.java.project.projectfxapp.threads.RefreshCompetitionsScreenThread;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.application.Platform;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

        List<Competition> competitionsBeforeNow = competitionList.stream()
                .filter(competition -> competition.getTimeOfCompetition().isBefore(LocalDateTime.now())).
                collect(Collectors.toList());

        initializeCompetitionsTableView(filteredCompetitions);
        initializeBarChart(competitionsBeforeNow);
        initializeLineChart(competitionsBeforeNow);


        RefreshCompetitionsScreenThread.startThreadIfThreadNotPresent(numberOfParticipantsInCompetitionBarChart,
                averageCompetitionScoreLineChart, competitionTableView);


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
                        ||competition.getTimeOfCompetition().format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))
                        .contains(lowerCaseFilter)
                        ||competition.getTimeOfCompetition().format(DateTimeFormatter.ofPattern("HH:mm"))
                        .contains(lowerCaseFilter)
                        ||competition.findWinner().isPresent() && competition.findWinner().get().getName().toLowerCase()
                        .contains(lowerCaseFilter)
                        ||competition.getOrganizer().getName().toLowerCase().contains(lowerCaseFilter);
            });
        });
        return filteredCompetitions;
    }


    private void initializeCompetitionsTableView(ObservableList<Competition> competitions) {
        competitionNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        competitionDescriptionTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getDescription());
            }
        });

        competitionAddressTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getAddress().toString());
            }
        });

        competitionDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
                return new ReadOnlyStringWrapper(param.getValue().getTimeOfCompetition().format(formatter));
            }
        });

        competitionTimeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return new ReadOnlyStringWrapper(param.getValue().getTimeOfCompetition().format(formatter));
            }
        });

        competitionWinnerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                String winnerString;
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime competitionTime = param.getValue().getTimeOfCompetition();

                if (competitionTime.isAfter(now)){
                    winnerString = "Natjecanje nije počelo";
                } else {
                    Optional<Student> winner = param.getValue().findWinner();
                    winnerString = winner.map(student -> student.getName() + " " + student.getSurname())
                            .orElse("Nema pobjednika");
                }

                return new ReadOnlyStringWrapper(winnerString);
            }
        });

        competitionOrganizerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getOrganizer().getName());
            }
        });

        competitionStatusTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime competitionTime = param.getValue().getTimeOfCompetition();
                String status = "";

                if (competitionTime.isBefore(now)){
                     status = Status.FINISHED.getStatusDescription();
                }
                else if (competitionTime.isAfter(now)){
                     status = Status.PLANNED.getStatusDescription();
                }
                else{
                     status = Status.ONGOING.getStatusDescription();
                }

                return new ReadOnlyStringWrapper(status);
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
                LocalDateTime competitionTime = selectedCompetition.getTimeOfCompetition();
                LocalDateTime now = LocalDateTime.now();

                if (now.plusDays(3).isBefore(competitionTime)) {
                    SessionManager.getInstance().setCurrentCompetition(selectedCompetition);
                    JavaFxProjectApplication.showPopup(ApplicationScreen.RegisterMembersIntoCompetition);

                } else {
                    if(competitionTime.isBefore(now)){
                        ValidationProtocol.showErrorAlert("Pogreška",
                                "Pogreška prilikom registracije članova u natjecanju",
                                "Natjecanje je već završilo");
                    }
                    else{
                        ValidationProtocol.showErrorAlert("Pogreška",
                                "Pogreška prilikom registracije članova u natjecanju",
                                "Natjecanje počinje za manje od 3 dana\nRegistracije i odjave nisu moguće");
                    }
                }
            }
        }

    }

    public void updateCompetitionUser(ActionEvent actionEvent) {

        if(competitionTableView.getSelectionModel().getSelectedItem() != null) {
            Competition selectedCompetition = competitionTableView.getSelectionModel().getSelectedItem();
            SessionManager.getInstance().setCurrentCompetition(selectedCompetition);
            User currentUser = SessionManager.getInstance().getCurrentUser();

            if(currentUser.getMathClubId().equals(selectedCompetition.getOrganizer().getId())){
                JavaFxProjectApplication.showPopup(ApplicationScreen.UpdateCompetitionUser);
            }
            else{
                ValidationProtocol.showErrorAlert("Pogreška",
                        "Pogreška prilikom ažuriranja natjecanja",
                        "Samo organizator natjecanja može ažurirati natjecanje");
            }

        }

    }


}
