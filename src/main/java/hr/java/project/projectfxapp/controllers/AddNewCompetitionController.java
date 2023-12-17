package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.entities.Address;
import hr.java.project.projectfxapp.entities.CompetitionResult;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.converter.BigDecimalStringConverter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    private TextField auditoriumNameTextArea;
    @FXML
    private TextField auditoriumBuildingNameTextArea;
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
    }

    public void saveCompetition(ActionEvent actionEvent) {
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
        // Check if a result for the participant already exists in the list
        return competitionResults.stream()
                .anyMatch(result -> result.participant().equals(participant));
    }

    public void removeSelectedParticipantFromTableView(ActionEvent actionEvent) {
        ObservableList<CompetitionResult> selectedResults = competitionResultsTableView.getSelectionModel().getSelectedItems();

        competitionResultsTableView.getItems().removeAll(selectedResults);


        removeParticipantButton.setDisable(competitionResultsTableView.getItems().isEmpty());
    }



}
