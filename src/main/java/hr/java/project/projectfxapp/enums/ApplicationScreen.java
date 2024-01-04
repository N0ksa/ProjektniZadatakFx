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

    AddNewProjectUser("Dodaj projekt", "projectsUser.fxml", false),
    ClubMembers("Članovi kluba", "clubMembers.fxml", true),
    MemberCard("Članska iskaznica", "memberCard.fxml", false),
    AddNewStudentUser("Dodaj studenta", "addNewStudentUser.fxml", false),
    UpdateMemberInformation("Ažuriraj podatke", "updateMemberInformation.fxml", false),
    CompetitionsUser("Natjecanja", "competitionsUser.fxml", true),
    ProjectsUser("Projekti", "projectsUser.fxml", true),
    AddNewCompetitionUser("Dodaj natjecanje", "addNewCompetitionUser.fxml", false),
    UpdateCompetitionUser("Ažuriraj natjecanje", "updateCompetitionUser.fxml", false),
    RegisterMembersIntoCompetition("Prijavi studente na natjecanje", "registerMembersIntoCompetition.fxml", false);

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
