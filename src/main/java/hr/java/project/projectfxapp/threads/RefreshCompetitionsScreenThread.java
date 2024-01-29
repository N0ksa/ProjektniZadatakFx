package hr.java.project.projectfxapp.threads;

import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.MathProject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RefreshCompetitionsScreenThread extends DataRefreshManager implements Runnable{

        private final BarChart<String, Integer> numberOfParticipantsInCompetitionBarChart;
        private final LineChart<String, BigDecimal> averageCompetitionScoreLineChart;
        private  TableView<Competition> competitionTableView;

        private Thread thread;

        private static RefreshCompetitionsScreenThread instance;

        private volatile boolean stopRequested = false;

        public RefreshCompetitionsScreenThread(BarChart<String, Integer> numberOfParticipantsInCompetitionBarChart,
                                               LineChart<String, BigDecimal> averageCompetitionScoreLineChart,
                                               TableView<Competition> competitionTableView){

            this.numberOfParticipantsInCompetitionBarChart = numberOfParticipantsInCompetitionBarChart;
            this.averageCompetitionScoreLineChart = averageCompetitionScoreLineChart;
            this.competitionTableView = competitionTableView;
        }


    public static synchronized void startThreadIfThreadNotPresent(BarChart<String, Integer> barChart,
                                                                  LineChart<String, BigDecimal> lineChart,
                                                                  TableView<Competition> tableView) {
        if (instance == null || !instance.isThreadAlive()) {
            instance = new RefreshCompetitionsScreenThread(barChart, lineChart, tableView);
            instance.startThread();
        }
    }

    public static synchronized void stopThread() {
        if (instance != null) {
            instance.stopRequested = true;
        }
    }


    private void startThread() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {

        final List<Competition>[] previousCompetitions = new List[]{super.getCompetitions().stream()
                .filter(competition -> competition.getTimeOfCompetition().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList())};

        while (!stopRequested) {
            try {
                Thread.sleep(10000);

                final List<Competition> currentCompetitions = super.getCompetitions();
                final List<Competition> competitionsBeforeNow = currentCompetitions.stream()
                        .filter(competition -> competition.getTimeOfCompetition().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    System.out.println("Postavljam natjecanja u tablicu");
                    setTableViewItems(competitionTableView, currentCompetitions);

                    if (!areCompetitionsEqualForCompetitionScoreLineChart(previousCompetitions[0], competitionsBeforeNow)) {

                        System.out.println("Postavljam natjecanja u graf");
                        initializeAverageCompetitionScoreLineChart(competitionsBeforeNow);

                        previousCompetitions[0] = competitionsBeforeNow;

                    }

                    if (!areCompetitionsEqualForNumberOfParticipantsInCompetitionBarChart(previousCompetitions[0], competitionsBeforeNow)) {
                        System.out.println("Postavljam natjecanja u graf 2");
                        initializeNumberOfParticipantsInCompetitionBarChart(competitionsBeforeNow);
                        previousCompetitions[0] = competitionsBeforeNow;
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




    private boolean areCompetitionsEqualForNumberOfParticipantsInCompetitionBarChart(List<Competition> previousCompetition,
                                                                                     List<Competition> competitionsBeforeNow) {
        if (previousCompetition.size() != competitionsBeforeNow.size()) {
            return false;
        }

        for (int i = 0; i < previousCompetition.size(); i++) {
            if (!previousCompetition.get(i).getNumberOfParticipants().
                    equals(competitionsBeforeNow.get(i).getNumberOfParticipants())) {
                return false;
            }
        }

        return true;
    }


    private boolean areCompetitionsEqualForCompetitionScoreLineChart(List<Competition> competitionListOne, List<Competition> competitionListTwo) {
        if (competitionListOne.size() != competitionListTwo.size()) {
            return false;
        }

        for (int i = 0; i < competitionListOne.size(); i++) {
            if (!competitionListOne.get(i).equals(competitionListTwo.get(i))) {
                return false;
            }
        }

        return true;
    }



    private void initializeAverageCompetitionScoreLineChart(List<Competition> competitionsBeforeNow) {
        averageCompetitionScoreLineChart.getData().clear();

        averageCompetitionScoreLineChart.getYAxis().setLabel("Prosječni rezultat");
        averageCompetitionScoreLineChart.getYAxis().setTickLabelGap(1);


        XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
        series.setName("Prosječni rezultat");

        for (Competition competition : competitionsBeforeNow) {
            BigDecimal averageScore = competition.getAverageScoreForCompetition();
            if (averageScore.compareTo(BigDecimal.ZERO) > 0) {
                series.getData().add(new XYChart.Data<>(competition.getName(), averageScore));
            }
        }

        averageCompetitionScoreLineChart.getData().add(series);
    }


    private void initializeNumberOfParticipantsInCompetitionBarChart(List<Competition> competitionsBeforeNow) {
        numberOfParticipantsInCompetitionBarChart.getData().clear();

        numberOfParticipantsInCompetitionBarChart.getYAxis().setTickLabelGap(1);
        numberOfParticipantsInCompetitionBarChart.getYAxis().setLabel("Broj natjecatelja");


        for (Competition competition : competitionsBeforeNow) {
            int numberOfParticipants = competition.getNumberOfParticipants();
            if (numberOfParticipants > 0) {
                XYChart.Series<String, Integer> series = new XYChart.Series<>();
                series.setName(competition.getName());
                series.getData().add(new XYChart.Data<>("Natjecanja", numberOfParticipants));
                numberOfParticipantsInCompetitionBarChart.getData().add(series);
            }
        }

    }


    private void setTableViewItems(TableView<Competition> competitionTableView, List<Competition> competitions) {
        competitionTableView.setItems(FXCollections.observableArrayList(competitions));

    }

    private boolean isThreadAlive() {
        return thread != null && thread.isAlive();
    }
}
