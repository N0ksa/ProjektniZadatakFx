package hr.java.project.projectfxapp.utility;

import javafx.util.converter.BigDecimalStringConverter;

import java.math.BigDecimal;

public class CustomBigDecimalStringConverter extends BigDecimalStringConverter {

    @Override
    public BigDecimal fromString(String value) {
        if (isValidInput(value)) {
            return super.fromString(value);
        } else {
            ValidationProtocol.showErrorAlert("Greška",
                    "Unos nije valjan",
                    "Uneseni podatak nije valjan. Molimo unesite broj.");
            return BigDecimal.ZERO;
        }
    }

    private boolean isValidInput(String value) {
        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
