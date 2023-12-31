package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.enums.Status;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.SessionManager;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddNewCompetitionUserController {


    @FXML
    private TextField  timeOfCompetitionTextField;
    @FXML
    private DatePicker dateOfCompetitionDatePicker;
    @FXML
    private ComboBox<City> cityComboBox;
    @FXML
    private TextField auditoriumBuildingNameTextField;

    @FXML
    private TextField auditoriumHallNameTextField;

    @FXML
    private TextArea competitionDescriptionTextArea;

    @FXML
    private TextField competitionNameTextField;

    @FXML
    private ListView<Student> competitionParticipantsListView;
    @FXML
    private TextField houseNumberTextField;

    @FXML
    private TextField streetNameTextField;




    public void initialize() {

       MathClub currentClub =  SessionManager.getInstance().getCurrentClub();
       competitionParticipantsListView.setItems(FXCollections.observableList(currentClub.getStudents().stream().toList()));
       competitionParticipantsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
       cityComboBox.getItems().setAll(City.values());


        dateOfCompetitionDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate currentDate = LocalDate.now();
                setDisable(date.isBefore(currentDate) && !date.isEqual(currentDate));;
            }
        });


    }

    @FXML
    void saveCompetition(ActionEvent event) {

        try {

            ValidationProtocol.validateCompetitionForUser(competitionNameTextField, competitionDescriptionTextArea,
                    dateOfCompetitionDatePicker, timeOfCompetitionTextField,
                    auditoriumBuildingNameTextField, auditoriumHallNameTextField, competitionParticipantsListView);


            List<Competition> competitions = new ArrayList<>();

            Long competitionId = 0L;
            String competitionName = competitionNameTextField.getText();
            String competitionDescription = competitionDescriptionTextArea.getText();

            LocalDate competitionDate = dateOfCompetitionDatePicker.getValue();

            String competitionTimeText = timeOfCompetitionTextField.getText();

            LocalTime competitionTime = LocalTime.parse(competitionTimeText,
                    DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_TIME_REGEX.getRegex()));

            LocalDateTime competitionDateTime = competitionDate.atTime(competitionTime);

            String buildingName = auditoriumBuildingNameTextField.getText();
            String hallName = auditoriumHallNameTextField.getText();

            Auditorium competitionAuditorium = new Auditorium(buildingName, hallName);

            Set<CompetitionResult> competitionResults = setCompetitionResults(competitionParticipantsListView);

            MathClub organizer = SessionManager.getInstance().getCurrentClub();

            Status status = Status.PLANNED;

            Address.AdressBuilder adressBuilder = new Address.AdressBuilder(cityComboBox.getValue())
                    .setHouseNumber(houseNumberTextField.getText())
                    .setStreet(streetNameTextField.getText())
                    .setAddressId(0L);

            Address address = adressBuilder.build();
            Long addressId = DatabaseUtil.saveAddress(address);

            adressBuilder.setAddressId(addressId);

            Address competitionAddress = adressBuilder.build();

            Competition newCompetition = new Competition(competitionId, organizer, competitionName, competitionDescription,
                    competitionAddress, competitionAuditorium, competitionDateTime, status, competitionResults);

            competitions.add(newCompetition);
            DatabaseUtil.saveMathCompetitions(competitions);


            ValidationProtocol.showSuccessAlert("Spremanje novog natjecanja je bilo uspješno",
                    "Natjecanje " + newCompetition.getName() + " uspješno se spremilo!");

        } catch (ValidationException ex) {
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());


        }
    }

    private Set<CompetitionResult> setCompetitionResults(ListView<Student> competitionParticipantsListView) {
        Set<CompetitionResult> competitionResults = new HashSet<>();
        List<Student> competitionParticipants = competitionParticipantsListView.getSelectionModel().getSelectedItems();
        for (Student competitor : competitionParticipants){
            competitionResults.add(new CompetitionResult(competitor, new BigDecimal(0)));
        }

        return competitionResults;
    }

}

