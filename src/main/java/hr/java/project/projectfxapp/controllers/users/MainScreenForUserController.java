package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class MainScreenForUserController {

    @FXML
    private PieChart yearNumberDifferencePieChart;
    @FXML
    private PieChart genderNumberDifferencePieChart;
    @FXML
    private Label welcomeMessageLabel;

    @FXML
    private Label numberOfCompetitionsLabel;

    @FXML
    private Label numberOfMembersLabel;

    @FXML
    private Label numberOfProjectsLabel;

    @FXML
    private BarChart<String, BigDecimal> overallScoreComparisonBarChart;

    @FXML
    private Label todayRegisteredMembersLabel;

    public void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        MathClub currentClub = DatabaseUtil.getMathClub(currentUser.getMathClubId()).get();

        List<Competition> competitionList = DatabaseUtil.getCompetitions();
        List<MathProject> mathProjectsList = DatabaseUtil.getProjects();
        List<MathClub> mathClubList = DatabaseUtil.getMathClubs();

        welcomeMessageLabel.setText("Dobrodošli, " + currentClub.getName() + "!");

        setMathClubStatistics(currentClub, competitionList, mathProjectsList, mathClubList);


        setOverallScoreComparisonBarChart(currentClub,mathClubList, competitionList, mathProjectsList);
        setGenderNumberDifferencePieChart(currentClub);
        setYearNumberDifferencePieChart(currentClub);


    }

    private void setMathClubStatistics(MathClub currentClub, List<Competition> competitionList,
                                       List<MathProject> mathProjectsList, List<MathClub> mathClubList) {
        setNumberOfCompetitionsLabel(currentClub, competitionList);
        setNumberOfMembersLabel(currentClub);
        setNumberOfProjectsLabel(currentClub,mathProjectsList);
        setTodayRegisteredMembersLabel(currentClub);
    }

    private void setYearNumberDifferencePieChart(MathClub currentClub) {
        Set<Student> currentClubMembers = currentClub.getStudents();

        long numberOfFirstYearMembers = currentClubMembers.stream()
                .filter(student -> student.getYearOfStudy().equals(1))
                .count();

        long numberOfSecondYearMembers = currentClubMembers.stream()
                .filter(student -> student.getYearOfStudy().equals(2))
                .count();

        long numberOfThirdYearMembers = currentClubMembers.stream()
                .filter(student -> student.getYearOfStudy().equals(3))
                .count();

        PieChart.Data firstYearMembersData = new PieChart.Data("1. godina", numberOfFirstYearMembers);
        PieChart.Data secondYearMembersData = new PieChart.Data("2. godina", numberOfSecondYearMembers);
        PieChart.Data thirdYearMembersData = new PieChart.Data("3. godina", numberOfThirdYearMembers);

        yearNumberDifferencePieChart.setData(FXCollections.observableArrayList(firstYearMembersData, secondYearMembersData,
                thirdYearMembersData));

        yearNumberDifferencePieChart.getData().forEach(data -> data.setName(data.getName() +
                ":\nBroj članova: " + (int) data.getPieValue()));

        yearNumberDifferencePieChart.setTitle("Distribucija članova po godini studija");
    }


    private void setGenderNumberDifferencePieChart(MathClub currentClub) {
        Set<Student> currentClubMembers = currentClub.getStudents();

        long numberOfMaleMembers = currentClubMembers.stream()
                .filter(student -> "Male".equalsIgnoreCase(student.getGender()))
                .count();

        long numberOfFemaleMembers = currentClubMembers.stream()
                .filter(student -> "Female".equalsIgnoreCase(student.getGender()))
                .count();

        PieChart.Data maleMembersData = new PieChart.Data("Muški", numberOfMaleMembers);
        PieChart.Data femaleMembersData = new PieChart.Data("Ženski", numberOfFemaleMembers);

        genderNumberDifferencePieChart.setData(FXCollections.observableArrayList(maleMembersData, femaleMembersData));

        genderNumberDifferencePieChart.getData().forEach(data -> data.setName(data.getName() +
                ":\nBroj članova: " + (int) data.getPieValue()));

        genderNumberDifferencePieChart.setTitle("Distribucija članova po spolu");

    }

    private void setOverallScoreComparisonBarChart(MathClub currentClub, List<MathClub> mathClubList,
                                                   List<Competition> competitionList, List<MathProject> mathProjectsList) {

        overallScoreComparisonBarChart.setTitle("Usporedba bodova s drugim klubovima");

        XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();

        for (MathClub club : mathClubList){

            List<CompetitionResult> competitionResultList = competitionList.stream()
                    .flatMap(competition -> competition.getCompetitionResults().stream())
                    .filter(result -> result.participant().getClubMembership().getClubId().equals(club.getId()))
                    .toList();

            Integer numberOfColaborations = mathProjectsList.stream()
                    .filter(project -> project.hasMathClubCollaborator(club))
                    .toList()
                    .size();


            BigDecimal clubScore = club.calculateScore(competitionResultList, numberOfColaborations);

            XYChart.Data<String, BigDecimal> data = new XYChart.Data<>(club.getName(), clubScore);

            series.getData().add(data);

            if (club.equals(currentClub)) {
                data.nodeProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        newValue.setStyle("-fx-bar-fill: #374151;");
                    }
                });
            }

        }
        overallScoreComparisonBarChart.getData().add(series);
    }

    private void setTodayRegisteredMembersLabel(MathClub currentClub) {
        Set<Student> clubMembers = currentClub.getStudents();

        long numberOfTodayRegisteredMembers = clubMembers.stream()
                .filter(student -> student.getClubMembership().getJoinDate().equals(LocalDate.now()))
                .count();

        todayRegisteredMembersLabel.setText(String.valueOf(numberOfTodayRegisteredMembers));
    }


    private void setNumberOfProjectsLabel(MathClub currentClub, List<MathProject> mathProjectsList) {
        long numberOfProjects = mathProjectsList.stream()
                .filter(project -> project.hasMathClubCollaborator(currentClub))
                .count();

        numberOfProjectsLabel.setText(String.valueOf(numberOfProjects));
    }


    private void setNumberOfMembersLabel(MathClub currentClub) {
        Integer numberOfMembers = currentClub.getNumberOfMembers();
        numberOfMembersLabel.setText(numberOfMembers.toString());

    }

    private void setNumberOfCompetitionsLabel(MathClub currentClub,List<Competition> competitionList) {
        int numberOfCompetitions = 0;

        for (Competition competition : competitionList) {
            for (CompetitionResult result: competition.getCompetitionResults()){
                if (result.participant().getClubMembership().getClubId().equals(currentClub.getId())){
                    numberOfCompetitions++;
                    break;
                }
            }

        }

        numberOfCompetitionsLabel.setText(Integer.toString(numberOfCompetitions));
    }


}
