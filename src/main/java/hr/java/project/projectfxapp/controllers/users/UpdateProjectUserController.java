package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.Address;
import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.entities.MathProject;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.manager.ChangesManager;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import hr.java.project.projectfxapp.validation.ValidationProtocol;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class UpdateProjectUserController {
    @FXML
    private TextField projectWebAddress;
    @FXML
    private ComboBox<City> cityComboBox;

    @FXML
    private DatePicker endDateOfProjectDatePicker;

    @FXML
    private TextField houseNumberTextField;

    @FXML
    private TextField newProjectNameTextField;

    @FXML
    private TextArea projectDescriptionTextArea;

    @FXML
    private TextField streetNameTextField;


    public void initialize(){
        MathProject currentProject = SessionManager.getInstance().getCurrentProject();
        setCurrentProjectInformation(currentProject);

    }

    private void setCurrentProjectInformation(MathProject currentProject) {
        newProjectNameTextField.setText(currentProject.getName());
        projectDescriptionTextArea.setText(currentProject.getDescription());
        streetNameTextField.setText(currentProject.getAddress().getStreet());
        houseNumberTextField.setText(currentProject.getAddress().getHouseNumber());
        cityComboBox.setItems(FXCollections.observableList(List.of(City.values())));
        cityComboBox.setValue(currentProject.getAddress().getCity());
        projectWebAddress.setText(currentProject.getProjectWebPageAddress());

        endDateOfProjectDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date.isBefore(currentProject.getStartDate()) || date.isEqual(currentProject.getStartDate())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        endDateOfProjectDatePicker.setValue(currentProject.getEndDate());
    }


    public void updateProject(ActionEvent event) {
        try{
            ValidationProtocol.validateUpdateProject(newProjectNameTextField, projectDescriptionTextArea,
                    streetNameTextField, houseNumberTextField, cityComboBox);

            MathProject projectToUpdate = SessionManager.getInstance().getCurrentProject();
            boolean positiveConfirmation =  ValidationProtocol.showConfirmationDialog
                    ("Potvrda ažuriranja projekta", "Ažuriranje projekta",
                            "Jeste li sigurni da želite ažurirati projekt " + projectToUpdate.getName() + "?" +
                                    "\nAko ste sigurni pritisnite Da");

            if(positiveConfirmation){

                MathProject oldProject = new MathProject(projectToUpdate);
                boolean updateSuccessful = changeProject(projectToUpdate);

                if (updateSuccessful){

                    Optional<Change> change = oldProject.getChange(projectToUpdate);

                    if (change.isPresent()) {
                        ChangesManager.setNewChangesIfChangesNotPresent().add(change.get());;
                    }

                    JavaFxProjectApplication.switchScene(ApplicationScreen.ProjectsUser);
                    ValidationProtocol.showSuccessAlert("Ažuriranje projekta je bilo uspješno",
                            "Projekt " + projectToUpdate.getName() + " uspješno se ažurirao!");
                }
                else{
                    ValidationProtocol.showErrorAlert("Greška pri ažuriranju",
                            "Ažuriranje projekta nije uspjelo",
                            "Pokušajte ponovno");
                }

            }


        }catch (ValidationException ex){
            ValidationProtocol.showErrorAlert("Greška pri unosu",
                    "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }





    }

    private boolean changeProject(MathProject projectToUpdate) {
        projectToUpdate.setName(newProjectNameTextField.getText());
        projectToUpdate.setDescription(projectDescriptionTextArea.getText());

        projectToUpdate.setEndDate(endDateOfProjectDatePicker.getValue());

        Address.AddressBuilder addressBuilder = new Address.AddressBuilder(cityComboBox.getValue())
                .setAddressId(projectToUpdate.getAddress().getAddressId())
                .setStreet(streetNameTextField.getText())
                .setHouseNumber(houseNumberTextField.getText());

        projectToUpdate.setAddress(addressBuilder.build());
        projectToUpdate.setProjectWebPageAddress(projectWebAddress.getText());

       return DatabaseUtil.updateProject(projectToUpdate);

    }

}
