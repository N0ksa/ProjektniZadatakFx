package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.entities.Student;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.util.Duration;

public class AddNewProjectController {
    @FXML
    private ProgressBar addNewProjectProgressBar;
    @FXML
    private TextField projectNameTextField;
    @FXML
    private TextArea projectDescriptionTextArea;
    @FXML
    private TreeView<Student> projectParticipantsTreeView;

    private static final int TOTAL_FIELDS = 3; // Total number of input fields

    public void initialize() {
        // Bind the progress property of the ProgressBar to the total completion percentage

        addNewProjectProgressBar.progressProperty().bind(Bindings.createDoubleBinding(
                () -> calculateCompletionPercentage(),
                projectNameTextField.textProperty(),
                projectDescriptionTextArea.textProperty(),
                projectParticipantsTreeView.getSelectionModel().selectedItemProperty()
        ));
    }

    private double calculateCompletionPercentage() {
        int filledFields = 0;

        if (!projectNameTextField.getText().isEmpty()) {
            filledFields++;
        }

        if (!projectDescriptionTextArea.getText().isEmpty()) {
            filledFields++;
        }

        // You may need to adjust this condition based on the TreeView's selection criteria
        if (projectParticipantsTreeView.getSelectionModel().getSelectedItem() != null) {
            filledFields++;
        }

        // Calculate the completion percentage
        return (double) filledFields / TOTAL_FIELDS;
    }

    public void saveProject(ActionEvent actionEvent) {
        // Implement your save logic here
    }
}
