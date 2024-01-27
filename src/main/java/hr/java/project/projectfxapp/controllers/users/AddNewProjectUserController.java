package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.*;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.manager.ChangesManager;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewProjectUserController {
    @FXML
    private ComboBox<City> cityComboBox;

    @FXML
    private TextField streetNameTextField;
    @FXML
    private TextField houseNumberTextField;
    @FXML
    private DatePicker beginningDateOfProjectDatePicker;

    @FXML
    private TextArea projectDescriptionTextArea;

    @FXML
    private ListView<Student> projectMathClubsParticipantsListView;

    @FXML
    private TextField projectNameTextField;

    public void initialize() {
        MathClub currentClub = SessionManager.getInstance().getCurrentClub();
        List<Student> clubMembers = currentClub.getStudents().stream().toList();

        projectMathClubsParticipantsListView.setItems(FXCollections.observableList(clubMembers));
        projectMathClubsParticipantsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cityComboBox.getItems().setAll(City.values());

    }

    public void saveProject(ActionEvent event) {
        List<MathProject> mathProjects = new ArrayList<>();

        try{

            ValidationProtocol.validateProjectForUser(projectNameTextField, projectDescriptionTextArea,
                    projectMathClubsParticipantsListView, beginningDateOfProjectDatePicker, cityComboBox,
                    streetNameTextField, houseNumberTextField);

            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda spremanja",
                    "Jeste li sigurni da želite spremiti novi projekt?",
                    "Kliknite Da za potvrdu");

            if (positiveConfirmation){

                MathProject newProject = constructNewProject();
                mathProjects.add(newProject);

                boolean success = DatabaseUtil.saveMathProjects(mathProjects);

                if (success){

                    User currentUser = SessionManager.getInstance().getCurrentUser();

                    Change change = Change.create(currentUser, "/",
                            "Spremljen novi projekt: " + newProject.getName(), "Projekt");

                    ChangesManager.setNewChangesIfChangesNotPresent().add(change);

                    JavaFxProjectApplication.switchScene(ApplicationScreen.ProjectsUser);

                    ValidationProtocol.showSuccessAlert("Spremanje novog projekta je bilo uspješno",
                            "Projekt " + newProject.getName() + "  uspješno se spremio");
                }
                else{
                    ValidationProtocol.showErrorAlert("Greška pri spremanju novog projekta",
                            "Projekt " + newProject.getName() + " nije se uspješno spremio",
                            "Pokušajte ponovno spremiti projekt");
                }

            }

        }
        catch (ValidationException ex) {
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }


    }

    private MathProject constructNewProject() {

        Long projectId = 0L;
        String projectName = projectNameTextField.getText();
        String projectDescription = projectDescriptionTextArea.getText();
        MathClub projectOrganizer = SessionManager.getInstance().getCurrentClub();
        LocalDate beginningDateOfProject = beginningDateOfProjectDatePicker.getValue();

        Address.AdressBuilder adressBuilder = new Address.AdressBuilder(cityComboBox.getSelectionModel().getSelectedItem());
        adressBuilder.setAddressId(0L)
                .setStreet(streetNameTextField.getText())
                .setHouseNumber(houseNumberTextField.getText());


        Address address = adressBuilder.build();

        Long addressId = DatabaseUtil.saveAddress(address);

        adressBuilder.setAddressId(addressId);
        Address projectAddress = adressBuilder.build();

        Map<MathClub, List<Student>> projectParticipants = new HashMap<>();

        List<Student> selectedStudents = projectMathClubsParticipantsListView.getSelectionModel().getSelectedItems();
        projectParticipants.put(projectOrganizer, selectedStudents);


        return new MathProject(projectId, projectOrganizer,
                beginningDateOfProject, projectAddress, projectName, projectDescription, projectParticipants);
    }
}
