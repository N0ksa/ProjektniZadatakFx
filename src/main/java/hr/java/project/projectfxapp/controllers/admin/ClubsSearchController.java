package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.filter.MathClubFilter;
import hr.java.project.projectfxapp.threads.ClockThread;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.files.FileReaderUtil;
import hr.java.project.projectfxapp.utility.files.FileWriterUtil;
import hr.java.project.projectfxapp.utility.files.SerializationUtil;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import hr.java.project.projectfxapp.validation.ValidationProtocol;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClubsSearchController {
    @FXML
    private Label clockLabel;
    @FXML
    private TextField clubNameTextField;
    @FXML
    private TableView<MathClub> clubsTableView;
    @FXML
    private TableColumn<MathClub, String> clubNameTableColumn;
    @FXML
    private TableColumn<MathClub, String> clubAddressTableColumn;
    @FXML
    private TableColumn<MathClub, String> clubMembersTableColumn;


    public void initialize(){

        ClockThread clockThread = ClockThread.getInstance();
        clockThread.setLabelToUpdate(clockLabel);

        clubNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathClub,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathClub, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });


        clubAddressTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathClub,String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathClub, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getAddress().toString());
            }
        });

        clubMembersTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MathClub, String>,
                ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MathClub, String> param) {
                MathClub mathClub = param.getValue();
                List<Student> students = new ArrayList<>(mathClub.getStudents());

                if (students.isEmpty()) {
                    return new ReadOnlyStringWrapper("Nema članova");
                } else {
                    String studentNames = students.stream()
                            .map(student -> student.getName() + " " + student.getSurname())
                            .collect(Collectors.joining(", "));

                    return new ReadOnlyStringWrapper(studentNames);
                }
            }
        });



    }

    public void clubSearch(ActionEvent actionEvent) {
        String clubName = clubNameTextField.getText();

        MathClubFilter mathClubFilter = new MathClubFilter(clubName);
        List<MathClub> filteredMathClubs = DatabaseUtil.getMathClubsByFilter(mathClubFilter);

        ObservableList<MathClub> observableMathClubList = FXCollections.observableList(filteredMathClubs);
        clubsTableView.setItems(observableMathClubList);
    }

    public void deleteMathClub(ActionEvent actionEvent) {
        MathClub mathClubForDeletion = clubsTableView.getSelectionModel().getSelectedItem();

        if (Optional.ofNullable(mathClubForDeletion).isPresent()){
            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda brisanja",
                    "Jeste li sigurni da želite obrisati matematički klub?",
                    "Ova radnja je nepovratna.");

            if (positiveConfirmation) {

                Optional<User> userForDeletion = DatabaseUtil.getUser(mathClubForDeletion.getId());

                boolean successfulUserDeletion = DatabaseUtil.deleteUser(userForDeletion.get());

                boolean successfulMathClubDeletion = DatabaseUtil.deleteMathClub(mathClubForDeletion);

                if (successfulUserDeletion && successfulMathClubDeletion){
                    List<User> users = FileReaderUtil.getUsers();

                    users.removeIf(user -> user.getUsername().equals(userForDeletion.get().getUsername()));
                    FileWriterUtil.saveUsers(users);

                    Change change = Change.create(SessionManager.getInstance().getCurrentUser(), "/",
                            "Obrisan matematički klub: " + mathClubForDeletion.getName(),
                            "Korisnik/Matematički klub:id=" + mathClubForDeletion.getId());

                    List<Change> changes = SerializationUtil.deserializeChanges();
                    changes.add(change);
                    SerializationUtil.serializeChanges(changes);


                    refreshTableView();
                    ValidationProtocol.showSuccessAlert("Brisanje uspješno",
                            "Uspješno ste obrisali matematički klub : " + mathClubForDeletion.getName());

                }else
                {
                    ValidationProtocol.showErrorAlert("Brisanje neuspješno",
                            "Matematički klub " + mathClubForDeletion.getName() + " nije obrisan",
                            "Nažalost matematički klub nije moguće obrisati");
                }


            }

        }
    }

    public void reset(ActionEvent actionEvent) {
        clubNameTextField.setText("");
        clubsTableView.getItems().clear();

    }

    private void refreshTableView() {
        List<MathClub> mathClubs = DatabaseUtil.getMathClubs();
        clubsTableView.setItems(FXCollections.observableList(mathClubs));
    }
}
