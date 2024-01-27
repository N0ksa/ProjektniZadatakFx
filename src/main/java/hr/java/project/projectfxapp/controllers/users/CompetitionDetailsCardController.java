package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.CompetitionResult;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.util.List;

public class CompetitionDetailsCardController {

    @FXML
    private TableView<CompetitionResult> competitionResultsTableView;

    @FXML
    private PieChart genderDistributionPieChart;

    @FXML
    private LineChart<String, BigDecimal> genderScoreDifferenceLineChart;

    @FXML
    private TableColumn<CompetitionResult, String> participantTableColumn;

    @FXML
    private TableColumn<CompetitionResult, String> scoreTableColumn;



    public void initialize(){

        Competition currentCompetition = SessionManager.getInstance().getCurrentCompetition();

        initializeTableView(currentCompetition);
        setGenderDistributionPieChart(currentCompetition);
        setGenderScoreDifferenceLineChart(currentCompetition);


    }

    private void setGenderScoreDifferenceLineChart(Competition currentCompetition) {

        genderScoreDifferenceLineChart.getYAxis().setLabel("Prosječni rezultat");

        XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Muški", currentCompetition.getAverageMaleScoreForCompetition()));
        series.getData().add(new XYChart.Data<>("Ženski", currentCompetition.getAverageFemaleScoreForCompetition()));

        genderScoreDifferenceLineChart.getData().add(series);

    }

    private void setGenderDistributionPieChart(Competition currentCompetition) {
        List<CompetitionResult> competitionResults = currentCompetition.getCompetitionResults().stream().toList();
        int numberOfMaleParticipants = 0;
        int numberOfFemaleParticipants = 0;

        for (CompetitionResult competitionResult : competitionResults) {
            if (competitionResult.participant().getGender().equals("Male")) {
                numberOfMaleParticipants++;
            } else {
                numberOfFemaleParticipants++;
            }
        }
        genderDistributionPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Muški", numberOfMaleParticipants),
                new PieChart.Data("Ženski", numberOfFemaleParticipants)
        ));

        genderDistributionPieChart.getData().forEach(data -> data.setName(data.getName() + "-" + (int) data.getPieValue()));

    }


    private void initializeTableView(Competition currentCompetition) {
        participantTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CompetitionResult, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CompetitionResult, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().participant().toString());
            }
        });

        scoreTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CompetitionResult, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CompetitionResult, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().score().toString());
            }
        });

        List<CompetitionResult> competitionResults = currentCompetition.getCompetitionResults().stream().toList();

        competitionResultsTableView.setItems(FXCollections.observableArrayList(competitionResults));

    }


    public void generateHtmlForPrinting(ActionEvent actionEvent) {
        JavaFxProjectApplication.showPopup(ApplicationScreen.GenerateHtmlForPrintingCompetition);

    }
}
