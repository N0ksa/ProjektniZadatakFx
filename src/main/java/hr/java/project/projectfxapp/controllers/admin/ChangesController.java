package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Recordable;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.threads.DeserializeChangesThread;
import hr.java.project.projectfxapp.utility.SerializationUtil;
import hr.java.project.projectfxapp.utility.SessionManager;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChangesController {

    @FXML
    private Label clockLabel;
    @FXML
    private TableColumn<Change,String> typeOfValueTableColumn;
    @FXML
    private TableView<Change> changesTableView;

    @FXML
    private Button clearChanges;

    @FXML
    private TableColumn<Change, String> dateOfChangeTableColumn;

    @FXML
    private TableColumn<Change, String> newValueTableColumn;

    @FXML
    private TableColumn<Change, String> oldValueTableColumn;

    @FXML
    private TableColumn<Change, String> userRoleTableColumn;

    public void initialize() {
        ClockThread clockThread = ClockThread.getInstance();
        clockThread.setLabelToUpdate(clockLabel);

        setChangesTableView();
        DeserializeChangesThread deserializeChangesThread = DeserializeChangesThread.getInstance();
        deserializeChangesThread.setChangesTableView(changesTableView);
        deserializeChangesThread.startThread();

    }

    private void setChangesTableView() {
        newValueTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Change,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Change, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().newValue());
            }
        });

        oldValueTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Change,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Change, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().oldValue());
            }
        });

        typeOfValueTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Change,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Change, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().dataType());
            }
        });

        userRoleTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Change,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Change, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().userRole());
            }
        });

        dateOfChangeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Change, String>, ObservableValue<String>>() {

            public ObservableValue<String> call(TableColumn.CellDataFeatures<Change, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().timestamp().
                        format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_TIME_REGEX.getRegex())));
            }

        });



    }

    public void deleteChanges(ActionEvent actionEvent) {
        boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog(
                "Brisanje promjena", "Brisanje promjena",
                "Jeste li sigurni da želite obrisati sve promjene?" +
                        "\nPritisnite Da za brisanje");

        if (positiveConfirmation){
            List<Change> emptyChanges = new ArrayList<>();
            SerializationUtil.serializeChanges(emptyChanges);
            changesTableView.setItems(FXCollections.observableArrayList(emptyChanges));
        }
    }

    public void deleteSelectedChange(ActionEvent actionEvent) {
        Change selectedChange = changesTableView.getSelectionModel().getSelectedItem();
        if (Optional.ofNullable(selectedChange).isPresent()){

            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog(
                    "Brisanje promjene", "Brisanje promjene", "Jeste li sigurni da želite obrisati odabranu promjenu?" +
                            "\nPritisnite Da za brisanje");

            if (positiveConfirmation){
                List<Change> changes = SerializationUtil.deserializeChanges();
                changes.remove(selectedChange);
                SerializationUtil.serializeChanges(changes);
                changesTableView.setItems(FXCollections.observableArrayList(changes));
            }
        }


    }
}
