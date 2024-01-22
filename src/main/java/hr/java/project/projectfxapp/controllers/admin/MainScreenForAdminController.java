package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.entities.LoginStatistics;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.threads.GetMostRecentChangeThread;
import hr.java.project.projectfxapp.utility.SerializationUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainScreenForAdminController {


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

        LocalDate dateOfMostSignIn = maxEntry.map(Map.Entry::getKey).orElse(null);

        if (dateOfMostSignIn != null) {
            this.dateOfMostSignIn.setText(dateOfMostSignIn.toString());
        }
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
}
