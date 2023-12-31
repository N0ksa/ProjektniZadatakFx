package hr.java.project.projectfxapp.utility;

import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.entities.User;

public class SessionManager {
    private static SessionManager instance;

    private User currentUser;

    private MathClub currentClub;

    private Student currentStudent;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
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

    public void clearSession() {
        this.currentUser = null;
    }
}
