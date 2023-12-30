package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.JavaFxProjectApplication;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.enums.ApplicationScreen;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.utility.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ClubMembersController {
    @FXML
    private TableColumn<Student, String> NameAndSurnameTableColumn;

    @FXML
    private TextField filterMembers;

    @FXML
    private TableView<Student> leaderBoardTableView;

    @FXML
    private TableColumn<Student, String> memberAverageGradeTableColumn;

    @FXML
    private TableColumn<Student, String> memberEmailTableColumn;

    @FXML
    private TableColumn<Student, String> memberJoinDateTableColumn;

    @FXML
    private TableColumn<Student, String> memberNameTableColumn;

    @FXML
    private TableColumn<Student, String> memberSurnameTableColumn;

    @FXML
    private TableView<Student> memberTableView;

    @FXML
    private TableColumn<Student, String> memberYearOfStudyTableColumn;

    @FXML
    private TableColumn<Student, String> overallScoreTableColumn;

    @FXML
    private TableColumn<Student, String> leaderBoardTableColumn;



   public void initialize(){

       MathClub currentClub = SessionManager.getInstance().getCurrentClub();
       List<Student> clubMembers = currentClub.getStudents().stream().toList();

       initializeMemberTableView(FXCollections.observableList(clubMembers));
       initializeLeaderBoardTableView(FXCollections.observableList(clubMembers));


   }

    private void initializeLeaderBoardTableView(ObservableList<Student> students) {
        leaderBoardTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(String.valueOf(students.indexOf(param.getValue()) +1));
            }
        });

        NameAndSurnameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName() + " " + param.getValue().getSurname());
            }
        });


        overallScoreTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(String.valueOf(param.getValue().calculateAverageGrade().toString()));
            }
        });

        leaderBoardTableView.setItems(students);
    }


    private void initializeMemberTableView(ObservableList<Student> clubMembers) {
        memberNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        memberSurnameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getSurname());
            }
        });

        memberJoinDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                LocalDate joinDate = param.getValue().getClubMembership().getJoinDate();

                if (Optional.ofNullable(joinDate).isPresent()){

                    return new ReadOnlyStringWrapper(param.getValue().getClubMembership().getJoinDate()
                            .format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex())));
                }else{
                    return new ReadOnlyStringWrapper("/");
                }

            }
        });

        memberAverageGradeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {


                return new ReadOnlyStringWrapper(String.format("%.1f", param.getValue().calculateAverageGrade()));
            }
        });


        memberEmailTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getEmail());
            }
        });

        memberYearOfStudyTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getYearOfStudy().toString());
            }
        });


        memberTableView.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Student selectedStudent = row.getItem();
                    handleMemberDoubleClick(selectedStudent);
                }
            });
            return row;
        });


        memberTableView.setItems(clubMembers);

    }


    private void handleMemberDoubleClick(Student selectedStudent) {
        JavaFxProjectApplication.showPopup(ApplicationScreen.MemberCard);
    }




}
