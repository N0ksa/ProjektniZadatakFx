package hr.java.project.projectfxapp.enums;

public enum ApplicationScreen {

    MainScreen("Glavni ekran", "mainScreen.fxml", false),
    AddNewCompetition("Dodaj natjecanje", "addNewCompetition.fxml", false),
    AddNewMathClub("Dodaj klub", "addNewMathClub.fxml", false),
    AddNewProject("Dodaj projekt", "addNewProject.fxml", false),
    AddNewStudent("Dodaj studenta", "addNewStudent.fxml", false),
    Clubs("Klubovi", "clubs.fxml", false),

    Competition("Natjecanja", "competitions.fxml", false),
    Projects("Projekti", "projects.fxml", false),
    Students("Studenti", "students.fxml", false),
    AddNewClubMember("Dodaj člana kluba", "addNewStudentUser.fxml", false),

    MainScreenForUser("Glavni ekran", "mainScreenForUser.fxml", false),

    ClubMembers("Članovi kluba", "clubMembers.fxml", false),
    MemberCard("Članska iskaznica", "memberCard.fxml", false),
    AddNewStudentUser("Dodaj studenta", "addNewStudentUser.fxml", false),
    UpdateMemberInformation("Ažuriraj podatke", "updateMemberInformation.fxml", false),
    CompetitionsUser("Natjecanja", "competitionsUser.fxml", false),
    ProjectsUser("Projekti", "projectsUser.fxml", false),
    AddNewCompetitionUser("Dodaj natjecanje", "addNewCompetitionUser.fxml", false),
    UpdateCompetitionUser("Ažuriraj natjecanje", "updateCompetitionUser.fxml", false),
    RegisterMembersIntoCompetition("Prijavi studente na natjecanje", "registerMembersIntoCompetition.fxml", false),
    CompetitionDetailsCard("Detalji natjecanja", "competitionDetailsCard.fxml", false),
    Settings("Postavke", "settings.fxml", false),
    AddNewProjectUser("Dodaj projekt", "addNewProjectUser.fxml", false),
    UpdateProjectUser("Ažuriraj projekt", "updateProjectUser.fxml", false),
    RegisterMembersIntoProject("Prijavi članove na projekt", "registerMembersIntoProject.fxml", false),
    ProjectDetailsCard("Detalji projekta", "projectDetailsCard.fxml", false),
    AddressSearch("Pretraži adrese", "addressSearch.fxml", false),
    AddNewAddress("Dodaj adresu", "addNewAddress.fxml", false),
    Changes("Promjene", "changes.fxml", false),
    Login("Prijava", "login.fxml", false),
    NavigationForUser("Navigacija", "navigationForUser.fxml", false);

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
