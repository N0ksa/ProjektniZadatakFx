package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.Gender;
import hr.java.project.projectfxapp.enums.YearOfStudy;
import hr.java.project.projectfxapp.exception.ValidationException;
import hr.java.project.projectfxapp.utility.*;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.files.FileUtility;
import hr.java.project.projectfxapp.utility.manager.ChangesManager;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class AddNewStudentUserController {

    @FXML
    private RadioButton maleGenderRadioButton;
    @FXML
    private ToggleGroup genderSelection;
    @FXML
    private RadioButton femaleGenderRadioButton;
    @FXML
    private ToggleGroup yearOfStudySelection;

    @FXML
    private RadioButton drugaGodinaRadioButton;

    @FXML
    private RadioButton prvaGodinaRadioButton;

    @FXML
    private TextField studentEmailTextField;

    @FXML
    private TableView<SubjectGrade> studentGradesTableView;

    @FXML
    private ImageView studentImageView;

    @FXML
    private TextField studentNameTextField;

    @FXML
    private TextField studentSurnameTextField;

    @FXML
    private TableColumn<SubjectGrade, String> subjectGradeTableColumn;

    @FXML
    private TableColumn<SubjectGrade, String> subjectNameTableColumn;

    @FXML
    private RadioButton trecaGodinaRadioButton;

    private static String imagePath = Constants.DEFAULT_PICTURE_MEMBER_ICON;
    private static final Logger logger = LoggerFactory.getLogger(AddNewStudentUserController.class);


    public void initialize(){

        imagePath = Constants.DEFAULT_PICTURE_MEMBER_ICON;

        studentGradesTableView.setEditable(true);
        subjectNameTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getSubject()));

        subjectGradeTableColumn.setCellValueFactory(param -> param.getValue().gradeProperty());
        subjectGradeTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        subjectGradeTableColumn.setOnEditCommit(event -> {

            String newGrade = event.getNewValue();

            int index = event.getTablePosition().getRow();

            SubjectGrade subjectGrade = event.getTableView().getItems().get(index);

            subjectGrade.setGrade(newGrade);
        });


        yearOfStudySelection.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                YearOfStudy selectedYear = getSelectedYears(newValue);

                updateStudentGradesTableView(selectedYear);
            }
        });

        File imageFile = new File(imagePath);
        Image image = new Image(imageFile.toURI().toString());
        studentImageView.setImage(image);

    }


    public void addImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odaberi sliku");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (Optional.ofNullable(selectedFile).isPresent()) {
            try {
                String destinationDirectory = "src/main/resources/images/";

                FileCopier<File> fileCopier = new FileUtility();
                fileCopier.copyToDirectory(selectedFile, destinationDirectory);

                File imageFile = new File(destinationDirectory + selectedFile.getName());
                Image image = new Image(imageFile.toURI().toString());
                studentImageView.setImage(image);

                imagePath = destinationDirectory + selectedFile.getName();

            } catch (IOException ex) {
                logger.error("Greška prilikom kopiranja slike", ex);
            }
        }
    }


    public void saveMember(ActionEvent event) {
        try{

            ValidationProtocol.validateClubMember(studentNameTextField, studentSurnameTextField, genderSelection,
                    studentEmailTextField,studentGradesTableView, yearOfStudySelection);

            boolean positiveConfirmation = ValidationProtocol.showConfirmationDialog("Potvrda spremanja",
                    "Jeste li sigurni da želite spremiti novog studenta?",
                    "Kliknite Da za potvrdu");

            if (positiveConfirmation){

                Student newStudent = buildNewStudent();
                List<Student> students = new ArrayList<>();
                students.add(newStudent);

                boolean success = DatabaseUtil.saveStudents(students);

                if(success){
                    MathClub currentClub = SessionManager.getInstance().getCurrentClub();

                    Optional<MathClub> updatedMathClub = DatabaseUtil.getMathClub(currentClub.getId());
                    SessionManager.getInstance().setCurrentClub(updatedMathClub.get());

                    User currentUser = SessionManager.getInstance().getCurrentUser();

                    Change change = Change.create(currentUser, "/",
                            "Spremljen novi student: " + newStudent.getName() + " " + newStudent.getSurname(),
                            "Student");

                    ChangesManager.setNewChangesIfChangesNotPresent().add(change);

                    JavaFxProjectApplication.switchScene(ApplicationScreen.ClubMembers);
                    ValidationProtocol.showSuccessAlert("Spremanje novog studenta je bilo uspješno",
                            "Student " + newStudent.getName() + " " + newStudent.getSurname()
                                    + " uspješno se spremio!");

                }else{
                    ValidationProtocol.showErrorAlert("Spremanje novog studenta nije uspjelo",
                            "Student " + newStudent.getName() + " " + newStudent.getSurname() +
                                    " nije uspješno spremljen", "Nažalost studenta nije moguće spremiti");
                }

            }
        }
        catch (ValidationException ex){
            ValidationProtocol.showErrorAlert("Greška pri unosu",
                    "Provjerite ispravnost unesenih podataka",
                    ex.getMessage());
        }

    }

    private Student buildNewStudent() {
        Long studentId = 0L;
        String studentName = studentNameTextField.getText();
        String studentSurname = studentSurnameTextField.getText();
        String studentEmail = studentEmailTextField.getText();

        Student.StudentBuilder studentBuilder = new Student.StudentBuilder(studentId, studentName, studentSurname)
                .email(studentEmail);

        MathClub studentClub = SessionManager.getInstance().getCurrentClub();
        LocalDate joinDate = LocalDate.now();
        ClubMembership studentClubMembership = new ClubMembership(0L,studentClub.getId(), joinDate);
        studentBuilder.clubMembership(studentClubMembership);

        Integer yearOfStudy = getYearOfStudy();
        studentBuilder.yearOfStudy(yearOfStudy);

        Gender gender = getStudentGender();
        studentBuilder.gender(gender.getGender());


        Map<String, Integer> studentGrades = new LinkedHashMap<>();
        for (SubjectGrade subjectAndGrade : studentGradesTableView.getItems()){
            studentGrades.put(subjectAndGrade.getSubject(), Integer.parseInt(subjectAndGrade.getGrade()));
        }
        studentBuilder.grades(studentGrades);

        studentBuilder.picturePath(imagePath);

        return studentBuilder.build();
    }


    private Gender getStudentGender() {
        if (femaleGenderRadioButton.isSelected()){
            return Gender.Female;
        }
        else{
            return Gender.Male;
        }
    }


    private Integer getYearOfStudy() {
        Integer yearOfStudy = 0;

        if (prvaGodinaRadioButton.isSelected()){
            yearOfStudy = 1;
        }else if(drugaGodinaRadioButton.isSelected()){
            yearOfStudy = 2;
        }else if (trecaGodinaRadioButton.isSelected()){
            yearOfStudy = 3;
        }
        return yearOfStudy;
    }


    private YearOfStudy getSelectedYears(Toggle toggle) {

        YearOfStudy selectedYear = YearOfStudy.FIRST_YEAR;

        if ( toggle == drugaGodinaRadioButton) {
            selectedYear = YearOfStudy.SECOND_YEAR;
        }
        else if (toggle == trecaGodinaRadioButton) {
            selectedYear = YearOfStudy.THIRD_YEAR;
        }

        return selectedYear;
    }


    private void updateStudentGradesTableView(YearOfStudy yearOfStudy) {

        studentGradesTableView.getItems().clear();
        for (String subject : yearOfStudy.getCombinedSubjectsUpToYear()) {
            SubjectGrade subjectGrade = new SubjectGrade(subject, "Unesi ocijenu");
            studentGradesTableView.getItems().add(subjectGrade);
        }

    }


}
