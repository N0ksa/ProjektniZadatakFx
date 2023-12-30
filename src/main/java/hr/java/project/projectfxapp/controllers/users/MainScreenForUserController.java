package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.User;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import hr.java.project.projectfxapp.utility.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainScreenForUserController {

    private static final User currentUser = SessionManager.getInstance().getCurrentUser();
    private static final MathClub currentClub = DatabaseUtil.getMathClub(currentUser.getMathClubId()).get();

    @FXML
    private Label welcomeMessageLabel;


    public void initialize() {
        welcomeMessageLabel.setText("Dobrodošli, " + currentClub.getName() + "!");
    }

}
