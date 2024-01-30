package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.filter.CompetitionFilter;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.manager.ChangesManager;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import hr.java.project.projectfxapp.validation.ValidationProtocol;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class CompetitionsSearchController {

    @FXML
    private Label clockLabel;
    @FXML
    private TextField competitionNameTextField;
    @FXML
    private DatePicker competitionDatePicker;
    @FXML
    private TableView<Competition> competitionTableView;
    @FXML
    private TableColumn<Competition, String> competitionNameTableColumn;
    @FXML
    private TableColumn<Competition, String> competitionDescriptionTableColumn;
    @FXML
    private TableColumn<Competition, String> competitionAddressTableColumn;
    @FXML
    private TableColumn<Competition, String> competitionTimeTableColumn;
    @FXML
    private TableColumn<Competition, String> competitionDateTableColumn;
    @FXML
    private TableColumn<Competition, String>competitionWinnerTableColumn;



    public void initialize(){

        ClockThread clockThread = ClockThread.getInstance();
        clockThread.setLabelToUpdate(clockLabel);

        setCompetitionTableViewSettings();

    }


    private void setCompetitionTableViewSettings() {
        competitionNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });


        competitionDescriptionTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getDescription());
            }
        });

        competitionAddressTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getAddress().toString());
            }
        });


        competitionDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getTimeOfCompetition()
                        .format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex())));
            }
        });

        competitionTimeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                Competition competition = param.getValue();
                LocalDateTime dateTime = competition.getTimeOfCompetition();

                int hours = dateTime.getHour();
                int minutes = dateTime.getMinute();
                int seconds = dateTime.getSecond();

                String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                return new ReadOnlyStringWrapper(formattedTime);
            }
        });


        competitionWinnerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Competition,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Competition, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().findWinner()
                        .map(student -> student.getName() + " " + student.getSurname()).orElse("Nema pobjednika"));
            }
        });
    }


    public void searchCompetitions(){
        LocalDate dateOfCompetition = competitionDatePicker.getValue();
        String competitionName = competitionNameTextField.getText();

        CompetitionFilter competitionFilter = new CompetitionFilter(competitionName, dateOfCompetition);

        List<Competition> filteredCompetitions = DatabaseUtil.getCompetitionsByFilter(competitionFilter);

        ObservableList<Competition> observableCompetitionsList = FXCollections.observableList(filteredCompetitions);
        competitionTableView.setItems(observableCompetitionsList);

    }

    public void resetCompetitions(){
        competitionNameTextField.setText("");
        competitionTableView.getItems().clear();
        competitionDatePicker.setValue(null);
    }

    public void deleteCompetition(ActionEvent actionEvent) {
        Competition competitionForDeletion = competitionTableView.getSelectionModel().getSelectedItem();

        if (Optional.ofNullable(competitionForDeletion).isPresent()){
            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda brisanja",
                    "Jeste li sigurni da želite obrisati ovo natjecanje?",
                    "Ova radnja je nepovratna.");

            if (positiveConfirmation) {
                boolean successfulDeletion = DatabaseUtil.deleteCompetition(competitionForDeletion);

                if (successfulDeletion){
                    User currentUser = SessionManager.getInstance().getCurrentUser();

                    Change change = Change.create(currentUser, "/",
                            "Obrisano natjecanje: " + competitionForDeletion.getName(), "Natjecanje/id:"
                                    + competitionForDeletion.getId());

                    ChangesManager.setNewChangesIfChangesNotPresent().add(change);

                    competitionTableView.setItems(FXCollections.observableList(DatabaseUtil.getCompetitions()));
                    ValidationProtocol.showSuccessAlert("Brisanje uspješno",
                            "Uspješno ste obrisali natjecanje : " + competitionForDeletion.getName());
                }else{
                    ValidationProtocol.showErrorAlert("Brisanje neuspješno",
                            "Natjecanje " + competitionForDeletion.getName() + " nije obrisano",
                            "Nažalost natjecanje nije moguće obrisati");
                }
            }

        }

    }
}
