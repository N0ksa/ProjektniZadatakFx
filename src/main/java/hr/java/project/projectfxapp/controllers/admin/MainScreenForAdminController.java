package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.threads.GetMostRecentChangeThread;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;

public class MainScreenForAdminController {


    @FXML
    private Label clockLabel;

    @FXML
    private TableView<Change> mostRecentChangeTableView;

    @FXML
    private TableColumn<Change, String> dateOfChangeTableColumn;

    @FXML
    private TableColumn<Change, String> newValueTableColumn;

    @FXML
    private TableColumn<Change, String> oldValueTableColumn;

    @FXML
    private TableColumn<Change, String> typeOfValueTableColumn;

    @FXML
    private TableColumn<Change, String> userRoleTableColumn;

    public void initialize() {
               ClockThread clockThread = ClockThread.getInstance();
        clockThread.setLabelToUpdate(clockLabel);
        clockThread.startThread();


        setMostRecentChangeTableView();
        GetMostRecentChangeThread getMostRecentChangeThread = GetMostRecentChangeThread.getInstance();
        getMostRecentChangeThread.setTableViewToUpdate(mostRecentChangeTableView);

        boolean newThreadStarted = getMostRecentChangeThread.startThread();
        if (!newThreadStarted){
            getMostRecentChangeThread.executeTaskManually();
        }





    }



    private void setMostRecentChangeTableView() {

        dateOfChangeTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().timestamp().toString()));
        newValueTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().newValue()));
        oldValueTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().oldValue()));
        typeOfValueTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().dataType()));
        userRoleTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().userRole()));

        setWrappingCellFactory(dateOfChangeTableColumn);
        setWrappingCellFactory(newValueTableColumn);
        setWrappingCellFactory(oldValueTableColumn);
        setWrappingCellFactory(typeOfValueTableColumn);
        setWrappingCellFactory(userRoleTableColumn);
    }



    private void setWrappingCellFactory(TableColumn<Change, String> column) {
        column.setCellFactory(tc -> {
            TableCell<Change, String> cell = new TableCell<>() {
                private final Text text = new Text();

                {
                    text.wrappingWidthProperty().bind(column.widthProperty());
                    setGraphic(text);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        text.setText(null);
                    } else {
                        text.setText(item);
                    }
                }
            };
            return cell;
        });
    }
}
