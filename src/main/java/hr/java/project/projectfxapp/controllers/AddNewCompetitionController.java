package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import hr.java.project.projectfxapp.utility.FileWriterUtil;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddNewCompetitionController {

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
        List<Student> potentialParticipantsList = FileReaderUtil.getStudentsFromFile();
        ObservableList<Student> potentialParticipantsObservableList = FXCollections.observableList(potentialParticipantsList);
        List<Address> addressesList = FileReaderUtil.getAddressesFromFile();
        ObservableList<Address> addressObservableList = FXCollections.observableList(addressesList);

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
                    auditoriumBuildingNameTextField, auditoriumHallNameTextField, competitionResultsTableView);



            List<Competition> competitions = FileReaderUtil.getMathCompetitionsFromFile(
                    FileReaderUtil.getStudentsFromFile(), FileReaderUtil.getAddressesFromFile());

            Long competitionId = FileWriterUtil.getNextCompetitionId();
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

            Competition newCompetition = new Competition(competitionId, competitionName, competitionDescription,
                    competitionAddress, competitionAuditorium, competitionDateTime, competitionResults);

            competitions.add(newCompetition);

            FileWriterUtil.saveCompetitionsToFile(competitions);


            ValidationProtocol.showSuccessAlert("Spremanje novog natjecanja je bilo uspješno",
                    "Natjecanje " + newCompetition.getName()  + " uspješno se spremio!");

        } catch (ValidationException ex) {
            ValidationProtocol.showErrorAlert("Greška pri unosu", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }
    }




    public void reset(ActionEvent actionEvent) {

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
