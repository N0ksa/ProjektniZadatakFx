package hr.java.project.projectfxapp.utility;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

                if(Optional.ofNullable(student.getClubMembership().getJoinDate()).isPresent()){
                    pw.println(student.getClubMembership().getJoinDate().format(formatter));
                }else{
                    pw.println();
                }



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


    public static void saveCompetitionsToFile(List <Competition> competitions){

        File competitionsFile = new File(Constants.COMPETITIONS_FILE_NAME);
        try (PrintWriter pw = new PrintWriter(competitionsFile)) {
            for (Competition competition : competitions) {

                pw.println(competition.getId());
                pw.println(competition.getName());
                pw.println(competition.getDescription());
                pw.println(competition.getAddress().getAddressId());
                pw.println(competition.getTimeOfCompetition()
                        .format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_TIME_REGEX.getRegex())));

                pw.println(competition.getAuditorium().building());
                pw.println(competition.getAuditorium().building());

                StringBuilder competitionResultsStringBuilder = new StringBuilder();
                for (CompetitionResult result : competition.getCompetitionResults()) {
                    competitionResultsStringBuilder.append(result.participant().getId())
                            .append("-")
                            .append(result.score().toString())
                            .append(", ");
                }

                if (!competitionResultsStringBuilder.isEmpty()) {
                    competitionResultsStringBuilder.setLength(competitionResultsStringBuilder.length() - 2);
                }
                pw.println(competitionResultsStringBuilder);

                pw.println();


            }


        } catch (IOException ex) {
            String message = "Dogodila se pogreška kod pisanja datoteke - + " + Constants.COMPETITIONS_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);

        }


    }


    public static Long getNextCompetitionId() {
        List<Competition> competitions = FileReaderUtil.getMathCompetitionsFromFile(FileReaderUtil.getStudentsFromFile(),
                FileReaderUtil.getAddressesFromFile());

        Long competitionId= competitions.stream().map(NamedEntity::getId).max(Long::compareTo).get();
        return competitionId + 1;
    }

    public static void saveProjectsToFile(List <MathProject> projects){

        File projectsFile = new File(Constants.MATH_PROJECTS_FILE_NAME);
        try (PrintWriter pw = new PrintWriter(projectsFile)) {
            for (MathProject project : projects) {

                pw.println(project.getId());
                pw.println(project.getName());
                pw.println(project.getDescription());

                StringBuilder resultBuilder = new StringBuilder();
                for (Map.Entry<MathClub, List<Student>> entry : project.getCollaborators().entrySet()) {
                    Long mathClubId = entry.getKey().getId();

                    List<Long> studentIds = entry.getValue().stream().map(NamedEntity::getId).toList();

                    resultBuilder.append(mathClubId).append("-");

                    for (int i = 0; i < studentIds.size(); i++) {
                        resultBuilder.append(studentIds.get(i));
                        if (i < studentIds.size() - 1) {
                            resultBuilder.append(",");
                        }
                    }

                    resultBuilder.append("/");
                }


                if (!resultBuilder.isEmpty()) {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }

                pw.println(resultBuilder);

                pw.println();

            }


        } catch (IOException ex) {
            String message = "Dogodila se pogreška kod pisanja datoteke - + " + Constants.MATH_PROJECTS_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);

        }


    }


    public static Long getNextProjectId() {
        List<MathProject> projects = FileReaderUtil.getMathProjectsFromFile
                (FileReaderUtil.getMathClubsFromFile(FileReaderUtil.getStudentsFromFile(),FileReaderUtil.getAddressesFromFile()),
                        FileReaderUtil.getStudentsFromFile());

        Long mathProjectId= projects.stream().map(NamedEntity::getId).max(Long::compareTo).get();
        return mathProjectId + 1;
    }




}
