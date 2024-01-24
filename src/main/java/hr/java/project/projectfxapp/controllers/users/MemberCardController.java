package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.MathProject;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.entities.SubjectGrade;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MemberCardController {

    @FXML
    private Label daysAsMemberLabel;
    @FXML
    private Label additionalInfoForMemberLabel;

    @FXML
    private Label competitionParticipationLabel;

    @FXML
    private Label competitionWinNumberLabel;

    @FXML
    private Label highestScoreInCompetitionLabel;

    @FXML
    private Label lowestScoreInCompetitionLabel;

    @FXML
    private ImageView memberImage;

    @FXML
    private Label projectParticipationLabel;
    @FXML
    private TableView<SubjectGrade> memberGradesTableView;

    @FXML
    private TableColumn<SubjectGrade, String> memberSubjectGradeTableColumn;

    @FXML
    private TableColumn<SubjectGrade, String> memberSubjectNameTableColumn;





    public void initialize() {
        Student currentStudent = SessionManager.getInstance().getCurrentStudent();
        String message = additionalInfoForMemberLabel.getText();
        String fullMessage = message + " " + currentStudent.getName() + " " + currentStudent.getSurname();
        additionalInfoForMemberLabel.setText(fullMessage);
        
        List<Competition> competitions = DatabaseUtil.getCompetitions();
        LocalDateTime now = LocalDateTime.now();
        competitions.removeIf(competition -> competition.getTimeOfCompetition().isAfter(now));

        List<MathProject> projects = DatabaseUtil.getProjects();

        
        setCompetitionWinNumberLabel(competitions, currentStudent);
        setHighestAndLowestScoreAndNumberOfParticipationLabel(competitions, currentStudent);
        setNumberOfParticipationsInProjectsLabel(projects, currentStudent);
        setMemberGradesTableView(currentStudent);
        setDaysAsMemberLabel(currentStudent);
        setStudentPicture(currentStudent);


    }

    private void setStudentPicture(Student currentStudent) {
        String picturePath = currentStudent.getPicture().getPicturePath();
        if (!picturePath.isEmpty()) {
            File file = new File(picturePath);
            Image image = new Image(file.toURI().toString());
            memberImage.setImage(image);
        }
    }

    private void setDaysAsMemberLabel(Student currentStudent) {
        LocalDate today = LocalDate.now();
        LocalDate joinDate = currentStudent.getClubMembership().getJoinDate();

        long daysAsMember = ChronoUnit.DAYS.between(joinDate, today);

        daysAsMemberLabel.setText(String.valueOf(daysAsMember));
    }

    private void setMemberGradesTableView(Student currentStudent) {
        memberSubjectNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
        memberSubjectGradeTableColumn.setCellValueFactory(cellData -> cellData.getValue().gradeProperty());

        currentStudent.getGrades().forEach((subject, grade) -> {
            SubjectGrade subjectGrade = new SubjectGrade(subject, String.valueOf(grade));
            memberGradesTableView.getItems().add(subjectGrade);
        });
    }


    private void setNumberOfParticipationsInProjectsLabel(List<MathProject> projects, Student currentStudent) {
        Integer numberOfParticipations = 0;
        for (MathProject project: projects){
            if (project.hasStudentCollaborator(currentStudent)){
                numberOfParticipations++;
            }
        }
        projectParticipationLabel.setText(numberOfParticipations.toString());
    }



    private void setHighestAndLowestScoreAndNumberOfParticipationLabel(List<Competition> competitions, Student currentStudent) {
        BigDecimal highestScore = null;
        BigDecimal lowestScore = null;
        boolean hasParticipated = false;
        Integer numberOfParticipations = 0;

        for (Competition competition: competitions){
            if (competition.hasParticipant(currentStudent)){
                numberOfParticipations++;
                hasParticipated = true;
                BigDecimal competitionScore = competition.getCompetitionResultForParticipant(currentStudent).get().score();
                
                if(highestScore == null || competitionScore.compareTo(highestScore) > 0){
                    highestScore = competitionScore;
                }
                if(lowestScore == null || competitionScore.compareTo(lowestScore) < 0){
                    lowestScore = competitionScore;
                }
            }
            
            if (hasParticipated){
                highestScoreInCompetitionLabel.setText(highestScore.toString());
                lowestScoreInCompetitionLabel.setText(lowestScore.toString());

            }
            else{
                highestScoreInCompetitionLabel.setText("Član nije sudjelovao na natjecanjima.");
                lowestScoreInCompetitionLabel.setText("Član nije sudjelovao na natjecanjima.");
            }

            competitionParticipationLabel.setText(numberOfParticipations.toString());
            
        }
    }


    private void setCompetitionWinNumberLabel(List<Competition> competitions, Student currentStudent) {
        Integer numberOfWins = 0;
        for (Competition competition : competitions){
            Optional<Student> winner = competition.findWinner();
            if (winner.isPresent() && winner.get().equals(currentStudent)){
                numberOfWins++;
            }
        }

        competitionWinNumberLabel.setText(numberOfWins.toString());
    }
}
