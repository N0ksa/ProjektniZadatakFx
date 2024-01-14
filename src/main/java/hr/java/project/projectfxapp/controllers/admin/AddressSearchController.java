package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.Address;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.filter.AddressFilter;
import hr.java.project.projectfxapp.filter.MathClubFilter;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.util.List;
import java.util.Optional;

public class AddressSearchController {
    @FXML
    private TableView<Address> addressTableView;

    @FXML
    private ComboBox<City> cityPickerComboBox;

    @FXML
    private TableColumn<Address, String> cityTableColumn;

    @FXML
    private TableColumn<Address, String> houseNumberTableColumn;

    @FXML
    private TextField houseNumberTextField;

    @FXML
    private TextField streetNameTextField;

    @FXML
    private TableColumn<Address, String> streetTableColumn;


    public void initialize(){
        List<Address> addressList = DatabaseUtil.getAddresses();
        setAddressTableView(addressList);

        cityPickerComboBox.getItems().setAll(City.values());

    }


    public void addressSearch(ActionEvent event) {
        City city  = cityPickerComboBox.getValue();
        String streetName = streetNameTextField.getText();
        String houseNumber = houseNumberTextField.getText();
        AddressFilter addressFilter = new AddressFilter(streetName, houseNumber, city);

        List<Address> filteredAddresses = DatabaseUtil.getAddressesByFilter(addressFilter);

        ObservableList<Address> observableAddressList = FXCollections.observableList(filteredAddresses);
        addressTableView.setItems(observableAddressList);
    }


    public void deleteAddress(ActionEvent event) {
        Address addressForDeletion = addressTableView.getSelectionModel().getSelectedItem();

        if (Optional.ofNullable(addressForDeletion).isPresent()){
            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda brisanja",
                    "Jeste li sigurni da želite obrisati adresu?",
                    "Ova radnja je nepovratna.");

            if (positiveConfirmation) {
                boolean successfulDeletion = DatabaseUtil.deleteAddress(addressForDeletion);
                if (successfulDeletion){
                    ValidationProtocol.showSuccessAlert("Brisanje uspješno",
                            "Uspješno ste obrisali adresu : " + addressForDeletion);
                }else{
                    ValidationProtocol.showErrorAlert("Brisanje neuspješno",
                            "Adresa " + addressForDeletion + " nije obrisana",
                            "Nažalost adresu nije moguće obrisati, jer je u korisničkoj uporabi.");
                }
            }

        }

    }


    public void reset(ActionEvent event) {
        streetNameTextField.setText("");
        houseNumberTextField.setText("");
        cityPickerComboBox.setValue(null);
        addressTableView.getItems().clear();
    }


    private void setAddressTableView(List<Address> addresses) {
        cityTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Address, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Address, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getCity().toString());
            }
        });

        streetTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Address, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Address, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getStreet());
            }
        });

        houseNumberTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Address, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Address, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getHouseNumber());
            }
        });

    }
}
