package hr.java.project.projectfxapp.controllers;

import hr.java.project.projectfxapp.entities.Address;
import hr.java.project.projectfxapp.entities.ClubMembership;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.utility.FileReaderUtil;
import hr.java.project.projectfxapp.utility.FileWriterUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AddNewMathClubController {

    @FXML
    private DatePicker studentJoinDateDatePicker;
    @FXML
    private ComboBox<Address> clubAddressComboBox;
    @FXML
    private TextField clubNameTextField;
    @FXML
    private ListView<Student> availableStudentListView;


    public void initialize(){
        List<Student> studentsList = FileReaderUtil.getStudentsFromFile();
        List<Student> availableStudentsList = studentsList.stream().filter(student -> student.getClubMembership().getClubId().equals(0L)).toList();

        ObservableList<Student> availableStudentsObservableList = FXCollections.observableList(availableStudentsList);
        availableStudentListView.setItems(availableStudentsObservableList);

        List<Address> availableAddresses = FileReaderUtil.getAddressesFromFile();
        ObservableList<Address> availableAddressesObservableList = FXCollections.observableList(availableAddresses);
        clubAddressComboBox.setItems(availableAddressesObservableList);
    }

    public void saveMathClubs(ActionEvent actionEvent) {
        Long mathClubId = FileWriterUtil.getNextMathClubId();
        String clubName = clubNameTextField.getText();
        Address clubAddress = clubAddressComboBox.getValue();
        Set<Student> clubMembers = new HashSet<>(availableStudentListView.getItems());
        LocalDate studentsJoinDate = studentJoinDateDatePicker.getValue();

        clubMembers.forEach(member -> member.setClubMembership(new ClubMembership(mathClubId, studentsJoinDate)));

        MathClub newMathClub = new MathClub(mathClubId, clubName, clubAddress, clubMembers);

        List<MathClub> mathClubs = FileReaderUtil.getMathClubsFromFile(FileReaderUtil.getStudentsFromFile(),
                FileReaderUtil.getAddressesFromFile());

        mathClubs.add(newMathClub);

        FileWriterUtil.saveMathClubsToFile(mathClubs);

        List <Student> studentsToUpdate = FileReaderUtil.getStudentsFromFile();
        studentsToUpdate.forEach(student -> {
            for (Student member : clubMembers){
                if (student.getId().equals(member.getId())){
                    student.getClubMembership().setClubId(mathClubId);
                }
            }
        });
        FileWriterUtil.saveStudentsToFile(studentsToUpdate);


    }
}