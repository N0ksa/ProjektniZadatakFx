package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.MathProject;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.threads.RefreshProjectsScreenThread;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectsUserController {

    @FXML
    private LineChart<String, Integer> comparisonOfProjectOrganizationsBetweenClubsLineChart;
    @FXML
    private BarChart<String, Integer> projectSizeComparisonChart;
    @FXML
    private Label currentClubNameTextField;
    @FXML
    private TextField filterProjects;

    @FXML
    private TableColumn<MathProject, String> projectClubColumnTable;

    @FXML
    private TableColumn<MathProject, String> projectCollaboratorsColumnTable;

    @FXML
    private TableColumn<MathProject, String> projectOrganizerTableColumn;

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
        setProjectSizeComparisonChart(mathProjectsList);
        setComparisonOfProjectOrganizationsBetweenClubsLineChart(mathProjectsList);

        RefreshProjectsScreenThread.startThreadIfThreadNotPresent(comparisonOfProjectOrganizationsBetweenClubsLineChart,
                projectSizeComparisonChart, projectsTableView);

    }

    public void setComparisonOfProjectOrganizationsBetweenClubsLineChart(List<MathProject> mathProjectsList) {

        comparisonOfProjectOrganizationsBetweenClubsLineChart.getData().clear();

        comparisonOfProjectOrganizationsBetweenClubsLineChart.getYAxis().setLabel("Broj projekata");

        Map<MathClub, Integer> numberOfProjectsPerClub = mathProjectsList.stream()
                .collect(Collectors.groupingBy(MathProject::getOrganizer, Collectors.summingInt(project -> 1)));

        XYChart.Series<String, Integer> series = new XYChart.Series<>();

        for (Map.Entry<MathClub, Integer> entry : numberOfProjectsPerClub.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().getName(), entry.getValue()));

        }

        comparisonOfProjectOrganizationsBetweenClubsLineChart.getData().add(series);


    }


        private void setProjectSizeComparisonChart(List<MathProject> mathProjectsList) {
        projectSizeComparisonChart.getData().clear();
        projectSizeComparisonChart.getYAxis().setTickLabelGap(1);
        projectSizeComparisonChart.getYAxis().setLabel("Broj sudionika");

        for (MathProject project : mathProjectsList) {
            Integer numberOfParticipants = project.getTotalNumberOfCollaborators();

            if(numberOfParticipants > 0){
                XYChart.Series<String, Integer> series = new XYChart.Series<>();
                series.setName(project.getName());
                series.getData().add(new XYChart.Data<>("Projekti", numberOfParticipants));
                projectSizeComparisonChart.getData().add(series);
            }

        }


    }


    private void initializeProjectsTableView(ObservableList<MathProject> mathProjectsList) {
        projectNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathProject, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        projectOrganizerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathProject,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathProject, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getOrganizer().getName());
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

        projectsTableView.setItems(mathProjectsList);
        projectsTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        projectsTableView.setRowFactory(tv -> {
            TableRow<MathProject> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    handleProjectDoubleClick(row.getItem());
                }
            });
            return row;
        });



    }

    private void handleProjectDoubleClick(MathProject selectedProject) {
        SessionManager.getInstance().setCurrentProject(selectedProject);
        JavaFxProjectApplication.showPopup(ApplicationScreen.ProjectDetailsCard);
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


    public void addNewProject(ActionEvent actionEvent) {
        JavaFxProjectApplication.showPopup(ApplicationScreen.AddNewProjectUser);
    }

    public void joinProject(ActionEvent actionEvent) {
        MathProject currentProject = projectsTableView.getSelectionModel().getSelectedItem();
        if (Optional.ofNullable(currentProject).isPresent()){

            LocalDate projectEndDate = currentProject.getEndDate();

            if (Optional.ofNullable(projectEndDate).isPresent()){
                if (projectEndDate.isBefore(LocalDate.now())){
                    ValidationProtocol.showErrorAlert("Pogreška prilikom prijave na projekt",
                            "Projekt je završio i nije moguće se prijaviti na njega",
                            "Projekt je završio " + projectEndDate.format(DateTimeFormatter
                                    .ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex())));

                }
                else {
                    SessionManager.getInstance().setCurrentProject(currentProject);
                    JavaFxProjectApplication.showPopup(ApplicationScreen.RegisterMembersIntoProject);
                }

            }else{
                SessionManager.getInstance().setCurrentProject(currentProject);
                JavaFxProjectApplication.showPopup(ApplicationScreen.RegisterMembersIntoProject);
            }

        }
    }

    public void updateProject(ActionEvent actionEvent) {
        MathProject currentProject = projectsTableView.getSelectionModel().getSelectedItem();
        if (Optional.ofNullable(currentProject).isPresent()) {
            MathClub currentClub = SessionManager.getInstance().getCurrentClub();

            if (currentProject.getOrganizer().getId().equals(currentClub.getId())) {
                SessionManager.getInstance().setCurrentProject(currentProject);
                JavaFxProjectApplication.showPopup(ApplicationScreen.UpdateProjectUser);
            } else {
                ValidationProtocol.showErrorAlert("Pogreška prilikom ažuriranja projekta",
                        "Nije moguće ažurirati projekt",
                        "Niste organizator projekta " + currentProject.getName());
            }

        }
    }
}
