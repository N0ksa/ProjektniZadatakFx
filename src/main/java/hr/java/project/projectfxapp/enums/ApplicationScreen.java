package hr.java.project.projectfxapp.enums;

public enum ApplicationScreen {

    MainScreen("Glavni ekran", "mainScreen.fxml", true),
    AddNewCompetition("Dodaj natjecanje", "addNewCompetition.fxml", true),
    AddNewMathClub("Dodaj klub", "addNewMathClub.fxml", true),
    AddNewProject("Dodaj projekt", "addNewProject.fxml", true),
    AddNewStudent("Dodaj studenta", "addNewStudent.fxml", true),
    Clubs("Klubovi", "clubs.fxml", true),

    Competition("Natjecanja", "competitions.fxml", true),
    Projects("Projekti", "projects.fxml", true),
    Students("Studenti", "students.fxml", true),
    AddNewClubMember("Dodaj člana kluba", "addNewStudentUser.fxml", true),

    MainScreenForUser("Glavni ekran", "mainScreenForUser.fxml", true),
    AddNewCCompetitionUser("Dodaj natjecanje", "addNewCompetitionUser.fxml", false),

    AddNewProjectUser("Dodaj projekt", "addNewProjectUser.fxml", false);
    private final String title;
    private final String pathOfFxml;

    private final boolean resizable;

    ApplicationScreen(String title, String pathOfFxml, boolean resizable){
        this.title = title;
        this.pathOfFxml = pathOfFxml;
        this.resizable = resizable;
    }

    public String getTitle() {
        return title;
    }

    public String getPathOfFxml() {
        return pathOfFxml;
    }

    public boolean isResizable() {
        return resizable;
    }
}
