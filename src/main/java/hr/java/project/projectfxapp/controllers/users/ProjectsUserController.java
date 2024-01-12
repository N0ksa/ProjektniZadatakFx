package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.MathProject;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectsUserController {
    @FXML
    private Label currentClubNameTextField;
    @FXML
    private TextField filterProjects;

    @FXML
    private TableColumn<MathProject, String> projectClubColumnTable;

    @FXML
    private TableColumn<MathProject, String> projectCollaboratorsColumnTable;

    @FXML
    private TableColumn<MathProject, String> projectDescriptionTableColumn;

    @FXML
    private TableColumn<MathProject, String> projectMembersColumnTable;

    @FXML
    private TableColumn<MathProject, String> projectNameTableColumn;

    @FXML
    private TableView<MathProject> projectsTableView;



    public void initialize(){

        currentClubNameTextField.setText(SessionManager.getInstance().getCurrentClub().getName());
        List<MathProject> mathProjectsList = DatabaseUtil.getProjects();
        FilteredList<MathProject> filteredMathProjects = getMathProjectsFilteredList(mathProjectsList);

        initializeProjectsTableView(filteredMathProjects);

    }

    private void initializeProjectsTableView(ObservableList<MathProject> mathProjectsList) {
        projectNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathProject, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        projectDescriptionTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathProject, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getDescription());
            }
        });

        projectClubColumnTable.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathProject, String> param) {
                Map<MathClub, List<Student>> collaborators = param.getValue().getCollaborators();

                if (collaborators.isEmpty()) {
                    return new ReadOnlyStringWrapper("Nema članova");
                } else {
                    String clubCollaborators = collaborators.keySet().stream()
                            .map(MathClub::getName)
                            .collect(Collectors.joining("\n"));

                    return new ReadOnlyStringWrapper(clubCollaborators);
                }
            }
        });



        projectMembersColumnTable.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathProject, String> param) {
                Map<MathClub, List<Student>> collaborators = param.getValue().getCollaborators();

                String clubCollaborators = collaborators.values().stream()
                        .map(collaboratorsList -> collaboratorsList.stream()
                                .map(student -> student.getName() + " " + student.getSurname())
                                .collect(Collectors.joining(", ")))
                        .collect(Collectors.joining("\n"));

                return new ReadOnlyStringWrapper(clubCollaborators);
            }
        });

        projectsTableView.setItems(mathProjectsList);


    }


    private FilteredList<MathProject> getMathProjectsFilteredList(List<MathProject> mathProjects) {

        FilteredList<MathProject> filteredMathProjects = new FilteredList<>(FXCollections.observableList(mathProjects), p -> true);

        filterProjects.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMathProjects.setPredicate(mathProject -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return mathProject.getName().toLowerCase().contains(lowerCaseFilter)
                        || mathProject.getDescription().toLowerCase().contains(lowerCaseFilter)
                        || mathProject.getCollaborators().keySet().stream()
                        .map(MathClub::getName).collect(Collectors.joining(", "))
                        .toLowerCase()
                        .contains(lowerCaseFilter)
                        || mathProject.getCollaborators().values().stream()
                        .map(collaboratorsList -> collaboratorsList.stream()
                                .map(student -> student.getName() + " " + student.getSurname())
                                .collect(Collectors.joining(", ")))
                        .collect(Collectors.joining("\n")).toLowerCase().contains(lowerCaseFilter);
            });
        });

        return filteredMathProjects;
    }




}
