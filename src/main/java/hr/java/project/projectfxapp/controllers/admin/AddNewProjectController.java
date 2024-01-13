package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.Address;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.MathProject;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import hr.java.project.projectfxapp.utility.FileWriterUtil;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AddNewProjectController {
    @FXML
    private ComboBox<Address> projectAddressComboBox;
    @FXML
    private ComboBox <MathClub> projectOrganizerComboBox;
    @FXML
    private DatePicker beginningDateOfProjectDatePicker;
    @FXML
    private ListView<MathClub> projectMathClubsParticipantsListView;
    @FXML
    private  ListView<Student> projectStudentParticipantsListView;

    @FXML
    private TextField projectNameTextField;
    @FXML
    private TextArea projectDescriptionTextArea;


    public void initialize() {

        List<MathClub> mathClubsList = DatabaseUtil.getMathClubs();


        ObservableList<MathClub> mathClubsObservableList = FXCollections.observableList(mathClubsList);


        projectMathClubsParticipantsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        projectMathClubsParticipantsListView.setItems(mathClubsObservableList);

        projectMathClubsParticipantsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        projectMathClubsParticipantsListView.setItems(mathClubsObservableList);

        projectMathClubsParticipantsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            List<MathClub> selectedMathClubs = new ArrayList<>(projectMathClubsParticipantsListView.getSelectionModel()
                    .getSelectedItems());


            List<Student> allSelectedStudents = selectedMathClubs.stream()
                    .flatMap(mathClub -> mathClub.getStudents().stream())
                    .collect(Collectors.toList());


            projectStudentParticipantsListView.setItems(FXCollections.observableList(allSelectedStudents));
        });

        projectStudentParticipantsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        projectOrganizerComboBox.setItems(FXCollections.observableList(mathClubsList));

        List<Address> addresses = DatabaseUtil.getAddresses();
        projectAddressComboBox.setItems(FXCollections.observableList(addresses));

    }

    public void saveProject(ActionEvent actionEvent) {

        List<MathProject> mathProjects = new ArrayList<>();

        try{

            ValidationProtocol.validateProject(projectNameTextField, projectDescriptionTextArea,
                    projectMathClubsParticipantsListView, projectStudentParticipantsListView, projectOrganizerComboBox,
                    beginningDateOfProjectDatePicker, projectAddressComboBox);

            MathProject newProject = constructNewProject();

            mathProjects.add(newProject);
            DatabaseUtil.saveMathProjects(mathProjects);

            ValidationProtocol.showSuccessAlert("Spremanje novog projekta je bilo uspješno",
                    "Projekt " + newProject.getName() + "  uspješno se spremio");
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
        MathClub projectOrganizer = projectOrganizerComboBox.getSelectionModel().getSelectedItem();
        LocalDate beginningDateOfProject = beginningDateOfProjectDatePicker.getValue();

        Address projectAddress = projectAddressComboBox.getSelectionModel().getSelectedItem();

        Map<MathClub, List<Student>> projectParticipants = new HashMap<>();

        for (MathClub selectedMathClub : projectMathClubsParticipantsListView.getSelectionModel().getSelectedItems()) {
            List<Student> selectedStudents = projectStudentParticipantsListView.getSelectionModel().getSelectedItems()
                    .stream()
                    .filter(student -> student.getClubMembership().getClubId().equals(selectedMathClub.getId()))
                    .collect(Collectors.toList());

            projectParticipants.put(selectedMathClub, selectedStudents);
        }


        return new MathProject(projectId, projectOrganizer,
                beginningDateOfProject, projectAddress, projectName, projectDescription, projectParticipants);
    }
}
