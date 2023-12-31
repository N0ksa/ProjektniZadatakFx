package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class MainScreenForUserController {


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

        setNumberOfCompetitionsLabel(currentClub, competitionList);
        setNumberOfMembersLabel(currentClub);
        setNumberOfProjectsLabel(currentClub,mathProjectsList);
        setTodayRegisteredMembersLabel(currentClub);


        setOverallScoreComparisonBarChart(currentClub,mathClubList, competitionList, mathProjectsList);





    }

    private void setOverallScoreComparisonBarChart(MathClub currentClub, List<MathClub> mathClubList,
                                                   List<Competition> competitionList, List<MathProject> mathProjectsList) {
        //
    }

    private void setTodayRegisteredMembersLabel(MathClub currentClub) {
        Set<Student> clubMembers = currentClub.getStudents();
        Integer numberOfTodayRegisteredMembers = 0;
        for (Student student : clubMembers) {
            if (student.getClubMembership().getJoinDate().equals(LocalDate.now())){
                numberOfTodayRegisteredMembers++;
            }
        }

        todayRegisteredMembersLabel.setText(numberOfTodayRegisteredMembers.toString());

    }

    private void setNumberOfProjectsLabel(MathClub currentClub, List<MathProject> mathProjectsList) {
        Integer numberOfProjects = 0;
        for (MathProject project : mathProjectsList) {
            if (project.hasMathClubCollaborator(currentClub)){
                numberOfProjects++;
            }
        }

        numberOfProjectsLabel.setText(numberOfProjects.toString());
    }

    private void setNumberOfMembersLabel(MathClub currentClub) {

        Integer numberOfMembers = currentClub.getNumberOfMembers();
        numberOfMembersLabel.setText(numberOfMembers.toString());

    }

    private void setNumberOfCompetitionsLabel(MathClub currentClub,List<Competition> competitionList) {
        Integer numberOfCompetitions = 0;
        for (Competition competition : competitionList) {
            for (CompetitionResult result: competition.getCompetitionResults()){
                if (result.participant().getClubMembership().getClubId().equals(currentClub.getId())){
                    numberOfCompetitions++;
                    break;
                }
            }

        }

        numberOfCompetitionsLabel.setText(numberOfCompetitions.toString());
    }

}
