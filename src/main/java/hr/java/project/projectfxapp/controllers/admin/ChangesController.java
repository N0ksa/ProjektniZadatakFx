package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Recordable;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.utility.SerializationUtil;
import hr.java.project.projectfxapp.utility.SessionManager;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChangesController {


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
        List<Change> changes = SerializationUtil.deserializeChanges();
        setChangesTableView(changes);

    }

    private void setChangesTableView(List<Change> changes) {
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


        changesTableView.setItems(FXCollections.observableArrayList(changes));
    }
}
