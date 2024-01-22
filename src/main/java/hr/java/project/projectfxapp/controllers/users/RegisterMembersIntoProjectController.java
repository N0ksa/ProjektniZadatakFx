package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.utility.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class RegisterMembersIntoProjectController {

    @FXML
    private ListView<Student> allRegistratedProjectParticipantsListView;

    @FXML
    private ListView<Student> availableClubMembersListView;

    @FXML
    private ListView<Student> registratedClubMembersListView;


    private List<Student> availableClubMembers;
    private List<Student> registeredClubMembers;
    private List<Student> registeredMembersForProject;


    public void initialize() {

        MathClub currentClub = SessionManager.getInstance().getCurrentClub();
        MathProject currentProject = SessionManager.getInstance().getCurrentProject();

        List<Student> allClubMembers = currentClub.getStudents().stream().toList();

        registeredMembersForProject = currentProject.getCollaborators().values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());


        availableClubMembers = allClubMembers.stream()
                .filter(student -> !registeredMembersForProject.contains(student))
                .collect(Collectors.toCollection(ArrayList::new));

        registeredClubMembers = allClubMembers.stream()
                .filter(registeredMembersForProject::contains)
                .collect(Collectors.toCollection(ArrayList::new));

        refreshListViews();

    }



    public void registerClubMemberToProject(MouseEvent event) {
        Student selectedStudent = availableClubMembersListView.getSelectionModel().getSelectedItem();
        if (Optional.ofNullable(selectedStudent).isPresent()) {
            registeredClubMembers.add(selectedStudent);
            availableClubMembers.remove(selectedStudent);
            registeredMembersForProject.add(selectedStudent);

            refreshListViews();
        }
    }


   public void removeClubMemberFromProject(ActionEvent event) {
       Student selectedStudent = registratedClubMembersListView.getSelectionModel().getSelectedItem();
       if (Optional.ofNullable(selectedStudent).isPresent()) {
           registeredClubMembers.remove(selectedStudent);
           availableClubMembers.add(selectedStudent);
           registeredMembersForProject.remove(selectedStudent);

           refreshListViews();
       }
    }

    public void register(ActionEvent actionEvent) {
        MathProject currentProject = SessionManager.getInstance().getCurrentProject();
        MathProject oldProject = new MathProject(currentProject);

        boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda",
                "Jeste li sigurni da želite promijeniti članove u projektu?",
                "Ako ste sigurni da želite promijeniti članove u projektu, pritisnite Da");


        if (positiveConfirmation) {



            List<MathClub> mathClubs = DatabaseUtil.getMathClubs();

            if (Optional.ofNullable(currentProject.getCollaborators()).isEmpty()) {
                currentProject.setCollaborators(new HashMap<>());
            } else {
                currentProject.getCollaborators().clear();
            }

            for (MathClub mathClub : mathClubs) {
                List<Student> students = registeredMembersForProject.stream()
                        .filter(student -> student.getClubMembership().getClubId().equals(mathClub.getId()))
                        .toList();

                if (!students.isEmpty()) {
                    currentProject.getCollaborators().put(mathClub, students);
                }
            }
        }

        boolean successfullyUpdated  = DatabaseUtil.updateProjectCollaborators(currentProject.getId(),
                    currentProject.getCollaborators());

            if (successfullyUpdated){

                Optional<Change> change = oldProject.getChange(currentProject);
                if (change.isPresent()) {
                    ChangesManager.getChanges().add(change.get());
                }

                ValidationProtocol.showSuccessAlert("Uspješno ste promijenili registrirane članove u natjecanju",
                        "Uspješno ste promijenili registrirane članove u natjecanju");

            }
            else{
                ValidationProtocol.showErrorAlert("Pogreška", "Pogreška prilikom promjene registriranih članova u natjecanju",
                        "Pogreška prilikom promjene registriranih članova u natjecanju");
            }


    }


    private void refreshListViews() {
        availableClubMembersListView.setItems(FXCollections.observableArrayList(availableClubMembers));
        registratedClubMembersListView.setItems(FXCollections.observableArrayList(registeredClubMembers));
        allRegistratedProjectParticipantsListView.setItems(FXCollections.observableArrayList(registeredMembersForProject));
    }
}
