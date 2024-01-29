package hr.java.project.projectfxapp.threads;

import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.MathProject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RefreshProjectsScreenThread extends DataRefreshManager implements Runnable {

    private static RefreshProjectsScreenThread instance;
    private Thread thread;

    private volatile boolean stopRequested = false;


    private final LineChart<String, Integer> comparisonOfProjectOrganizationsBetweenClubsLineChart;
    private final BarChart<String, Integer> projectSizeComparisonChart;
    private final TableView<MathProject> projectsTableView;

    private List<MathProject> mathProjectList;

    private RefreshProjectsScreenThread(LineChart<String, Integer> comparisonOfProjectOrganizationsBetweenClubsLineChart,
                                        BarChart<String, Integer> projectSizeComparisonChart,
                                        TableView<MathProject> projectsTableView) {
        this.comparisonOfProjectOrganizationsBetweenClubsLineChart = comparisonOfProjectOrganizationsBetweenClubsLineChart;
        this.projectSizeComparisonChart = projectSizeComparisonChart;
        this.projectsTableView = projectsTableView;
        this.mathProjectList = super.getMathProjects();
    }


    public static synchronized void startThreadIfThreadNotPresent(LineChart<String, Integer> lineChart,
                                                                  BarChart<String, Integer> barChart,
                                                                  TableView<MathProject> tableView) {
        if (instance == null || !instance.isThreadAlive()) {
            instance = new RefreshProjectsScreenThread(lineChart, barChart, tableView);
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
        try {
            Thread.sleep(10000);

            while (!stopRequested) {
                final List<MathProject> currentMathProjectList = super.getMathProjects();

                Platform.runLater(() -> {
                    System.out.println("Postavljam projekte u tablicu");
                    setTableViewItems(projectsTableView, currentMathProjectList);

                    if (!areMathProjectsEqual(currentMathProjectList, mathProjectList)) {
                        System.out.println("Postavljam projekte u graf");
                        initializeProjectSizeComparisonChart(mathProjectList);
                        initializeProjectOrganizationComparisonChart(mathProjectList);
                        mathProjectList = currentMathProjectList;
                        System.out.println("Postavio sam projekte u graf");
                    }
                });

                Thread.sleep(10000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeProjectOrganizationComparisonChart(List<MathProject> currentMathProjectList) {
        comparisonOfProjectOrganizationsBetweenClubsLineChart.getData().clear();
        comparisonOfProjectOrganizationsBetweenClubsLineChart.getYAxis().setLabel("Broj projekata");

        Map<MathClub, Integer> numberOfProjectsPerClub = currentMathProjectList.stream()
                .collect(Collectors.groupingBy(MathProject::getOrganizer, Collectors.summingInt(project -> 1)));

        XYChart.Series<String, Integer> series = new XYChart.Series<>();

        for (Map.Entry<MathClub, Integer> entry : numberOfProjectsPerClub.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().getName(), entry.getValue()));
        }

        comparisonOfProjectOrganizationsBetweenClubsLineChart.getData().add(series);
    }

    private void initializeProjectSizeComparisonChart(List<MathProject> currentMathProjectList) {
        projectSizeComparisonChart.getData().clear();
        projectSizeComparisonChart.getYAxis().setTickLabelGap(1);
        projectSizeComparisonChart.getYAxis().setLabel("Broj sudionika");

        for (MathProject project : currentMathProjectList) {
            Integer numberOfParticipants = project.getTotalNumberOfCollaborators();

            if (numberOfParticipants > 0) {
                XYChart.Series<String, Integer> series = new XYChart.Series<>();
                series.setName(project.getName());
                series.getData().add(new XYChart.Data<>("Projekti", numberOfParticipants));
                projectSizeComparisonChart.getData().add(series);
            }
        }
    }

    private boolean areMathProjectsEqual(List<MathProject> mathProjectList, List<MathProject> currentMathProjectList) {
        if (mathProjectList.size() != currentMathProjectList.size()) {
            return false;
        }

        for (int i = 0; i < mathProjectList.size(); i++) {
            Integer currentMathProjectSize = currentMathProjectList.get(i).getTotalNumberOfCollaborators();
            Integer mathProjectSize = mathProjectList.get(i).getTotalNumberOfCollaborators();
            if (!currentMathProjectSize.equals(mathProjectSize)) {
                return false;
            }
        }
        return true;
    }

    private void setTableViewItems(TableView<MathProject> projectsTableView, List<MathProject> currentMathProjectList) {
        projectsTableView.setItems(FXCollections.observableArrayList(currentMathProjectList));
    }

    private boolean isThreadAlive() {
        return thread != null && thread.isAlive();
    }

}

