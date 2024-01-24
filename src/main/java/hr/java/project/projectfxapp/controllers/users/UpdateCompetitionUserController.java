package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.enums.Status;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.BigDecimalStringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UpdateCompetitionUserController {

    @FXML
    private TableColumn<CompetitionResult, Student> competitionParticipantTableColumn;

    @FXML
    private TableView<CompetitionResult> competitionResultsTableView;

    @FXML
    private TableColumn<CompetitionResult, BigDecimal> competitionScoreTableColumn;

    @FXML
    private Label enterResultsLabel;

    @FXML
    private TextField auditoriumBuildingNameTextField;

    @FXML
    private TextField auditoriumHallNameTextField;

    @FXML
    private ComboBox<City> cityComboBox;

    @FXML
    private TextArea competitionDescriptionTextArea;

    @FXML
    private TextField competitionNameTextField;

    @FXML
    private DatePicker dateOfCompetitionDatePicker;

    @FXML
    private TextField houseNumberTextField;

    @FXML
    private TextField streetNameTextField;

    @FXML
    private TextField timeOfCompetitionTextField;



    public void initialize() {
       Competition currentCompetition =  SessionManager.getInstance().getCurrentCompetition();
       setCurrentCompetitionInformation(currentCompetition);

       LocalDateTime competitionDateTime = currentCompetition.getTimeOfCompetition();
       LocalDateTime now = LocalDateTime.now();

         if (competitionDateTime.isBefore(now) || competitionDateTime.isEqual(now)) {
     	   enterResultsLabel.setVisible(true);
     	   competitionResultsTableView.setVisible(true);
         }


        dateOfCompetitionDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate minSelectableDate = LocalDate.now().plusDays(4);

                if (date.isBefore(minSelectableDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });


        competitionResultsTableView.editableProperty().set(true);
        competitionParticipantTableColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().participant()));

        competitionScoreTableColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().score()));
        competitionScoreTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));

        competitionScoreTableColumn.setOnEditCommit(event -> {
            CompetitionResult result = event.getRowValue();
            BigDecimal newScore = event.getNewValue();
            CompetitionResult updatedResult = result.withScore(newScore);

            List<CompetitionResult> updatedList = new ArrayList<>(competitionResultsTableView.getItems());
            int index = updatedList.indexOf(result);
            updatedList.set(index, updatedResult);

            competitionResultsTableView.setItems(FXCollections.observableList(updatedList));
        });




    }

    private void setCurrentCompetitionInformation(Competition currentCompetition) {
        auditoriumBuildingNameTextField.setText(currentCompetition.getAuditorium().building());
        auditoriumHallNameTextField.setText(currentCompetition.getAuditorium().hall());

        cityComboBox.setItems(FXCollections.observableList(List.of(City.values())));
        cityComboBox.setValue(currentCompetition.getAddress().getCity());
        competitionDescriptionTextArea.setText(currentCompetition.getDescription());
        competitionNameTextField.setText(currentCompetition.getName());
        dateOfCompetitionDatePicker.setValue(currentCompetition.getTimeOfCompetition().toLocalDate());
        houseNumberTextField.setText(currentCompetition.getAddress().getHouseNumber());
        streetNameTextField.setText(currentCompetition.getAddress().getStreet());
        timeOfCompetitionTextField.setText(currentCompetition.getTimeOfCompetition().toLocalTime().toString());


        List<CompetitionResult> competitionResults = new ArrayList<>(currentCompetition.getCompetitionResults());
        competitionResultsTableView.setItems(FXCollections.observableList(competitionResults));
    }


   public void updateCompetition(ActionEvent event) {
        try{
            ValidationProtocol.validateUpdateCompetition(competitionNameTextField, competitionDescriptionTextArea,
                    cityComboBox, dateOfCompetitionDatePicker, timeOfCompetitionTextField,
                    auditoriumBuildingNameTextField, auditoriumHallNameTextField, competitionResultsTableView);

            Competition competitionToUpdate = SessionManager.getInstance().getCurrentCompetition();
            boolean positiveConfirmation =  ValidationProtocol.showConfirmationDialog
                    ("Potvrda ažuriranja natjecanja", "Ažuriranje natjecanja",
                            "Jeste li sigurni da želite ažurirati natjecanje " + competitionToUpdate.getName() + "?" +
                                    "\nAko ste sigurni pritisnite Da");

            if(positiveConfirmation){

                Competition oldcompetition = new Competition(competitionToUpdate);
                boolean updateSuccessful = changeCompetition(competitionToUpdate);

                if (updateSuccessful){

                    Optional<Change> change = oldcompetition.getChange(competitionToUpdate);

                    if (change.isPresent()){
                        ChangesManager.getChanges().add(change.get());
                    }

                    JavaFxProjectApplication.switchScene(ApplicationScreen.CompetitionsUser);

                    ValidationProtocol.showSuccessAlert("Ažuriranje natjecanja je bilo uspješno",
                            "Natjecanje " + competitionToUpdate.getName() + " uspješno se ažuriralo!");

                }
                else{
                    ValidationProtocol.showErrorAlert("Greška pri ažuriranju", "Ažuriranje natjecanja nije uspjelo",
                            "Pokušajte ponovno");
                }

            }


        }catch (ValidationException ex){
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }


   }

    private boolean changeCompetition(Competition competitionToUpdate) {
        competitionToUpdate.setName(competitionNameTextField.getText());
        competitionToUpdate.setDescription(competitionDescriptionTextArea.getText());

        Address.AdressBuilder addressBuilder = new Address.AdressBuilder(cityComboBox.getValue())
                .setAddressId(competitionToUpdate.getAddress().getAddressId())
                .setStreet(streetNameTextField.getText())
                .setHouseNumber(houseNumberTextField.getText());

        competitionToUpdate.setAddress(addressBuilder.build());

        LocalDate competitionDate = dateOfCompetitionDatePicker.getValue();

        LocalTime competitionTime = LocalTime.parse(timeOfCompetitionTextField.getText(),
                DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_TIME_REGEX.getRegex()));

        LocalDateTime competitionDateTime = competitionDate.atTime(competitionTime);
        competitionToUpdate.setTimeOfCompetition(competitionDateTime);

        competitionToUpdate.setAuditorium(new Auditorium(auditoriumBuildingNameTextField.getText(),
                auditoriumHallNameTextField.getText()));


        Set<CompetitionResult> competitionResults = new HashSet<>(competitionResultsTableView.getItems());
        competitionToUpdate.setCompetitionResults(competitionResults);


        return DatabaseUtil.updateCompetition(competitionToUpdate);
    }

    public void checkForPossibleCompetitionScoreToEnter(ActionEvent actionEvent) {
        LocalDate selectedDate = dateOfCompetitionDatePicker.getValue();
        LocalDate today = LocalDate.now();

        if (selectedDate.isBefore(today) || selectedDate.isEqual(today)) {
            enterResultsLabel.setVisible(true);
            competitionResultsTableView.setVisible(true);
            List<CompetitionResult> competitionResults = SessionManager.getInstance().getCurrentCompetition()
                    .getCompetitionResults().stream().toList();

            competitionResultsTableView.setItems(FXCollections.observableList(competitionResults));

        } else {
            enterResultsLabel.setVisible(false);
            competitionResultsTableView.setVisible(false);
        }
    }

}
