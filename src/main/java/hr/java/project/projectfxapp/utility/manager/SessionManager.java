package hr.java.project.projectfxapp.utility.manager;

import hr.java.project.projectfxapp.entities.*;

import java.time.LocalDateTime;

public class SessionManager implements LoginStatisticsRecorder {
    private static SessionManager instance;
    private User currentUser;
    private MathClub currentClub;
    private Student currentStudent;
    private Competition currentCompetition;
    private MathProject currentProject;


    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;



    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public MathProject getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(MathProject project) {
        this.currentProject = project;
    }

    public Competition getCurrentCompetition() {
        return currentCompetition;
    }

    public void setCurrentCompetition(Competition competition) {
        this.currentCompetition = competition;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public MathClub getCurrentClub() {
        return currentClub;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setCurrentClub(MathClub club) {
        this.currentClub = club;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
    }


    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    @Override
    public LoginStatistics recordLoginStatistics() {
        return LoginStatistics.getStatistic(currentUser, currentClub, loginTime, logoutTime);
    }
}
