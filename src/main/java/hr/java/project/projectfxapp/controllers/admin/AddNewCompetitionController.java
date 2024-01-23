package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.Status;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.threads.DeserializeChangesThread;
import hr.java.project.projectfxapp.threads.SerializeChangesThread;
import hr.java.project.projectfxapp.utility.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.BigDecimalStringConverter;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddNewCompetitionController {

    @FXML
    private Label clockLabel;
    @FXML
    private ComboBox<MathClub> organizerComboBox;
    @FXML
    private Button removeParticipantButton;
    @FXML
    private TextField competitionNameTextField;
    @FXML
    private TextArea competitionDescriptionTextArea;
    @FXML
    private ComboBox<Address> competitionAddressComboBox;
    @FXML
    private TextField auditoriumHallNameTextField;
    @FXML
    private TextField auditoriumBuildingNameTextField;
    @FXML
    private DatePicker competitionDateDatePicker;
    @FXML
    private TextField competitionTimeTextArea;
    @FXML
    private ListView<Student> competitionParticipantsListView;
    @FXML
    private TableView<CompetitionResult> competitionResultsTableView;
    @FXML
    private TableColumn<CompetitionResult, Student> competitionParticipantTableColumn;
    @FXML
    private TableColumn<CompetitionResult, BigDecimal> competitionScoreTableColumn;


    public void initialize(){

        ClockThread clockThread = ClockThread.getInstance();
        clockThread.setLabelToUpdate(clockLabel);

        List<Student> potentialParticipantsList = DatabaseUtil.getStudents();
        ObservableList<Student> potentialParticipantsObservableList = FXCollections.observableList(potentialParticipantsList);
        List<Address> addressesList = DatabaseUtil.getAddresses();
        ObservableList<Address> addressObservableList = FXCollections.observableList(addressesList);

        List<MathClub> mathClubs = DatabaseUtil.getMathClubs();
        ObservableList<MathClub> mathClubsObservableList = FXCollections.observableList(mathClubs);
        organizerComboBox.setItems(mathClubsObservableList);

        competitionParticipantsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        competitionParticipantsListView.setItems(potentialParticipantsObservableList);

        competitionAddressComboBox.setItems(addressObservableList);

        competitionResultsTableView.editableProperty().set(true);
        competitionParticipantTableColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().participant()));

        competitionScoreTableColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().score()));
        competitionScoreTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));


        competitionScoreTableColumn.setOnEditCommit(event -> {

            CompetitionResult result = event.getRowValue();

            BigDecimal newScore = event.getNewValue();
            CompetitionResult updatedResult = result.withScore(newScore);

            int index = competitionResultsTableView.getItems().indexOf(result);
            competitionResultsTableView.getItems().set(index, updatedResult);
        });

        removeParticipantButton.setDisable(true);

        competitionDescriptionTextArea.setWrapText(true);
    }



    public void saveCompetition(ActionEvent actionEvent) {
        try {
            ValidationProtocol.validateCompetition(competitionNameTextField, competitionDescriptionTextArea,
                    competitionAddressComboBox, competitionDateDatePicker, competitionTimeTextArea,
                    auditoriumBuildingNameTextField, auditoriumHallNameTextField, competitionResultsTableView,
                    organizerComboBox);


            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda unosa",
                    "Jeste li sigurni da želite unijeti novo natjecanje?",
                    "Ako želite unijeti natjecanje: " + competitionNameTextField.getText() + "\nPritisnite Da za potvrdu");

            if (positiveConfirmation){

                List<Competition> competitions = new ArrayList<>();

                Competition newCompetition = constructCompetition();
                competitions.add(newCompetition);

                boolean success = DatabaseUtil.saveMathCompetitions(competitions);

                if (success){
                    User currentUser = SessionManager.getInstance().getCurrentUser();
                    Change change = Change.create(currentUser, "/",
                            "Dodano natjecanje: " + newCompetition.getName(), "Natjecanje");

                    ChangesManager.getChanges().add(change);


                    ValidationProtocol.showSuccessAlert("Spremanje novog natjecanja je bilo uspješno",
                            "Natjecanje " + newCompetition.getName()  + " uspješno se spremio!");
                }
                else{
                    ValidationProtocol.showErrorAlert("Greška pri spremanju", "Greška pri spremanju natjecanja",
                            "Došlo je do greške pri spremanju natjecanja u bazu podataka");
                }


            }


        } catch (ValidationException ex) {
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }
    }





    private Competition constructCompetition() {
        Long competitionId = 0L;
        String competitionName = competitionNameTextField.getText();
        String competitionDescription = competitionDescriptionTextArea.getText();
        Address competitionAddress = competitionAddressComboBox.getValue();
        LocalDate competitionDate = competitionDateDatePicker.getValue();

        String competitionTimeText = competitionTimeTextArea.getText();

        LocalTime competitionTime = LocalTime.parse(competitionTimeText,
                DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_TIME_REGEX.getRegex()));

        LocalDateTime competitionDateTime = competitionDate.atTime(competitionTime);

        String buildingName = auditoriumBuildingNameTextField.getText();
        String hallName = auditoriumHallNameTextField.getText();

        Auditorium competitionAuditorium = new Auditorium(buildingName, hallName);

        Set<CompetitionResult> competitionResults = new HashSet<>(competitionResultsTableView.getItems());

        MathClub organizer = organizerComboBox.getValue();

        Status status = Status.FINISHED;
        if (competitionDateTime.isAfter(LocalDateTime.now())) {
            status = Status.PLANNED;
        }

        return new Competition(competitionId, organizer, competitionName, competitionDescription,
                competitionAddress, competitionAuditorium, competitionDateTime, status, competitionResults);
    }


    public void reset(ActionEvent actionEvent) {
        competitionNameTextField.setText("");
        competitionDescriptionTextArea.setText("");
        competitionResultsTableView.getItems().clear();
        competitionAddressComboBox.getSelectionModel().clearSelection();
        competitionDateDatePicker.setValue(null);
        competitionTimeTextArea.setText("");
        auditoriumBuildingNameTextField.setText("");
        auditoriumHallNameTextField.setText("");

    }

    public void addPotentialParticipantToTableView(MouseEvent mouseEvent) {
        ObservableList<Student> participants = competitionParticipantsListView.getSelectionModel().getSelectedItems();

        ObservableList<CompetitionResult> competitionResults = competitionResultsTableView.getItems();

        participants.forEach(participant -> {
            if (!resultAlreadyExists(participant, competitionResults)) {
                competitionResults.add(new CompetitionResult(participant, new BigDecimal(0)));
            }
        });

        competitionResultsTableView.setItems(competitionResults);

        removeParticipantButton.setDisable(competitionResults.isEmpty());

    }

    private boolean resultAlreadyExists(Student participant, ObservableList<CompetitionResult> competitionResults) {
        return competitionResults.stream()
                .anyMatch(result -> result.participant().equals(participant));
    }

    public void removeSelectedParticipantFromTableView(ActionEvent actionEvent) {

        ObservableList<CompetitionResult> selectedResults = competitionResultsTableView.getSelectionModel().getSelectedItems();

        competitionResultsTableView.getItems().removeAll(selectedResults);

        removeParticipantButton.setDisable(competitionResultsTableView.getItems().isEmpty());
    }



}
