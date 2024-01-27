package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.MathProject;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ProjectDetailsCardController {


    @FXML
    private Label projectStartDateLabel;
    @FXML
    private WebView projectWebPageWebView;
    @FXML
    private TextArea projectDescriptionTextArea;

    @FXML
    private Label projectDurationLabel;

    @FXML
    private Label projectEndDateLabel;




    public void initialize() {
        MathProject currentProject = SessionManager.getInstance().getCurrentProject();

        setProjectInformation(currentProject);

        WebEngine webEngine = projectWebPageWebView.getEngine();


        webEngine.getLoadWorker().exceptionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String projectWebPageAddress = currentProject.getProjectWebPageAddress();
                if (projectWebPageAddress.equals(Constants.DEFAULT_PROJECT_WEB_PAGE)){
                    webEngine.load(getClass().getResource(projectWebPageAddress).toExternalForm());
                }else{
                    webEngine.load(getClass().getResource(Constants.DEFAULT_ERROR_WEB_PAGE).toExternalForm());
                }

            }
        });

        webEngine.load(currentProject.getProjectWebPageAddress());
    }

    private void setProjectInformation(MathProject currentProject) {

        projectDescriptionTextArea.setText(currentProject.getDescription());
        projectDescriptionTextArea.setWrapText(true);

        LocalDate startDate = currentProject.getStartDate();
        LocalDate today = LocalDate.now();

        projectStartDateLabel.setText(startDate.format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex())));

        if(startDate.isAfter(today)){
            projectDurationLabel.setText("Projekt još nije počeo");
        }else {
            int projectDuration = startDate.until(today).getDays();
            projectDurationLabel.setText(projectDuration + " dana");
        }

        LocalDate endDate = currentProject.getEndDate();
        if (Optional.ofNullable(endDate).isPresent()){
            projectEndDateLabel.setText(endDate.format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex())));
        }else{
            projectEndDateLabel.setText("Projekt još nije završio");
        }
    }






}
