package hr.java.project.projectfxapp.utility.database;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.City;
import hr.java.project.projectfxapp.enums.Gender;
import hr.java.project.projectfxapp.enums.UserRole;
import hr.java.project.projectfxapp.filter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DataBaseUtilGet {


    private static final Logger logger = LoggerFactory.getLogger(DataBaseUtilGet.class);
    private static final String DATABASE_FILE = "conf/database.properties";

    private static Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));
        String urlDataBase = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        Connection connection = DriverManager.getConnection(urlDataBase,
                username, password);
        return connection;

    }

    public static List<Address> getAddresses() {
        List<Address> addresses = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM ADDRESS";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            mapResultSetToAddressesList(rs, addresses);

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return addresses;

    }


    public static List<MathClub> getMathClubs() {

        List<Student> students = getStudents();
        List<Address> addresses = getAddresses();

        List<MathClub> mathClubs = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM MATH_CLUB ";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            mapResultSetToMathClubsList(rs, addresses, students, mathClubs);

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }


        return mathClubs;

    }

    public static Optional<MathClub> getMathClub(Long mathClubId) {
        List<Address> addresses = getAddresses();
        List<Student> students = getStudents();

        MathClub mathClub = null;

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT * FROM MATH_CLUB WHERE CLUB_ID = %d", mathClubId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {

                String mathClubName = rs.getString("NAME");
                Long addressId = rs.getLong("ADDRESS_ID");

                Address mathClubAddress = addresses.stream()
                        .filter(address -> address.getAddressId().equals(addressId))
                        .findFirst()
                        .get();

                Set<Student> studentsForMathClub = getStudentsForMathClub(mathClubId, students);

                mathClub = new MathClub(mathClubId, mathClubName, mathClubAddress, studentsForMathClub);

            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }
        ;

        return Optional.ofNullable(mathClub);


    }


    public static Set<Student> getStudentsForMathClub(Long mathClubId, List<Student> students) {

        return students.stream().
                filter(student -> student.getClubMembership().getClubId()
                        .equals(mathClubId))
                .collect(Collectors.toSet());

    }


    public static List<Student> getStudents() {

        List<Student> students = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM STUDENT";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            mapResultSetToStudentsList(rs, students);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return students;

    }

    private static Optional<Student> getStudent(Long studentId) {

        Student student = null;

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT * FROM STUDENT WHERE STUDENT_ID = %d;", studentId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {

                Long idOfStudent = rs.getLong("STUDENT_ID");
                String studentName = rs.getString("NAME");
                String studentSurname = rs.getString("SURNAME");
                Student.StudentBuilder studentBuilder = new Student.StudentBuilder(idOfStudent, studentName, studentSurname);

                String studentEmail = rs.getString("EMAIL");
                studentBuilder.email(studentEmail);
                Integer yearOfStudy = rs.getInt("YEAR_OF_STUDY");
                studentBuilder.yearOfStudy(yearOfStudy);


                Map<String, Integer> studentGrades = getStudentGrades(studentId);
                studentBuilder.grades(studentGrades);

                Long membershipId = rs.getLong("CLUB_MEMBERSHIP_ID");
                ClubMembership clubMembership = getStudentClubMembership(membershipId);
                studentBuilder.clubMembership(clubMembership);

                String picturePath = rs.getString("PICTURE_PATH");
                studentBuilder.picturePath(picturePath);

                String gender = rs.getString("GENDER");
                studentBuilder.gender(Gender.getGenderFromString(gender).getGender());

                student = studentBuilder.build();

            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return Optional.ofNullable(student);
    }

    private static ClubMembership getStudentClubMembership(Long membershipId) {

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT * FROM CLUB_MEMBERSHIP WHERE CLUB_MEMBERSHIP_ID = %d", membershipId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {

                Long clubId = rs.getLong("CLUB_ID");
                LocalDate joinDate = rs.getDate("JOIN_DATE").toLocalDate();

                return new ClubMembership(membershipId, clubId, joinDate);

            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return null;
    }

    private static Map<String, Integer> getStudentGrades(Long studentId) {

        Map<String, Integer> studentGrades = new HashMap<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT SUBJECT_NAME, GRADE FROM STUDENT_GRADES WHERE STUDENT_ID = %d", studentId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                String subjectName = rs.getString("SUBJECT_NAME");
                Integer subjectGrade = rs.getInt("GRADE");

                studentGrades.put(subjectName, subjectGrade);

            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }


        return studentGrades;

    }

    public static List<Competition> getCompetitions() {

        List<Competition> competitions = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM COMPETITION";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            mapResultSetToCompetitionsList(rs, competitions);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return competitions;

    }


    private static Set<CompetitionResult> getCompetitionResults(Long competitionId) {

        Set<CompetitionResult> competitionResults = new HashSet<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT * FROM COMPETITION_RESULTS WHERE COMPETITION_ID = %d", competitionId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {

                Long participandId = rs.getLong("STUDENT_ID");
                Optional<Student> competitionParticipant = getStudent(participandId);

                BigDecimal competitionScore = rs.getBigDecimal("SCORE");

                competitionParticipant.ifPresent(student -> {
                    CompetitionResult competitionResult = new CompetitionResult(student, competitionScore);
                    competitionResults.add(competitionResult);
                });

            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return competitionResults;
    }


    private static Optional<Address> getAddress(Long addressId) {

        Address address = null;

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT * FROM ADDRESS WHERE ADDRESS_ID = %d", addressId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                String streetName = rs.getString("STREET");
                String houseNumber = rs.getString("HOUSE_NUMBER");
                City city = City.getCityFromStringName(rs.getString("CITY"));

                Address.AdressBuilder addressBuilder = new Address.AdressBuilder(city)
                        .setHouseNumber(houseNumber)
                        .setId(addressId)
                        .setStreet(streetName);

                address = addressBuilder.build();
            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }


        return Optional.ofNullable(address);

    }

    public static List<MathProject> getProjects() {

        List<MathProject> mathProjects = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM MATH_PROJECT ";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            mapResultSetToMathProjectsList(rs, mathProjects);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return mathProjects;

    }


    private static Map<MathClub, List<Student>> getProjectCollaborators(Long projectId) {
        Map<MathClub, List<Student>> mathProjectCollaborators = new HashMap<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT * FROM PROJECT_COLLABORATORS WHERE PROJECT_ID = %d", projectId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();


            while (rs.next()) {
                Long mathClubCollaboratorId = rs.getLong("MATH_CLUB_ID");
                Optional<MathClub> mathClubCollaborator = getMathClub(mathClubCollaboratorId);
                Long studentCollaboratorId = rs.getLong("STUDENT_ID");
                Optional<Student> studentCollaborator = getStudent(studentCollaboratorId);


                if (mathClubCollaborator.isPresent() && studentCollaborator.isPresent()) {
                    mathProjectCollaborators
                            .computeIfAbsent(mathClubCollaborator.get(), k -> new ArrayList<>())
                            .add(studentCollaborator.get());
                }

            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return mathProjectCollaborators;
    }

    public static List<Student> getStudentsByFilter(StudentFilter studentFilter) {
        List<Student> filteredStudents = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM STUDENT WHERE 1=1";

            if (!studentFilter.getName().isEmpty()) {
                baseSqlQuery += " AND LOWER(NAME) LIKE LOWER(?)";
                queryParams.put(paramOrdinalNumber, studentFilter.getName() + "%");
                paramOrdinalNumber++;
            }

            if (!studentFilter.getSurname().isEmpty()) {
                baseSqlQuery += " AND LOWER(SURNAME) LIKE LOWER(?)";
                queryParams.put(paramOrdinalNumber, studentFilter.getSurname() + "%");
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                pstmt.setString(paramNumber, (String) queryParams.get(paramNumber));
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();

            mapResultSetToStudentsList(rs, filteredStudents);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return filteredStudents;

    }

    public static List<MathClub> getMathClubsByFilter(MathClubFilter mathClubFilter) {

        List<Student> students = getStudents();
        List<Address> addresses = getAddresses();

        List<MathClub> filteredMathClubs = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM MATH_CLUB WHERE 1=1";

            if (!mathClubFilter.getName().isEmpty()) {
                baseSqlQuery += " AND LOWER(NAME) LIKE LOWER(?)";
                queryParams.put(paramOrdinalNumber, mathClubFilter.getName() + "%");
                paramOrdinalNumber++;
            }


            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                pstmt.setString(paramNumber, (String) queryParams.get(paramNumber));
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();

            mapResultSetToMathClubsList(rs, addresses, students, filteredMathClubs);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return filteredMathClubs;

    }

    public static List<MathProject> getMathProjectsByFilter(MathProjectFilter mathProjectFilter) {

        List<MathProject> filteredMathProjects = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM MATH_PROJECT WHERE 1=1";

            if (!mathProjectFilter.getName().isEmpty()) {
                baseSqlQuery += " AND LOWER(NAME) LIKE LOWER(?)";
                queryParams.put(paramOrdinalNumber, mathProjectFilter.getName() + "%");
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                pstmt.setString(paramNumber, (String) queryParams.get(paramNumber));
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();

            mapResultSetToMathProjectsList(rs, filteredMathProjects);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return filteredMathProjects;

    }


    public static List<Competition> getCompetitionsByFilter(CompetitionFilter competitionFilter) {

        List<Competition> filteredCompetitions = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM COMPETITION WHERE 1=1";

            if (!competitionFilter.getName().isEmpty()) {
                baseSqlQuery += " AND LOWER(NAME) LIKE LOWER(?)";
                queryParams.put(paramOrdinalNumber, competitionFilter.getName() + "%");
                paramOrdinalNumber++;
            }

            if (Optional.ofNullable(competitionFilter.getDateOfCompetition()).isPresent()) {
                baseSqlQuery += " AND DATE_OF_COMPETITION = ?";
                queryParams.put(paramOrdinalNumber, Date.valueOf(competitionFilter.getDateOfCompetition()));
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Date dqp) {
                    pstmt.setDate(paramNumber, dqp);
                }
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();

            mapResultSetToCompetitionsList(rs, filteredCompetitions);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return filteredCompetitions;

    }

    private static void mapResultSetToMathClubsList(ResultSet rs, List<Address> addresses, List<Student> students,
                                                    List<MathClub> filteredMathClubs) throws SQLException {

        while (rs.next()) {

            Long mathClubId = rs.getLong("CLUB_ID");
            String mathClubName = rs.getString("NAME");
            Long addressId = rs.getLong("ADDRESS_ID");

            Address mathClubAddress = addresses.stream()
                    .filter(address -> address.getAddressId().equals(addressId))
                    .findFirst()
                    .get();

            Set<Student> studentsForMathClub = getStudentsForMathClub(mathClubId, students);

            MathClub filteredMathClub = new MathClub(mathClubId, mathClubName, mathClubAddress, studentsForMathClub);

            filteredMathClubs.add(filteredMathClub);

        }
    }


    private static void mapResultSetToStudentsList(ResultSet rs, List<Student> filteredStudents) throws SQLException {

        while (rs.next()) {

            Long studentId = rs.getLong("STUDENT_ID");
            String studentName = rs.getString("NAME");
            String studentSurname = rs.getString("SURNAME");

            Student.StudentBuilder studentBuilder = new Student.StudentBuilder(studentId, studentName, studentSurname);

            String studentEmail = rs.getString("EMAIL");
            studentBuilder.email(studentEmail);

            Integer yearOfStudy = rs.getInt("YEAR_OF_STUDY");
            studentBuilder.yearOfStudy(yearOfStudy);

            Map<String, Integer> studentGrades = getStudentGrades(studentId);
            studentBuilder.grades(studentGrades);

            Long membershipId = rs.getLong("CLUB_MEMBERSHIP_ID");
            ClubMembership clubMembership = getStudentClubMembership(membershipId);
            studentBuilder.clubMembership(clubMembership);

            String gender = rs.getString("GENDER");
            studentBuilder.gender(Gender.getGenderFromString(gender).getGender());

            String filePath = rs.getString("PICTURE_PATH");
            studentBuilder.picturePath(filePath);

            Student filteredStudent = studentBuilder.build();

            filteredStudents.add(filteredStudent);

        }
    }

    private static void mapResultSetToMathProjectsList(ResultSet rs, List<MathProject> mathProjects) throws SQLException {
        while (rs.next()) {
            Long projectId = rs.getLong("PROJECT_ID");
            String projectName = rs.getString("NAME");
            String projectDescription = rs.getString("DESCRIPTION");
            LocalDate startDate = rs.getDate("START_DATE").toLocalDate();


            Date endDate = rs.getDate("END_DATE");

            LocalDate endDateOfProject = null;
            if (Optional.ofNullable(endDate).isPresent()) {
                endDateOfProject = endDate.toLocalDate();
            }

            Long mathClubOrganizerId = rs.getLong("ORGANIZER_ID");
            Long addressId = rs.getLong("ADDRESS_ID");


            Address projectAddress = getAddress(addressId).get();
            Map<MathClub, List<Student>> projectCollaborators = getProjectCollaborators(projectId);
            MathClub organizer = getMathClub(mathClubOrganizerId).get();

            MathProject newMathProject = new MathProject(projectId, organizer, startDate, projectAddress,
                    projectName, projectDescription, projectCollaborators);

            newMathProject.setEndDate(endDateOfProject);
            mathProjects.add(newMathProject);


        }
    }


    private static void mapResultSetToCompetitionsList(ResultSet rs, List<Competition> competitions) throws SQLException {
        while (rs.next()) {
            Long competitionId = rs.getLong("COMPETITION_ID");
            String competitionName = rs.getString("NAME");
            String competitionDescription = rs.getString("DESCRIPTION");

            Long addressId = rs.getLong("ADDRESS_ID");
            Optional<Address> competitionAddress = getAddress(addressId);

            Time timeOfCompetition = rs.getTime("TIME_OF_COMPETITION");
            LocalDate dateOfCompetition = rs.getDate("DATE_OF_COMPETITION").toLocalDate();


            LocalDateTime dateAndTimeOfCompetition = dateOfCompetition.atTime(timeOfCompetition.toLocalTime());

            String auditoriumBuildingName = rs.getString("AUDITORIUM_BUILDING");
            String auditoriumHallName = rs.getString("AUDITORIUM_HALL");
            Auditorium auditorium = new Auditorium(auditoriumBuildingName, auditoriumHallName);

            Set<CompetitionResult> competitionResults = getCompetitionResults(competitionId);

            Long organizerId = rs.getLong("ORGANIZER_ID");

            MathClub organizer = getMathClub(organizerId).get();


            competitionAddress.ifPresent(address -> {

                Competition newCompetition = new Competition(competitionId, organizer, competitionName, competitionDescription
                        , address, auditorium, dateAndTimeOfCompetition, competitionResults);

                competitions.add(newCompetition);
            });


        }
    }


    public static Optional<User> getCurrentUser(String enteredUsername, String enteredPassword) {
        User currentUser = null;

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT * FROM USERS WHERE USERNAME = '%s' AND PASSWORD = '%s'",
                    enteredUsername, enteredPassword);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                Long mathClubId = rs.getLong("MATH_CLUB_ID");
                String role = rs.getString("ROLE");
                String imagePath = rs.getString("PICTURE_PATH");

                currentUser = new User(username, password, UserRole.getRoleByName(role), mathClubId, new Picture(imagePath));
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }


        return Optional.ofNullable(currentUser);

    }


    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM USERS";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                Long mathClubId = rs.getLong("MATH_CLUB_ID");
                String role = rs.getString("ROLE");
                String picturePath = rs.getString("PICTURE_PATH");

                User user = new User(username, password, UserRole.getRoleByName(role), mathClubId, new Picture(picturePath));
                users.add(user);
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }


        return users;

    }

    public static Optional<User> getUser(Long mathClubId) {

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT * FROM USERS WHERE MATH_CLUB_ID = %d", mathClubId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                String role = rs.getString("ROLE");
                String picturePath = rs.getString("PICTURE_PATH");

                User user = new User(username, password, UserRole.getRoleByName(role), mathClubId, new Picture(picturePath));
                return Optional.of(user);
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }


        return Optional.empty();

    }

    public static List<Address> getAddressesByFilter(AddressFilter addressFilter) {
        List<Address> filteredAddresses = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM ADDRESS WHERE 1=1";

            if (!addressFilter.getStreetName().isEmpty()) {
                baseSqlQuery += " AND LOWER(STREET) LIKE LOWER(?)";
                queryParams.put(paramOrdinalNumber, addressFilter.getStreetName() + "%");
                paramOrdinalNumber++;
            }

            if (!addressFilter.getHouseNumber().isEmpty()) {
                baseSqlQuery += " AND LOWER(HOUSE_NUMBER) LIKE LOWER(?)";
                queryParams.put(paramOrdinalNumber, addressFilter.getHouseNumber() + "%");
                paramOrdinalNumber++;
            }

            if (Optional.ofNullable(addressFilter.getCity()).isPresent()) {
                baseSqlQuery += " AND LOWER(CITY) LIKE LOWER(?)";
                queryParams.put(paramOrdinalNumber, addressFilter.getCity().getName() + "%");
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                pstmt.setString(paramNumber, (String) queryParams.get(paramNumber));
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            mapResultSetToAddressesList(rs, filteredAddresses);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }


        return filteredAddresses;
    }

    private static void mapResultSetToAddressesList(ResultSet rs, List<Address> filteredAddresses) throws SQLException {
        while (rs.next()) {

            Long addressId = rs.getLong("ADDRESS_ID");
            String street = rs.getString("STREET");
            String houseNumber = rs.getString("HOUSE_NUMBER");
            String cityName = rs.getString("CITY");


            City city = City.getCityFromStringName(cityName);
            Address.AdressBuilder addressBuilder = new Address.AdressBuilder(city).setAddressId(addressId)
                    .setStreet(street).setHouseNumber(houseNumber);

            Address filteredAddress = addressBuilder.build();

            filteredAddresses.add(filteredAddress);

        }
    }


}
