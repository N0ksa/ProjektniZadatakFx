package hr.java.project.projectfxapp.enums;

public enum ChangeApplicationScreen {

    MainScreen("Glavni ekran", "mainScreen.fxml", true),
    AddNewCompetition("Dodaj natjecanje", "addNewCompetition.fxml", true),
    AddNewMathClub("Dodaj klub", "addNewMathClub.fxml", true),
    AddNewProject("Dodaj projekt", "addNewProject.fxml", true),
    AddNewStudent("Dodaj studenta", "addNewStudent.fxml", true),
    Clubs("Klubovi", "clubs.fxml", true),
    Competition("Natjecanja", "competitions.fxml", true),
    Projects("Projekti", "projects.fxml", true),
    Students("Studenti", "students.fxml", true);



    private final String title;
    private final String pathOfFxml;

    private final boolean resizable;

    ChangeApplicationScreen(String title, String pathOfFxml, boolean resizable){
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
