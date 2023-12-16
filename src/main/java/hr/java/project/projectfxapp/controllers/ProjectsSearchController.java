package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.MathProject;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import static hr.java.project.projectfxapp.utility.FileReaderUtil.*;
public class ProjectsSearchController {
    @FXML
    private TableView projectsTableView;
    @FXML
    private TextField projectNameTextField;
    @FXML
    private  TableColumn projectNameTableColumn;
    @FXML
    private  TableColumn projectDescriptionTableColumn;
    @FXML
    private  TableColumn projectCollaboratorsColumnTable;
    @FXML
    private  TableColumn projectClubColumnTable;
    @FXML
    private  TableColumn projectMembersColumnTable;

    private static List<MathProject> projects;
    private static List<Student> students;


    public void initialize(){

        students = getStudentsFromFile();
        projects = getMathProjectsFromFile(getMathClubsFromFile(students, getAddressesFromFile()), students);
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

        projectClubColumnTable.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathProject, String> param) {
                Map< MathClub, List<Student>> collaborators = param.getValue().getCollaborators();

                String clubCollaborators = collaborators.keySet().stream()
                        .map(MathClub::getName)
                        .collect(Collectors.joining("\n"));

                return new ReadOnlyStringWrapper(clubCollaborators);
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




    }


    public void projectSearch(){
        String projectName = projectNameTextField.getText();
        List<MathProject> filteredProjects = projects.stream()
                .filter(project -> project.getName().contains(projectName))
                .collect(Collectors.toList());

        ObservableList <MathProject> observableProjectsList = FXCollections.observableList(filteredProjects);
        projectsTableView.setItems(observableProjectsList);


    }
}
