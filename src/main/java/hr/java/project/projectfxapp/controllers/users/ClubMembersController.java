package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClubMembersController {

    @FXML
    private LineChart<String, BigDecimal> clubMemberScoreOverDifferentCompetitionsLineChart;
    @FXML
    private TableColumn<Student, String> nameAndSurnameTableColumn;

    @FXML
    private TextField filterMembers;

    @FXML
    private TableView<Student> leaderBoardTableView;

    @FXML
    private TableColumn<Student, String> memberAverageGradeTableColumn;

    @FXML
    private TableColumn<Student, String> memberEmailTableColumn;

    @FXML
    private TableColumn<Student, String> memberJoinDateTableColumn;

    @FXML
    private TableColumn<Student, String> memberNameTableColumn;

    @FXML
    private TableColumn<Student, String> memberSurnameTableColumn;

    @FXML
    private TableView<Student> memberTableView;

    @FXML
    private TableColumn<Student, String> memberYearOfStudyTableColumn;

    @FXML
    private TableColumn<Student, BigDecimal> overallScoreTableColumn;


   public void initialize(){
       MathClub currentClub = SessionManager.getInstance().getCurrentClub();


       List<Student> clubMembers = currentClub.getStudents().stream().toList();

       List<Competition> finishedCompetitions = DatabaseUtil.getCompetitions();
       LocalDateTime now = LocalDateTime.now();
       finishedCompetitions.removeIf(competition -> competition.getTimeOfCompetition().isAfter(now));

       List<MathProject> mathProjects = DatabaseUtil.getProjects();



       List<CompetitionResult> competitionResults = finishedCompetitions.stream()
               .flatMap(competition -> competition.getCompetitionResults().stream())
               .toList();


       FilteredList<Student> filteredMembers = getMembersFilteredList(clubMembers);

       initializeMemberTableView(filteredMembers);

       initializeLeaderBoardTableView(FXCollections.observableList(clubMembers), competitionResults, mathProjects);

       setClubMemberScoreOverDifferentCompetitionsLineChart(clubMembers, finishedCompetitions);


   }

    private void setClubMemberScoreOverDifferentCompetitionsLineChart(List<Student> clubMembers,
                                                                      List<Competition> competitionsList) {

        clubMemberScoreOverDifferentCompetitionsLineChart.setTitle(
                "Kretanje rezultata članova kluba kroz različita natjecanja");

        for (Student clubMember : clubMembers) {
            XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
            series.setName(clubMember.getName() + " " + clubMember.getSurname());

            for (Competition competition : competitionsList) {
                Optional<CompetitionResult> resultOptional = competition.getCompetitionResultForParticipant(clubMember);

                resultOptional.ifPresent(result -> {
                    BigDecimal competitionScore = result.score();
                    series.getData().add(new XYChart.Data<>(competition.getName(), competitionScore));
                });
            }

            clubMemberScoreOverDifferentCompetitionsLineChart.getData().add(series);
        }
    }


    private FilteredList<Student> getMembersFilteredList(List<Student> clubMembers) {

        FilteredList<Student> filteredMembers = new FilteredList<>(FXCollections.observableList(clubMembers), p -> true);

        filterMembers.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMembers.setPredicate(student -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                return student.getName().toLowerCase().contains(lowerCaseFilter)
                        || student.getSurname().toLowerCase().contains(lowerCaseFilter)
                        || student.getEmail().toLowerCase().contains(lowerCaseFilter)
                        ||student.getClubMembership().getJoinDate().format(DateTimeFormatter.
                        ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex())).toLowerCase().contains(lowerCaseFilter);
            });
        });

        return filteredMembers;
    }

    private void initializeLeaderBoardTableView(ObservableList<Student> students,
                                                List<CompetitionResult> competitionResults,
                                                List<MathProject> mathProjects) {

        nameAndSurnameTableColumn.setCellValueFactory(param ->
                new ReadOnlyStringWrapper(param.getValue().getName() + " " + param.getValue().getSurname()));

        overallScoreTableColumn.setCellValueFactory(param -> {
            List<CompetitionResult> studentCompetitionResults = competitionResults.stream()
                    .filter(result -> result.participant().equals(param.getValue()))
                    .collect(Collectors.toList());

            Integer numberOfCollaborations = mathProjects.stream()
                    .filter(project -> project.hasStudentCollaborator(param.getValue()))
                    .toList().size();

            BigDecimal overallScore = param.getValue().calculateScore(studentCompetitionResults, numberOfCollaborations);
            overallScore = overallScore.setScale(2, RoundingMode.HALF_UP);

            return new ReadOnlyObjectWrapper<>(overallScore);
        });

        List<Student> sortedStudents = students.sorted((s1, s2) -> {
            BigDecimal score1 = overallScoreTableColumn.getCellObservableValue(s1).getValue();
            BigDecimal score2 = overallScoreTableColumn.getCellObservableValue(s2).getValue();

            return score2.compareTo(score1);
        });


        leaderBoardTableView.setItems(FXCollections.observableList(sortedStudents));
    }




        private void initializeMemberTableView(ObservableList<Student> clubMembers) {
        memberNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        memberSurnameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getSurname());
            }
        });

        memberJoinDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                LocalDate joinDate = param.getValue().getClubMembership().getJoinDate();

                if (Optional.ofNullable(joinDate).isPresent()){

                    return new ReadOnlyStringWrapper(param.getValue().getClubMembership().getJoinDate()
                            .format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex())));
                }else{
                    return new ReadOnlyStringWrapper("/");
                }

            }
        });

        memberAverageGradeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(String.format("%.1f", param.getValue().calculateAverageGrade()));
            }
        });


        memberEmailTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getEmail());
            }
        });

        memberYearOfStudyTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getYearOfStudy().toString());
            }
        });

        memberTableView.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Student selectedStudent = row.getItem();
                    handleMemberDoubleClick(selectedStudent);
                }
            });
            return row;
        });

        memberTableView.setItems(clubMembers);
    }


    private void handleMemberDoubleClick(Student selectedStudent) {
        SessionManager.getInstance().setCurrentStudent(selectedStudent);
        JavaFxProjectApplication.showPopup(ApplicationScreen.MemberCard);
    }


    public void showAddNewStudentUserScreen(ActionEvent actionEvent) {
        JavaFxProjectApplication.showPopup(ApplicationScreen.AddNewStudentUser);
    }

    public void showUpdateMemberInformationScreen(ActionEvent actionEvent) {
       if(Optional.ofNullable(memberTableView.getSelectionModel().getSelectedItem()).isPresent()){
            SessionManager.getInstance().setCurrentStudent(memberTableView.getSelectionModel().getSelectedItem());
            JavaFxProjectApplication.showPopup(ApplicationScreen.UpdateMemberInformation);
        }

    }
}
