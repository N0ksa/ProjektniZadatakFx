package hr.java.project.projectfxapp.utility;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.NamedEntity;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileWriterUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileWriterUtil.class);
    public static void saveStudentsToFile(List<Student> students) {
        File studentsFile = new File(Constants.STUDENTS_FILE_NAME);

        try (PrintWriter pw = new PrintWriter(studentsFile)) {
            for (Student student : students) {
                pw.println(student.getId());
                pw.println(student.getName());
                pw.println(student.getSurname());
                pw.println(student.getEmail());
                pw.println(student.getYearOfStudy());
                
                List<String> studentGradesList = student.getGrades().values()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());

                String studentGradesString  = String.join(", ", studentGradesList);
                pw.println(studentGradesString);

                pw.println(student.getClubMembership().getClubId());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex());
                pw.println(student.getClubMembership().getJoinDate().format(formatter));



                pw.println();
            }


        } catch (IOException ex) {
            String message = "Dogodila se pogreška kod pisanja datoteke - + " + Constants.STUDENTS_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);

        }
    }


    public static Long getNextStudentId() {
        List<Student> students = FileReaderUtil.getStudentsFromFile();

        Long studentId = students.stream().map(NamedEntity::getId).max(Long::compareTo).get();
        return studentId + 1;
    }

    public static void saveMathClubsToFile(List <MathClub> mathClubs){

        File mathClubsFile = new File(Constants.MATH_CLUBS_FILE_NAME);
        try (PrintWriter pw = new PrintWriter(mathClubsFile)) {
            for (MathClub mathClub : mathClubs) {
                pw.println(mathClub.getId());
                pw.println(mathClub.getName());
                pw.println(mathClub.getAddress().getAddressId());

                Set<Student> studentList = mathClub.getStudents();
                String studentListIdString = studentList.stream()
                        .map(student -> student.getId().toString())
                        .collect(Collectors.joining(", "));

                pw.println(studentListIdString);

                pw.println();

            }


        } catch (IOException ex) {
            String message = "Dogodila se pogreška kod pisanja datoteke - + " + Constants.MATH_CLUBS_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);

        }


    }

    public static Long getNextMathClubId() {
        List<MathClub> mathClubs = FileReaderUtil.getMathClubsFromFile(FileReaderUtil.getStudentsFromFile(),
                FileReaderUtil.getAddressesFromFile());

        Long mathClubId = mathClubs.stream().map(NamedEntity::getId).max(Long::compareTo).get();
        return mathClubId + 1;
    }



}
