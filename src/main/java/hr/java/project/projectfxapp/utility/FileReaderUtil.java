package hr.java.project.projectfxapp.utility;


import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.enums.YearOfStudy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Pomoćna klasa koja služi za učitavanje podataka iz datoteka.
 */
public class FileReaderUtil {

    private static final String STUDENTS_FILE_NAME = "dat/students.txt";
    private static final String PROFESSORS_FILE_NAME = "dat/professors.txt";
    private static final String ADDRESSES_FILE_NAME = "dat/addresses.txt";
    private static final String MATH_CLUBS_FILE_NAME = "dat/math-clubs.txt";
    private static final String MATH_PROJECTS_FILE_NAME = "dat/math-projects.txt";
    private static final String MATH_COMPETITIONS_FILE_NAME = "dat/competitions.txt";

    private static final Logger logger = LoggerFactory.getLogger(FileReaderUtil.class);


    /**
     * Čita studente iz datoteke i vraća ih kao listu objekata klase Student.
     *
     * @return Lista studenata učitanih iz datoteke.
     * @throws IOException Ako dođe do pogreške prilikom čitanja datoteke.
     */
    public static List<Student> getStudentsFromFile() {
        List<Student> students = new ArrayList<>();
        File studentsFile = new File(STUDENTS_FILE_NAME);

        try (BufferedReader reader = new BufferedReader(new FileReader(studentsFile))) {

            String line;
            while ((Optional.ofNullable(line = reader.readLine()).isPresent())) {

                String studentName = line;
                String studentSurname = reader.readLine();
                Long studentId = Long.parseLong(reader.readLine());
                String studentWebAddress = reader.readLine();

                Integer studentYearOfStudy = Integer.parseInt(reader.readLine());
                List<Integer> studentGrades = Stream.of(reader.readLine().split(","))
                        .map(grade -> Integer.parseInt(grade))
                        .collect(Collectors.toList());

                Map<String, Integer> grades = new HashMap<>();
                List<String> subjects = new ArrayList<>();

                switch (studentYearOfStudy) {
                    case 1 -> subjects = YearOfStudy.FIRST_YEAR.getAvailableSubjects();
                    case 2 -> subjects = YearOfStudy.SECOND_YEAR.getAvailableSubjects();
                    case 3 -> subjects = YearOfStudy.THIRD_YEAR.getAvailableSubjects();
                }

                for (int i = 0; i < subjects.size(); i++) {
                    grades.put(subjects.get(i), studentGrades.get(i));
                }


                String memberId;
                LocalDate joinDate;

                memberId = reader.readLine();
                joinDate = LocalDate.parse(reader.readLine(),
                        DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex()));
                ClubMembership membership = new ClubMembership(memberId, joinDate);

                reader.readLine();



                students.add(new Student(studentName, studentSurname, studentId, studentWebAddress,
                        studentYearOfStudy, grades, membership));

            }

        } catch (IOException ex) {
            String message = "Dogodila se pogreška kod čitanja datoteke - + " + STUDENTS_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);
        }

        return students;

    }


    /**
     * Čita artikle iz datoteke i vraća ih kao listu objekata klase Item.
     *
     * @return Lista artikala učitanih iz datoteke.
     * @throws IOException Ako dođe do pogreške prilikom čitanja datoteke.
     */
    public static List<Professor> getProfessorsFromFile() {
        File itemsFile = new File(PROFESSORS_FILE_NAME);
        List<Professor> professors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(itemsFile))) {
            String line;
            while ((Optional.ofNullable(line = reader.readLine()).isPresent())) {

                String professorName = line;
                String professorSurname = reader.readLine();
                Long professorId = Long.parseLong(reader.readLine());
                String professorWebAddress = reader.readLine();

                reader.readLine();

                professors.add(new Professor(professorId, professorName, professorSurname, professorWebAddress));
            }

        } catch (IOException ex) {
            String message = "Dogodila se pogreška kod čitanja datoteke - + " + PROFESSORS_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);
        }

        return professors;
    }


    /**
     * Čita adrese iz datoteke i vraća ih kao listu objekata klase Address.
     *
     * @return Lista adresa učitanih iz datoteke.
     */
    public static List<Address> getAddressesFromFile() {

        File categoriesFile = new File(ADDRESSES_FILE_NAME);
        List<Address> addresses = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(categoriesFile))) {

            String line;
            while ((Optional.ofNullable(line = reader.readLine()).isPresent())) {

                Long addressId = Long.parseLong(line);
                String streetName = reader.readLine();
                String houseNumber = reader.readLine();
                String cityName = reader.readLine();

                City city = City.getCityFromStringName(cityName);

                Address.AdressBuilder adressBuilder = new Address.AdressBuilder(city).setId(addressId)
                        .setStreet(streetName).setHouseNumber(houseNumber);


                addresses.add(adressBuilder.build());


            }

        } catch (IOException ex) {
            String message = "Dogodila se pogreška kod čitanja datoteke - + " + ADDRESSES_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);
        }

        return addresses;
    }

    /**
     * Čita studentske matematičke klubove iz datoteke i vraća ih kao listu objekata klase MathClub.
     * @return Lista matematičkih klubova učitanih iz datoteke.
     */

    public static List<MathClub> getMathClubsFromFile(List <Student> students, List <Address> addresses) {
        List<MathClub> mathClubs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(MATH_CLUBS_FILE_NAME))) {

            String line;
            while ((Optional.ofNullable(line = reader.readLine()).isPresent())) {

                Long mathClubId = Long.parseLong(line);
                String mathClubName = reader.readLine();
                Long addressId = Long.parseLong(reader.readLine());
                Optional <Address> mathClubAddress = addresses.stream()
                        .filter(address -> address.getAddressId().compareTo(addressId) == 0)
                        .findFirst();

                List<String> membersIdString = Arrays.asList(reader.readLine().split(","))
                        .stream()
                        .map(String::trim)
                        .collect(Collectors.toList());

                List <Long> membersId = membersIdString.stream()
                        .map(stringId -> Long.parseLong(stringId))
                        .collect(Collectors.toList());

                Set <Student> mathClubMembers = membersId.stream()
                        .map(id -> findStudentById(id, students))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());

                reader.readLine();

                mathClubAddress.ifPresent(address -> mathClubs.add(new MathClub(mathClubId, mathClubName, address, mathClubMembers)));

            }


        } catch (IOException ex) {
            String message = "Dogodila se pogreška kod čitanja datoteke - + " + MATH_CLUBS_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);
        }

        return mathClubs;
    }



    public static List<Competition> getMathCompetitionsFromFile(List<Student> students, List<Address> addresses){
        List<Competition> mathCompetitions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MATH_COMPETITIONS_FILE_NAME))) {

            String line;

            while ((Optional.ofNullable(line = reader.readLine()).isPresent())) {

                Long competitionId = Long.parseLong(line);
                String competitionName = reader.readLine();
                String competitionDescription = reader.readLine();

                Long addressId = Long.parseLong(reader.readLine());
                Optional<Address> competitionAddress = addresses.stream()
                        .filter(address -> address.getAddressId().compareTo(addressId) == 0)
                        .findFirst();

                LocalDateTime competitionTime = LocalDateTime.parse(reader.readLine(),
                        DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_TIME_REGEX.getRegex()));

                Auditorium auditorium = new Auditorium(reader.readLine(), reader.readLine());

                List<String> competitionResultsString = Arrays.asList(reader.readLine().split(","))
                        .stream().map(result -> result.trim()).collect(Collectors.toList());

                Set<CompetitionResult> competitionResults = competitionResultsString.stream()
                        .map(competitionString ->{
                            List<String> individualResults = Arrays.asList(competitionString.split("-"));

                            Long studentId = Long.parseLong(individualResults.get(0));
                            BigDecimal result = new BigDecimal(individualResults.get(1));

                            Optional<Student> participant = findStudentById(studentId, students);

                            return new CompetitionResult(participant.get(), result);

                })
                        .collect(Collectors.toSet());


                reader.readLine();

                competitionAddress.ifPresent(address -> mathCompetitions.add(new Competition(competitionId, competitionName,
                        competitionDescription, address, auditorium, competitionTime, competitionResults)));


            }


        }
        catch (IOException ex) {
            String message = "Dogodila se pogreška kod čitanja datoteke - + " + MATH_COMPETITIONS_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);
        }

        return mathCompetitions;
    }


    public static List<MathProject> getMathProjectsFromFile(List<MathClub> studentClubs, List<Student> students){

        List<MathProject> mathProjects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MATH_PROJECTS_FILE_NAME))) {

            String line;
            while ((Optional.ofNullable(line = reader.readLine()).isPresent())) {

                Long projectId = Long.parseLong(line);
                String projectName = reader.readLine();
                String projectDescription = reader.readLine();

                List <String> collaboratorsByStudentClubString = Arrays.asList(reader.readLine().split("/"));

                Map<MathClub, List<Student>> collaborators = collaboratorsByStudentClubString.stream()
                        .map(collaborator -> {
                            List <String> clubStudentString = Arrays.asList(collaborator.split("-"));
                            Long mathClubId = Long.parseLong(clubStudentString.get(0));
                            List <Long> studentsId = Arrays.stream(clubStudentString.get(1)
                                    .split(","))
                                    .map(studentString -> studentString.trim())
                                    .map(studentString -> Long.parseLong(studentString))
                                    .collect(Collectors.toList());

                            Optional<MathClub> mathClubOptional = findMathClubById(mathClubId, studentClubs);
                            List<Student> studentList = studentsId.stream()
                                    .map(studentId -> findStudentById(studentId, students))
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .collect(Collectors.toList());


                            return Map.entry(mathClubOptional.get(), studentList);


                })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                reader.readLine();

                mathProjects.add(new MathProject(projectId, projectName, projectDescription, collaborators));

            }
        }catch (IOException ex) {
            String message = "Dogodila se pogreška kod čitanja datoteke - + " + MATH_PROJECTS_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);
        }

        return mathProjects;
    }


    /**
     * Pronalazi studenta s određenim identifikacijskim brojem u zadanoj listi.
     * @param id Identifikacijski broj studenta kojeg treba pronaći.
     * @param students Lista studenata u kojoj treba tražiti.
     * @return {@code Optional} koji sadrži studenta ili prazan {@code Optional} ako student nije pronađen.
     */
    public static Optional<Student> findStudentById(Long id, List<Student> students){
        return students.stream()
                .filter(student -> student.getId().compareTo(id) == 0)
                .findFirst();
    }


    /**
     * Pronalazi matematički klub s određenim identifikacijskim brojem u zadanoj listi.
     * @param id Identifikacijski broj matematički kluba kojeg treba pronaći.
     * @param mathClubs Lista matematičkih klubova u kojoj treba tražiti.
     * @return {@code Optional} koji sadrži matematički klub ili prazan {@code Optional} ako matematički klub nije pronađen.
     */
    public static Optional<MathClub> findMathClubById(Long id, List<MathClub> mathClubs){
        return mathClubs.stream()
                .filter(mathClub -> mathClub.getId().compareTo(id) == 0)
                .findFirst();
    }

}