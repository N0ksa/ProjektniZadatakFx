package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.filter.MathProjectFilter;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.manager.ChangesManager;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import hr.java.project.projectfxapp.validation.ValidationProtocol;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectsSearchController {
    @FXML
    private Label clockLabel;
    @FXML
    private TableView<MathProject> projectsTableView;
    @FXML
    private TextField projectNameTextField;
    @FXML
    private  TableColumn<MathProject, String> projectNameTableColumn;
    @FXML
    private  TableColumn<MathProject, String> projectDescriptionTableColumn;
    @FXML
    private  TableColumn<MathProject, String> projectCollaboratorsColumnTable;
    @FXML
    private  TableColumn<MathProject, String> projectClubColumnTable;
    @FXML
    private  TableColumn<MathProject, String> projectMembersColumnTable;



    public void initialize(){

        ClockThread clockThread = ClockThread.getInstance();
        clockThread.setLabelToUpdate(clockLabel);

        projectNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathProject, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        projectDescriptionTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathProject, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getDescription());
            }
        });

        projectClubColumnTable.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject, String>,
                ObservableValue<String>>() {
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



        projectMembersColumnTable.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject, String>,
                ObservableValue<String>>() {
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

        MathProjectFilter mathProjectFilter = new MathProjectFilter(projectName);

        List<MathProject> filteredMathProjects = DatabaseUtil.getMathProjectsByFilter(mathProjectFilter);

        ObservableList <MathProject> observableProjectsList = FXCollections.observableList(filteredMathProjects);
        projectsTableView.setItems(observableProjectsList);


    }

    public void deleteProject(ActionEvent actionEvent) {
        MathProject mathProjectForDeletion = projectsTableView.getSelectionModel().getSelectedItem();

        if (Optional.ofNullable(mathProjectForDeletion).isPresent()){
            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda brisanja",
                    "Jeste li sigurni da želite obrisati ovaj projekt?",
                    "Ova radnja je nepovratna.");

            if (positiveConfirmation) {
                boolean successfulDeletion = DatabaseUtil.deleteProject(mathProjectForDeletion);
                if (successfulDeletion){

                    User currentUser = SessionManager.getInstance().getCurrentUser();

                    Change change = Change.create(currentUser, "/",
                            "Obrisan projekt: " + mathProjectForDeletion.getName(), "Projekt/id:"
                                    + mathProjectForDeletion.getId());

                    ChangesManager.setNewChangesIfChangesNotPresent().add(change);

                    JavaFxProjectApplication.switchScene(ApplicationScreen.Projects);
                    ValidationProtocol.showSuccessAlert("Brisanje uspješno",
                            "Uspješno ste obrisali projekt : " + mathProjectForDeletion.getName());
                }else{
                    ValidationProtocol.showErrorAlert("Brisanje neuspješno",
                            "Projekt " + mathProjectForDeletion.getName() + " nije obrisan",
                            "Nažalost projekt nije moguće obrisati");
                }
            }

        }

    }

    public void reset(ActionEvent actionEvent) {
        projectNameTextField.setText("");
        projectsTableView.getItems().clear();
    }
}
