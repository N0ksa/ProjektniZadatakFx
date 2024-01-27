package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.utility.*;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.manager.ChangesManager;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RegisterMembersIntoCompetitionController {

    @FXML
    private ListView<Student> allRegistratedCompetitionParticipantsListView;

    @FXML
    private ListView<Student> availableClubMembersListView;

    @FXML
    private ListView<Student> registratedClubMembersListView;

    private List<Student> availableClubMembers;
    private List<Student> registeredClubMembers;
    private List<Student> registeredMembersForCompetition;

    public void initialize() {
        MathClub currentClub = SessionManager.getInstance().getCurrentClub();
        Competition currentCompetition = SessionManager.getInstance().getCurrentCompetition();

        List<Student> allClubMembers = currentClub.getStudents().stream().toList();

        registeredMembersForCompetition = currentCompetition.getCompetitionResults()
                .stream()
                .map(CompetitionResult::participant)
                .collect(Collectors.toList());

        availableClubMembers = allClubMembers.stream()
                .filter(student -> !registeredMembersForCompetition.contains(student))
                .collect(Collectors.toCollection(ArrayList::new));

        registeredClubMembers = allClubMembers.stream()
                .filter(registeredMembersForCompetition::contains)
                .collect(Collectors.toCollection(ArrayList::new));

        refreshListViews();
    }

    public void register(ActionEvent event) {

        boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda",
                "Jeste li sigurni da želite promijeniti registrirane članove u natjecanju?",
                    "Ako ste sigurni da želite promijeniti registrirane članove u natjecanju, pritisnite Da");


        if (positiveConfirmation){


            Competition currentCompetition = SessionManager.getInstance().getCurrentCompetition();

            Competition oldcompetition = new Competition(currentCompetition);


            currentCompetition.getCompetitionResults().clear();

            registeredMembersForCompetition.forEach(student -> {
                CompetitionResult competitionResult = new CompetitionResult(student, BigDecimal.ZERO);

                currentCompetition.getCompetitionResults().add(competitionResult);

            });

            boolean successfullyUpdated  = DatabaseUtil.updateCompetitionScores(currentCompetition.getId(),
                    currentCompetition.getCompetitionResults());

            if (successfullyUpdated){

                Optional<Change> change = oldcompetition.getChange(currentCompetition);

                if (change.isPresent()) {
                    ChangesManager.getChanges().add(change.get());;
                }

                JavaFxProjectApplication.switchScene(ApplicationScreen.CompetitionsUser);
                ValidationProtocol.showSuccessAlert("Uspješno ste promijenili registrirane članove u natjecanju",
                        "Uspješno ste promijenili registrirane članove u natjecanju");

            }
            else{
                ValidationProtocol.showErrorAlert("Pogreška", "Pogreška prilikom promjene registriranih članova u natjecanju",
                        "Pogreška prilikom promjene registriranih članova u natjecanju");
            }

        }

    }



    public void removeClubMemberFromCompetition(ActionEvent event) {
        Student selectedStudent = registratedClubMembersListView.getSelectionModel().getSelectedItem();
        if (Optional.ofNullable(selectedStudent).isPresent()) {
            registeredClubMembers.remove(selectedStudent);
            availableClubMembers.add(selectedStudent);
            registeredMembersForCompetition.remove(selectedStudent);

            refreshListViews();
        }
    }

    private void refreshListViews() {
        availableClubMembersListView.setItems(FXCollections.observableArrayList(availableClubMembers));
        registratedClubMembersListView.setItems(FXCollections.observableArrayList(registeredClubMembers));
        allRegistratedCompetitionParticipantsListView.setItems(FXCollections.observableArrayList(registeredMembersForCompetition));
    }

    public void registerClubMemberToCompetition(MouseEvent mouseEvent) {

        Student selectedStudent = availableClubMembersListView.getSelectionModel().getSelectedItem();
        if (Optional.ofNullable(selectedStudent).isPresent()) {
            registeredClubMembers.add(selectedStudent);
            availableClubMembers.remove(selectedStudent);
            registeredMembersForCompetition.add(selectedStudent);

            refreshListViews();
        }
    }
}
