package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.MathProject;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import hr.java.project.projectfxapp.utility.FileWriterUtil;
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

import java.util.*;
import java.util.stream.Collectors;

public class AddNewProjectController {
    @FXML
    private ListView<MathClub> projectMathClubsParticipantsListView;
    @FXML
    private  ListView<Student> projectStudentParticipantsListView;
    @FXML
    private ProgressBar addNewProjectProgressBar;
    @FXML
    private TextField projectNameTextField;
    @FXML
    private TextArea projectDescriptionTextArea;

    private static final int TOTAL_FIELDS = 3;

    public void initialize() {

        List<Student> availableStudentsList = FileReaderUtil.getStudentsFromFile();

        List<MathClub> mathClubsList = FileReaderUtil.getMathClubsFromFile(availableStudentsList
                ,FileReaderUtil.getAddressesFromFile())
                .stream()
                .filter(mathClub -> !mathClub.getId().equals(0L))
                .collect(Collectors.toList());


        ObservableList<MathClub> mathClubsObservableList = FXCollections.observableList(mathClubsList);


        addNewProjectProgressBar.progressProperty().bind(Bindings.createDoubleBinding(
                () -> calculateCompletionPercentage(),
                projectNameTextField.textProperty(),
                projectDescriptionTextArea.textProperty(),
                projectStudentParticipantsListView.getSelectionModel().getSelectedItems()
        ));

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
    }


    private double calculateCompletionPercentage() {
        int filledFields = 0;

        if (!projectNameTextField.getText().isEmpty()) {
            filledFields++;
        }

        if (!projectDescriptionTextArea.getText().isEmpty()) {
            filledFields++;
        }

        if (projectStudentParticipantsListView.getSelectionModel().getSelectedItem() != null) {
            filledFields++;
        }

        // Calculate the completion percentage
        return (double) filledFields / TOTAL_FIELDS;
    }

    public void saveProject(ActionEvent actionEvent) {
        List<MathProject> mathProjects = FileReaderUtil.
                getMathProjectsFromFile(FileReaderUtil.getMathClubsFromFile(FileReaderUtil.getStudentsFromFile(),
                        FileReaderUtil.getAddressesFromFile()), FileReaderUtil.getStudentsFromFile());


        Long projectId = FileWriterUtil.getNextProjectId();
        String projectName = projectNameTextField.getText();
        String projectDescription = projectDescriptionTextArea.getText();

        Map<MathClub, List<Student>> projectParticipants = new HashMap<>();

        for (MathClub selectedMathClub : projectMathClubsParticipantsListView.getSelectionModel().getSelectedItems()) {
            List<Student> selectedStudents = projectStudentParticipantsListView.getSelectionModel().getSelectedItems()
                    .stream()
                    .filter(student -> student.getClubMembership().getClubId().equals(selectedMathClub.getId()))
                            .collect(Collectors.toList());

            projectParticipants.put(selectedMathClub, selectedStudents);
        }


        MathProject newProject = new MathProject(projectId, projectName, projectDescription, projectParticipants);

        mathProjects.add(newProject);
        FileWriterUtil.saveProjectsToFile(mathProjects);



    }
}
