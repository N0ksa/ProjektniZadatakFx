package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.entities.LoginStatistics;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.threads.GetMostRecentChangeThread;
import hr.java.project.projectfxapp.utility.files.SerializationUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainScreenForAdminController {



    @FXML
    private LineChart<String, Integer> numberOfSignInOverTimeLineChart;
    @FXML
    private TableView<Map.Entry<String, Duration>> timeLeaderboardTableView;
    @FXML
    private TableColumn<Map.Entry<String, Duration>, String> userTableColumn;
    @FXML
    private TableColumn<Map.Entry<String, Duration>, String> overallTimeTableColumn;
    @FXML
    private Label clockLabel;

    @FXML
    private TableView<Change> mostRecentChangeTableView;

    @FXML
    private TableColumn<Change, String> dateOfChangeTableColumn;

    @FXML
    private TableColumn<Change, String> newValueTableColumn;

    @FXML
    private TableColumn<Change, String> oldValueTableColumn;

    @FXML
    private TableColumn<Change, String> typeOfValueTableColumn;

    @FXML
    private TableColumn<Change, String> userRoleTableColumn;


    @FXML
    private Label dateOfMostSignIn;

    @FXML
    private Label overallSignInNumber;

    @FXML
    private Label todaySignInNumber;

    @FXML
    private Label userWithMostSignIn;


    public void initialize() {
        ClockThread clockThread = ClockThread.getInstance();
        clockThread.setLabelToUpdate(clockLabel);
        clockThread.startThread();

        setMostRecentChangeTableView();
        GetMostRecentChangeThread getMostRecentChangeThread = GetMostRecentChangeThread.getInstance();
        getMostRecentChangeThread.setTableViewToUpdate(mostRecentChangeTableView);

        boolean newThreadStarted = getMostRecentChangeThread.startThread();
        if (!newThreadStarted){
            getMostRecentChangeThread.executeTaskManually();
        }

        List<LoginStatistics> loginStatisticsList = SerializationUtil.deserializeLoginStatisticsList();

        setLoginStatisticsCards(loginStatisticsList);
        setNumberOfSignInOverTimeLineChart(loginStatisticsList);


        Map<String, Duration> totalDurationForEachUser = loginStatisticsList.stream()
                .collect(Collectors.groupingBy(LoginStatistics::username,
                        Collectors.summingInt(LoginStatistics::loginDuration)))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Duration.ofSeconds(entry.getValue())));

        setTimeLeaderboardTableView(totalDurationForEachUser);


    }

    private void setTimeLeaderboardTableView(Map<String, Duration> totalDurationMap) {
        userTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getKey()));

        overallTimeTableColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(formatDuration(cellData.getValue().getValue())));

        List<Map.Entry<String, Duration>> sortedTotalDurationMap = totalDurationMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        timeLeaderboardTableView.setItems(FXCollections.observableArrayList(sortedTotalDurationMap));
    }




    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format("%d dana - %02d:%02d:%02d", days, hours, minutes, seconds);
    }

    private void setNumberOfSignInOverTimeLineChart(List<LoginStatistics> loginStatisticsList) {
        Map<LocalDate, Long> signInCountsByDate = loginStatisticsList.stream()
                .collect(Collectors.groupingBy(login -> login.loginTime().toLocalDate(), Collectors.counting()));

        Map<LocalDate, Long> sortedSignInCountsByDate = new TreeMap<>(signInCountsByDate);

       numberOfSignInOverTimeLineChart.getData().clear();

       numberOfSignInOverTimeLineChart.getYAxis().setLabel("Broj prijava");
       numberOfSignInOverTimeLineChart.getXAxis().setLabel("Datum prijave");

          XYChart.Series<String, Integer> series = new XYChart.Series<>();
          series.setName("Broj prijava");

          for (Map.Entry<LocalDate, Long> entry : sortedSignInCountsByDate.entrySet()) {
                LocalDate date = entry.getKey();
                Long signInCount = entry.getValue();

                series.getData().add(new XYChart.Data<>(date.format(DateTimeFormatter.ofPattern
                        (ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex())), signInCount.intValue()));
          }

          numberOfSignInOverTimeLineChart.getData().add(series);
    }





    private void setLoginStatisticsCards(List<LoginStatistics> loginStatisticsList) {
        setTodaySignInNumber(loginStatisticsList);
        setOverallSignInNumber(loginStatisticsList);
        setUserWithMostSignIn(loginStatisticsList);
        setDateOfMostSignIn(loginStatisticsList);

    }

    private void setDateOfMostSignIn(List<LoginStatistics> loginStatisticsList) {

        Map<LocalDate, Long> signInCountsByDate = loginStatisticsList.stream()
                .collect(Collectors.groupingBy(login -> login.loginTime().toLocalDate(), Collectors.counting()));

        Optional<Map.Entry<LocalDate, Long>> maxEntry = signInCountsByDate.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        maxEntry.map(Map.Entry::getKey).ifPresent(dateOfMostSignIn ->
                this.dateOfMostSignIn.setText(dateOfMostSignIn.format(DateTimeFormatter.ofPattern
                        (ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex()))));

    }


    private void setUserWithMostSignIn(List<LoginStatistics> loginStatisticsList) {
        Map<String, Long> signInCountsByUser = loginStatisticsList.stream()
                .collect(Collectors.groupingBy(LoginStatistics::username, Collectors.counting()));

        Optional<Map.Entry<String, Long>> maxEntry = signInCountsByUser.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        String userWithMostSignIn = maxEntry.map(Map.Entry::getKey).orElse("");

        this.userWithMostSignIn.setText(userWithMostSignIn);
    }


    private void setOverallSignInNumber(List<LoginStatistics> loginStatisticsList) {
        overallSignInNumber.setText(String.valueOf(loginStatisticsList.size()));
    }


    private void setTodaySignInNumber(List<LoginStatistics> loginStatisticsList) {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        int todaySignInCount = 0;

        for (LoginStatistics loginStatistics : loginStatisticsList) {
            LocalDateTime loginDateTime = loginStatistics.loginTime();

            if (loginDateTime.isAfter(today.minusSeconds(1)) && loginDateTime.isBefore(today.plusDays(1))) {
                todaySignInCount += 1;
            }
        }

        todaySignInNumber.setText(String.valueOf(todaySignInCount));
    }


    private void setMostRecentChangeTableView() {

        dateOfChangeTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().timestamp().toString()));
        newValueTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().newValue()));
        oldValueTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().oldValue()));
        typeOfValueTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().dataType()));
        userRoleTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().userRole()));

        setWrappingCellFactory(dateOfChangeTableColumn);
        setWrappingCellFactory(newValueTableColumn);
        setWrappingCellFactory(oldValueTableColumn);
        setWrappingCellFactory(typeOfValueTableColumn);
        setWrappingCellFactory(userRoleTableColumn);
    }



    private void setWrappingCellFactory(TableColumn<Change, String> column) {
        column.setCellFactory(tc -> {
            TableCell<Change, String> cell = new TableCell<>() {
                private final Text text = new Text();

                {
                    text.wrappingWidthProperty().bind(column.widthProperty());
                    setGraphic(text);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        text.setText(null);
                    } else {
                        text.setText(item);
                    }
                }
            };

            return cell;

        });
    }

    public void generateHtmlForPrinting(ActionEvent actionEvent) {
        JavaFxProjectApplication.showPopup(ApplicationScreen.GenerateHtmlForPrintingUserStatistics);
    }
}
