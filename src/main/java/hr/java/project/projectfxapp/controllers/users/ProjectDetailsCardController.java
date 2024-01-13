package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.Address;
import hr.java.project.projectfxapp.entities.MathProject;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.utility.SessionManager;
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
    private WebView locationOfProjectWebView;
    @FXML
    private TextArea projectDescriptionTextArea;

    @FXML
    private Label projectDurationLabel;

    @FXML
    private Label projectEndDateLabel;




    public void initialize(){
        MathProject currentProject = SessionManager.getInstance().getCurrentProject();

        setProjectInformation(currentProject);

        WebEngine webEngine = locationOfProjectWebView.getEngine();
        String openStreetMapURL = generateOpenStreetMapURL(currentProject.getAddress());
        webEngine.load(openStreetMapURL);

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



    private String generateOpenStreetMapURL(Address address) {
        if (address != null) {
            City city = address.getCity();

            return String.format(
                    "https://www.openstreetmap.org/export/embed.html?mlat=%s&mlon=%s#map=15/%s/%s",
                    city.getName().replaceAll(" ", "_"),
                    city.getPostalCode(),
                    city.getName().replaceAll(" ", "_"),
                    city.getPostalCode());
        } else {
            return "https://www.openstreetmap.org/export/embed.html?bbox=0,0,0,0&layer=mapnik";
        }
    }


}
