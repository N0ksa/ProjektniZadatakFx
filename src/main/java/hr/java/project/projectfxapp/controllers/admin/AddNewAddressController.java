package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.Address;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.ValidationProtocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddNewAddressController {

    @FXML
    private ComboBox<City> cityPickerComboBox;

    @FXML
    private TextField houseNumberTextField;

    @FXML
    private TextField streetNameTextField;



    public void initialize() {
        cityPickerComboBox.getItems().setAll(City.values());
    }
    public void saveAddress(ActionEvent event) {
        try {
            ValidationProtocol.validateAddress(streetNameTextField, houseNumberTextField, cityPickerComboBox);

            Address.AdressBuilder addressBuilder = new Address.AdressBuilder(cityPickerComboBox.getValue())
                    .setAddressId(0L)
                    .setStreet(streetNameTextField.getText())
                    .setHouseNumber(houseNumberTextField.getText());

            Address address = addressBuilder.build();


            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda unosa",
                    "Jeste li sigurni da želite unijeti novu adresu?",
                    "Ako želite unijeti adresu: " + address + "\nPritisnite Da za potvrdu");


            if (positiveConfirmation) {
                DatabaseUtil.saveAddress(address);
                ValidationProtocol.showSuccessAlert("Uspješan unos", "Uspješno ste unijeli novu adresu");
            }


        } catch (ValidationException ex) {
            ValidationProtocol.showErrorAlert("Greška prilikom registracije", "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }
    }

}
