package hr.java.project.projectfxapp.utility.database;

import hr.java.project.projectfxapp.constants.Constants;
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

public class DatabaseUtil {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    private static Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(Constants.DATABASE_PATH));
        String urlDataBase = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(urlDataBase,
                username, password);

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

    private static List<Address> getAddresses(Connection connection) {
        List<Address> addresses = new ArrayList<>();

        try  {
            String sqlQuery = "SELECT * FROM ADDRESS";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            mapResultSetToAddressesList(rs, addresses);

        } catch (SQLException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return addresses;
    }

    public static List<MathClub> getMathClubs() {
        List<MathClub> mathClubs = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM MATH_CLUB ";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            List<Student> students = getStudents(connection);
            List<Address> addresses = getAddresses(connection);

            mapResultSetToMathClubsList(rs, addresses, students, mathClubs);

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return mathClubs;
    }

    private static Optional<MathClub> getMathClub(Long mathClubId, Connection connection) {
        List<Address> addresses = getAddresses(connection);
        List<Student> students = getStudents(connection);

        MathClub mathClub = null;

        try {
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

        } catch (SQLException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return Optional.ofNullable(mathClub);

    }

    public static Optional<MathClub> getMathClub(Long mathClubId) {

        MathClub mathClub = null;

        try (Connection connection = connectToDatabase()){
            String sqlQuery = String.format("SELECT * FROM MATH_CLUB WHERE CLUB_ID = %d", mathClubId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            List<Address> addresses = getAddresses(connection);
            List<Student> students = getStudents(connection);

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

        return Optional.ofNullable(mathClub);

    }


    private static Set<Student> getStudentsForMathClub(Long mathClubId, List<Student> students) {
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

            mapResultSetToStudentsList(rs, students, connection);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return students;

    }

    private static List<Student> getStudents(Connection connection) {

        List<Student> students = new ArrayList<>();

        try  {
            String sqlQuery = "SELECT * FROM STUDENT";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            mapResultSetToStudentsList(rs, students, connection);


        } catch (SQLException ex) {
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


                Map<String, Integer> studentGrades = getStudentGrades(studentId, connection);
                studentBuilder.grades(studentGrades);

                Long membershipId = rs.getLong("CLUB_MEMBERSHIP_ID");
                ClubMembership clubMembership = getStudentClubMembership(membershipId, connection);
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

    private static ClubMembership getStudentClubMembership(Long membershipId, Connection connection) {

        try {
            String sqlQuery = String.format("SELECT * FROM CLUB_MEMBERSHIP WHERE CLUB_MEMBERSHIP_ID = %d", membershipId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                Long clubId = rs.getLong("CLUB_ID");
                LocalDate joinDate = rs.getDate("JOIN_DATE").toLocalDate();

                return new ClubMembership(membershipId, clubId, joinDate);

            }


        } catch (SQLException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return null;
    }

    private static Map<String, Integer> getStudentGrades(Long studentId, Connection connection) {
        Map<String, Integer> studentGrades = new HashMap<>();

        try{
            String sqlQuery = String.format("SELECT SUBJECT_NAME, GRADE FROM STUDENT_GRADES WHERE STUDENT_ID = %d", studentId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                String subjectName = rs.getString("SUBJECT_NAME");
                Integer subjectGrade = rs.getInt("GRADE");

                studentGrades.put(subjectName, subjectGrade);

            }


        } catch (SQLException ex) {
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

            mapResultSetToCompetitionsList(rs, competitions, connection);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return competitions;

    }


    private static Set<CompetitionResult> getCompetitionResults(Long competitionId, Connection connection) {

        Set<CompetitionResult> competitionResults = new HashSet<>();

        try  {
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


        } catch (SQLException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return competitionResults;
    }


    private static Optional<Address> getAddress(Long addressId, Connection connection) {
        Address address = null;

        try {
            String sqlQuery = String.format("SELECT * FROM ADDRESS WHERE ADDRESS_ID = %d", addressId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                String streetName = rs.getString("STREET");
                String houseNumber = rs.getString("HOUSE_NUMBER");
                City city = City.getCityFromStringName(rs.getString("CITY"));

                Address.AddressBuilder addressBuilder = new Address.AddressBuilder(city)
                        .setHouseNumber(houseNumber)
                        .setId(addressId)
                        .setStreet(streetName);

                address = addressBuilder.build();
            }


        } catch (SQLException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return Optional.ofNullable(address);
    }

    public static Long saveAddress(Address addressToSave) {

        Long addresId = 0L;
        try (Connection connection = connectToDatabase()) {

            String insertAddressSql = "INSERT INTO ADDRESS(STREET, HOUSE_NUMBER, CITY) VALUES(?, ?, ?)";

            PreparedStatement pstmt = connection.prepareStatement(insertAddressSql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, addressToSave.getStreet());
            pstmt.setLong(2, Long.parseLong(addressToSave.getHouseNumber()));
            pstmt.setString(3, addressToSave.getCity().getName());
            pstmt.executeUpdate();


            ResultSet generatedKeys = pstmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                addresId = generatedKeys.getLong(1);
            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja adrese u bazu podataka";
            logger.error(message, ex);
        }

        return addresId;

    }

    public static List<MathProject> getProjects() {

        List<MathProject> mathProjects = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM MATH_PROJECT ";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            mapResultSetToMathProjectsList(rs, mathProjects, connection);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return mathProjects;

    }


    private static Map<MathClub, List<Student>> getProjectCollaborators(Long projectId, Connection connection) {
        Map<MathClub, List<Student>> mathProjectCollaborators = new HashMap<>();

        try{
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


        } catch (SQLException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return mathProjectCollaborators;
    }


    public static Long saveMathClubs(List<MathClub> mathClubs) {
        Long mathClubId = 0L;
        try (Connection connection = connectToDatabase()) {
            for (MathClub mathClub : mathClubs) {

                String insertMathClubSql = "INSERT INTO MATH_CLUB(NAME, ADDRESS_ID) VALUES(?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insertMathClubSql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, mathClub.getName());
                pstmt.setLong(2, mathClub.getAddress().getAddressId());
                pstmt.executeUpdate();


                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    mathClubId = generatedKeys.getLong(1);
                    for (Student member : mathClub.getStudents()) {
                        String insertMembersIntoMathClubStudentsSql = "INSERT INTO MATH_CLUB_STUDENTS(CLUB_ID, STUDENT_ID) VALUES(?, ?);";
                        pstmt = connection.prepareStatement(insertMembersIntoMathClubStudentsSql);
                        pstmt.setLong(1, mathClubId);
                        pstmt.setLong(2, member.getId());
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja matematičkih klubova u bazu podataka";
            logger.error(message, ex);
        }

        return mathClubId;
    }

    public static boolean saveMathProjects(List<MathProject> mathProjects) {
        boolean success = true;
        try (Connection connection = connectToDatabase()) {
            for (MathProject mathProject : mathProjects) {

                String insertMathProjectSql = "INSERT INTO MATH_PROJECT(NAME, DESCRIPTION, START_DATE, ORGANIZER_ID, " +
                        "ADDRESS_ID, END_DATE, WEB_ADDRESS) VALUES(?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insertMathProjectSql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, mathProject.getName());
                pstmt.setString(2, mathProject.getDescription());
                pstmt.setDate(3, Date.valueOf(mathProject.getStartDate()));
                pstmt.setLong(4, mathProject.getOrganizer().getId());
                pstmt.setLong(5, mathProject.getAddress().getAddressId());
                pstmt.setDate(6, mathProject.getEndDate() != null ? Date.valueOf(mathProject.getEndDate()) : null);
                pstmt.setString(7, mathProject.getProjectWebPageAddress());
                pstmt.executeUpdate();

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {

                    Long projectId = generatedKeys.getLong(1);

                    for (Map.Entry<MathClub, List<Student>> entry : mathProject.getCollaborators().entrySet()) {
                        String insertMembersIntoMathClubStudentsSql =
                                "INSERT INTO PROJECT_COLLABORATORS (PROJECT_ID, MATH_CLUB_ID, STUDENT_ID) VALUES(?,?,?);";

                        pstmt = connection.prepareStatement(insertMembersIntoMathClubStudentsSql);
                        pstmt.setLong(1, projectId);
                        pstmt.setLong(2, entry.getKey().getId());

                        List<Student> clubMembersCollaborators = entry.getValue();

                        for (Student clubMemberCollaborator : clubMembersCollaborators) {
                            System.out.println(clubMemberCollaborator.getId());
                            pstmt.setLong(3, clubMemberCollaborator.getId());
                            pstmt.executeUpdate();
                        }

                    }
                }
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja matematičkih projekata u bazu podataka";
            logger.error(message, ex);
            success = false;
        }


        return success;
    }

    public static boolean saveMathCompetitions(List<Competition> mathCompetitions) {
        boolean success = true;

        try (Connection connection = connectToDatabase()) {
            for (Competition mathCompetition : mathCompetitions) {

                String insertCompetitionProjectSql = "INSERT INTO COMPETITION(NAME, DESCRIPTION, ADDRESS_ID, " +
                        "TIME_OF_COMPETITION, AUDITORIUM_BUILDING, AUDITORIUM_HALL, DATE_OF_COMPETITION, " +
                        "ORGANIZER_ID) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insertCompetitionProjectSql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, mathCompetition.getName());
                pstmt.setString(2, mathCompetition.getDescription());
                pstmt.setLong(3, mathCompetition.getAddress().getAddressId());

                Time sqlTime = Time.valueOf(mathCompetition.getTimeOfCompetition().toLocalTime());
                pstmt.setTime(4, sqlTime);

                pstmt.setString(5, mathCompetition.getAuditorium().building());
                pstmt.setString(6, mathCompetition.getAuditorium().hall());
                pstmt.setDate(7, Date.valueOf(mathCompetition.getTimeOfCompetition().toLocalDate()));
                pstmt.setLong(8, mathCompetition.getOrganizer().getId());
                pstmt.executeUpdate();


                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {

                    Long competitionId = generatedKeys.getLong(1);

                    for (CompetitionResult competitionResult : mathCompetition.getCompetitionResults()) {
                        String insertParticipantScoreIntoCompetitionResultsSql =
                                "INSERT INTO COMPETITION_RESULTS  (COMPETITION_ID, STUDENT_ID, SCORE) VALUES(?,?,?);";

                        pstmt = connection.prepareStatement(insertParticipantScoreIntoCompetitionResultsSql);
                        pstmt.setLong(1, competitionId);
                        pstmt.setLong(2, competitionResult.participant().getId());
                        pstmt.setBigDecimal(3, competitionResult.score());
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja matematičkih natjecanja u bazu podataka";
            logger.error(message, ex);
            success = false;
        }


        return success;
    }


    public static boolean saveStudents(List<Student> students) {
        boolean success = true;
        try (Connection connection = connectToDatabase()) {
            for (Student student : students) {

                Long clubMembershipId = addClubMembershipForStudent(student);

                String insertStudentSql = "INSERT INTO STUDENT(NAME,SURNAME,EMAIL," +
                        "YEAR_OF_STUDY, CLUB_MEMBERSHIP_ID, GENDER, PICTURE_PATH) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insertStudentSql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getSurname());
                pstmt.setString(3, student.getEmail());
                pstmt.setInt(4, student.getYearOfStudy());
                pstmt.setLong(5, clubMembershipId);
                pstmt.setString(6, student.getGender());
                pstmt.setString(7, student.getPicture().getPicturePath());
                pstmt.executeUpdate();


                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {

                    Long studentId = generatedKeys.getLong(1);
                    Map<String, Integer> studentGrades = student.getGrades();
                    Long studentMathClubId = student.getClubMembership().getClubId();

                    saveStudentGrades(studentId, studentGrades);
                    addStudentToClub(studentId, studentMathClubId);

                }
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja studenata u bazu podataka";
            logger.error(message, ex);
            success = false;
        }

        return success;
    }

    private static Long addClubMembershipForStudent(Student student) {

        Long clubMembershipId = 0L;
        try (Connection connection = connectToDatabase()) {

            String insertStudentSql = "INSERT INTO CLUB_MEMBERSHIP(JOIN_DATE,CLUB_ID) VALUES(?, ?)";

            PreparedStatement pstmt = connection.prepareStatement(insertStudentSql, PreparedStatement.RETURN_GENERATED_KEYS);

            pstmt.setDate(1, Date.valueOf(student.getClubMembership().getJoinDate()));
            pstmt.setLong(2, student.getClubMembership().getClubId());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {

                clubMembershipId = generatedKeys.getLong(1);

            }

        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja studenata u bazu podataka";
            logger.error(message, ex);
        }

        return clubMembershipId;

    }

    private static void addStudentToClub(Long studentId, Long mathClubId) {
        try (Connection connection = connectToDatabase()) {

            String insertStudentIntoMathClubSql = "INSERT INTO MATH_CLUB_STUDENTS (CLUB_ID, STUDENT_ID) VALUES (?, ?);";

            PreparedStatement pstmt = connection.prepareStatement(insertStudentIntoMathClubSql);
            pstmt.setLong(1, mathClubId);
            pstmt.setLong(2, studentId);
            pstmt.executeUpdate();


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja matematičkih natjecanja u bazu podataka";
            logger.error(message, ex);
        }
    }

    private static void saveStudentGrades(Long studentId, Map<String, Integer> studentGrades) {


        try (Connection connection = connectToDatabase()) {
            for (Map.Entry<String, Integer> entry : studentGrades.entrySet()) {

                String insertStudentGradesSql = "INSERT INTO STUDENT_GRADES (STUDENT_ID, SUBJECT_NAME, GRADE) VALUES (?, ?, ?);";


                PreparedStatement pstmt = connection.prepareStatement(insertStudentGradesSql);
                pstmt.setLong(1, studentId);
                pstmt.setString(2, entry.getKey());
                pstmt.setInt(3, entry.getValue());
                pstmt.executeUpdate();

            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja matematičkih natjecanja u bazu podataka";
            logger.error(message, ex);
        }
    }


    public static boolean deleteProject(MathProject mathProjectForDeletion) {
        boolean projectDeletionSuccess = false;

        try (Connection connection = connectToDatabase()) {

            String deleteProjectCollaboratorsSql = "DELETE FROM PROJECT_COLLABORATORS WHERE PROJECT_ID = ?;";

            try (PreparedStatement pstmtCollaborators = connection.prepareStatement(deleteProjectCollaboratorsSql)) {
                pstmtCollaborators.setLong(1, mathProjectForDeletion.getId());
                pstmtCollaborators.executeUpdate();

            }


            String deleteProjectSql = "DELETE FROM MATH_PROJECT WHERE PROJECT_ID = ?;";

            try (PreparedStatement pstmt = connection.prepareStatement(deleteProjectSql)) {
                pstmt.setLong(1, mathProjectForDeletion.getId());
                pstmt.executeUpdate();
                projectDeletionSuccess = true;
            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod brisanja projekta iz baze podataka";
            logger.error(message, ex);
        }

        return projectDeletionSuccess;
    }

    public static boolean deleteCompetition(Competition competitionForDeletion) {
        boolean competitionDeletionSuccess = false;

        try (Connection connection = connectToDatabase()) {

            String deleteCompetitionResultsSql = "DELETE FROM COMPETITION_RESULTS WHERE COMPETITION_ID = ?;";

            try (PreparedStatement pstmtCollaborators = connection.prepareStatement(deleteCompetitionResultsSql)) {
                pstmtCollaborators.setLong(1, competitionForDeletion.getId());
                pstmtCollaborators.executeUpdate();

            }


            String deleteCompetitionSql = "DELETE FROM COMPETITION WHERE COMPETITION_ID = ?;";

            try (PreparedStatement pstmt = connection.prepareStatement(deleteCompetitionSql)) {
                pstmt.setLong(1, competitionForDeletion.getId());
                pstmt.executeUpdate();
                competitionDeletionSuccess = true;
            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod brisanja natjecanja iz baze podataka";
            logger.error(message, ex);
        }

        return competitionDeletionSuccess;
    }

    public static boolean deleteStudent(Student studentForDeletion) {
        boolean studentDeletionSuccess = false;

        try (Connection connection = connectToDatabase()) {

            String deleteStudentFromMathClubSql = "DELETE FROM MATH_CLUB_STUDENTS  WHERE STUDENT_ID = ?;";

            try (PreparedStatement pstmtCollaborators = connection.prepareStatement(deleteStudentFromMathClubSql)) {
                pstmtCollaborators.setLong(1, studentForDeletion.getId());
                pstmtCollaborators.executeUpdate();

            }

            String deleteStudentGradesSql = "DELETE FROM STUDENT_GRADES WHERE STUDENT_ID = ?;";

            try (PreparedStatement pstmt = connection.prepareStatement(deleteStudentGradesSql)) {
                pstmt.setLong(1, studentForDeletion.getId());
                pstmt.executeUpdate();
            }

            String deleteStudentCompetitionsResultsSql = "DELETE FROM COMPETITION_RESULTS WHERE STUDENT_ID = ?;";

            try (PreparedStatement pstmt = connection.prepareStatement(deleteStudentCompetitionsResultsSql)) {
                pstmt.setLong(1, studentForDeletion.getId());
                pstmt.executeUpdate();
            }

            String deleteStudentFromProjectsCollaborationsSql = "DELETE FROM PROJECT_COLLABORATORS  WHERE STUDENT_ID = ?;";

            try (PreparedStatement pstmtCollaborators = connection.prepareStatement(deleteStudentFromProjectsCollaborationsSql)) {
                pstmtCollaborators.setLong(1, studentForDeletion.getId());
                pstmtCollaborators.executeUpdate();
            }

            String deleteStudentSql = "DELETE FROM STUDENT WHERE STUDENT_ID = ?;";

            try (PreparedStatement pstmt = connection.prepareStatement(deleteStudentSql)) {
                pstmt.setLong(1, studentForDeletion.getId());
                pstmt.executeUpdate();
            }

            String deleteClubMembershipFromStudentSql = "DELETE FROM CLUB_MEMBERSHIP WHERE CLUB_MEMBERSHIP_ID = ?;";

            try (PreparedStatement pstmtCollaborators = connection.prepareStatement(deleteClubMembershipFromStudentSql)) {
                pstmtCollaborators.setLong(1, studentForDeletion.getClubMembership().getClubMembershipId());
                pstmtCollaborators.executeUpdate();
                studentDeletionSuccess = true;
            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod brisanja studenta iz baze podataka";
            logger.error(message, ex);
        }

        return studentDeletionSuccess;
    }

    public static boolean deleteMathClub(MathClub mathClubForDeletion) {
        boolean mathClubDeletionSuccess = false;

        for (Student clubMember : mathClubForDeletion.getStudents()) {
            boolean memberDeletionSuccess = deleteStudent(clubMember);
            if (!memberDeletionSuccess) {
                return false;
            }
        }

        try (Connection connection = connectToDatabase()) {

            String deleteMathClub = "DELETE FROM MATH_CLUB WHERE CLUB_ID = ?;";

            try (PreparedStatement pstmtCollaborators = connection.prepareStatement(deleteMathClub)) {
                pstmtCollaborators.setLong(1, mathClubForDeletion.getId());
                pstmtCollaborators.executeUpdate();

                mathClubDeletionSuccess = true;
            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod brisanja matematičkog kluba iz baze podataka";
            logger.error(message, ex);
        }

        return mathClubDeletionSuccess;

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

            mapResultSetToStudentsList(rs, filteredStudents, connection);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return filteredStudents;

    }

    public static List<MathClub> getMathClubsByFilter(MathClubFilter mathClubFilter) {

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

            List<Student> students = getStudents(connection);
            List<Address> addresses = getAddresses(connection);

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

            mapResultSetToMathProjectsList(rs, filteredMathProjects, connection);


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

            mapResultSetToCompetitionsList(rs, filteredCompetitions, connection);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return filteredCompetitions;

    }

    private static void mapResultSetToMathClubsList(ResultSet rs, List<Address> addresses,
                                                    List<Student> students,
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


    private static void mapResultSetToStudentsList(ResultSet rs, List<Student> filteredStudents, Connection connection
    ) throws SQLException {

        while (rs.next()) {

            Long studentId = rs.getLong("STUDENT_ID");
            String studentName = rs.getString("NAME");
            String studentSurname = rs.getString("SURNAME");

            Student.StudentBuilder studentBuilder = new Student.StudentBuilder(studentId, studentName, studentSurname);

            String studentEmail = rs.getString("EMAIL");
            studentBuilder.email(studentEmail);

            Integer yearOfStudy = rs.getInt("YEAR_OF_STUDY");
            studentBuilder.yearOfStudy(yearOfStudy);

            Map<String, Integer> studentGrades = getStudentGrades(studentId, connection);
            studentBuilder.grades(studentGrades);

            Long membershipId = rs.getLong("CLUB_MEMBERSHIP_ID");
            ClubMembership clubMembership = getStudentClubMembership(membershipId, connection);
            studentBuilder.clubMembership(clubMembership);

            String gender = rs.getString("GENDER");
            studentBuilder.gender(Gender.getGenderFromString(gender).getGender());

            String filePath = rs.getString("PICTURE_PATH");
            studentBuilder.picturePath(filePath);

            Student filteredStudent = studentBuilder.build();

            filteredStudents.add(filteredStudent);

        }
    }

    private static void mapResultSetToMathProjectsList(ResultSet rs, List<MathProject> mathProjects,
                                                       Connection connection) throws SQLException {
        while (rs.next()) {
            Long projectId = rs.getLong("PROJECT_ID");
            String projectName = rs.getString("NAME");
            String projectDescription = rs.getString("DESCRIPTION");
            LocalDate startDate = rs.getDate("START_DATE").toLocalDate();


            Date endDate = rs.getDate("END_DATE");

            String projectWebPageAddress = rs.getString("WEB_ADDRESS");

            LocalDate endDateOfProject = null;
            if (Optional.ofNullable(endDate).isPresent()) {
                endDateOfProject = endDate.toLocalDate();
            }

            Long mathClubOrganizerId = rs.getLong("ORGANIZER_ID");
            Long addressId = rs.getLong("ADDRESS_ID");


            Address projectAddress = getAddress(addressId, connection).get();
            Map<MathClub, List<Student>> projectCollaborators = getProjectCollaborators(projectId, connection);
            MathClub organizer = getMathClub(mathClubOrganizerId, connection).get();

            MathProject newMathProject = new MathProject(projectId, organizer, startDate, projectAddress,
                    projectName, projectDescription, projectCollaborators);

            newMathProject.setProjectWebPageAddress(projectWebPageAddress);
            newMathProject.setEndDate(endDateOfProject);

            mathProjects.add(newMathProject);


        }
    }


    private static void mapResultSetToCompetitionsList(ResultSet rs, List<Competition> competitions,
                                                       Connection connection) throws SQLException {
        while (rs.next()) {
            Long competitionId = rs.getLong("COMPETITION_ID");
            String competitionName = rs.getString("NAME");
            String competitionDescription = rs.getString("DESCRIPTION");

            Long addressId = rs.getLong("ADDRESS_ID");
            Optional<Address> competitionAddress = getAddress(addressId, connection);

            Time timeOfCompetition = rs.getTime("TIME_OF_COMPETITION");
            LocalDate dateOfCompetition = rs.getDate("DATE_OF_COMPETITION").toLocalDate();


            LocalDateTime dateAndTimeOfCompetition = dateOfCompetition.atTime(timeOfCompetition.toLocalTime());

            String auditoriumBuildingName = rs.getString("AUDITORIUM_BUILDING");
            String auditoriumHallName = rs.getString("AUDITORIUM_HALL");
            Auditorium auditorium = new Auditorium(auditoriumBuildingName, auditoriumHallName);

            Set<CompetitionResult> competitionResults = getCompetitionResults(competitionId, connection);

            Long organizerId = rs.getLong("ORGANIZER_ID");

            MathClub organizer = getMathClub(organizerId, connection).get();


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

    public static boolean deleteUser(User userForDeletion) {
        boolean userDeletionSuccess = true;

        try (Connection connection = connectToDatabase()) {

            String deleteProjectCollaboratorsSql = "DELETE FROM USERS WHERE USERNAME = ?;";

            try (PreparedStatement pstmtCollaborators = connection.prepareStatement(deleteProjectCollaboratorsSql)) {
                pstmtCollaborators.setString(1, userForDeletion.getUsername());
                pstmtCollaborators.executeUpdate();
            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod brisanja korisnika iz baze podataka";
            logger.error(message, ex);
            userDeletionSuccess = false;
        }

        return userDeletionSuccess;
    }


    public static boolean saveUser(User registerUser) {
        boolean success = true;

        try (Connection connection = connectToDatabase()) {

            String insertUserSql = "INSERT INTO USERS(USERNAME, PASSWORD, MATH_CLUB_ID, ROLE, PICTURE_PATH) VALUES(?, ?, ?, ?, ?)";

            PreparedStatement pstmt = connection.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, registerUser.getUsername());
            pstmt.setString(2, registerUser.getHashedPassword());
            pstmt.setLong(3, registerUser.getMathClubId());
            pstmt.setString(4, registerUser.getRole().getName());
            pstmt.setString(5, registerUser.getPicture().getPicturePath());
            pstmt.executeUpdate();


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja korisnika u bazu podataka";
            logger.error(message, ex);
            success = false;
        }

        return success;
    }

    public static boolean updateStudent(Student updatedStudent) {
        try (Connection connection = connectToDatabase()) {
            String updateQuery = "UPDATE STUDENT SET NAME = ?, SURNAME = ?, EMAIL = ?, YEAR_OF_STUDY = ?, GENDER = ?," +
                    " PICTURE_PATH = ? WHERE STUDENT_ID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, updatedStudent.getName());
                preparedStatement.setString(2, updatedStudent.getSurname());
                preparedStatement.setString(3, updatedStudent.getEmail());
                preparedStatement.setInt(4, updatedStudent.getYearOfStudy());
                preparedStatement.setString(5, updatedStudent.getGender());
                preparedStatement.setString(6, updatedStudent.getPicture().getPicturePath());
                preparedStatement.setLong(7, updatedStudent.getId());

                preparedStatement.executeUpdate();
            }

            updateStudentGrades(updatedStudent.getId(), updatedStudent.getGrades());


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
            return false;
        }

        return true;
    }


    private static void updateStudentGrades(Long studentId, Map<String, Integer> updatedGrades) throws SQLException, IOException {
        try (Connection connection = connectToDatabase()) {
            String deleteQuery = "DELETE FROM STUDENT_GRADES WHERE STUDENT_ID = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setLong(1, studentId);
                deleteStatement.executeUpdate();
            }

            String insertQuery = "INSERT INTO STUDENT_GRADES (STUDENT_ID, SUBJECT_NAME, GRADE) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                for (Map.Entry<String, Integer> entry : updatedGrades.entrySet()) {
                    insertStatement.setLong(1, studentId);
                    insertStatement.setString(2, entry.getKey());
                    insertStatement.setInt(3, entry.getValue());
                    insertStatement.executeUpdate();
                }
            }

        }
    }

    public static boolean updateCompetition(Competition competitionToUpdate) {
        try (Connection connection = connectToDatabase()) {
            String updateQuery = "UPDATE COMPETITION SET NAME = ?, DESCRIPTION = ?, TIME_OF_COMPETITION = ?" +
                    ",AUDITORIUM_BUILDING = ?, AUDITORIUM_HALL = ?, DATE_OF_COMPETITION = ?, ORGANIZER_ID = ?" +
                    " WHERE COMPETITION_ID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, competitionToUpdate.getName());
                preparedStatement.setString(2, competitionToUpdate.getDescription());
                preparedStatement.setTime(3, Time.valueOf(competitionToUpdate.getTimeOfCompetition().toLocalTime()));
                preparedStatement.setString(4, competitionToUpdate.getAuditorium().building());
                preparedStatement.setString(5, competitionToUpdate.getAuditorium().hall());
                preparedStatement.setDate(6, Date.valueOf(competitionToUpdate.getTimeOfCompetition().toLocalDate()));
                preparedStatement.setLong(7, competitionToUpdate.getOrganizer().getId());
                preparedStatement.setLong(8, competitionToUpdate.getId());


                preparedStatement.executeUpdate();
            }

            updateAddress(competitionToUpdate.getAddress(), connection);
            updateCompetitionScores(competitionToUpdate.getId(), competitionToUpdate.getCompetitionResults());


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
            return false;
        }

        return true;
    }

    private static void updateAddress(Address address, Connection connection) {
        try {
            String updateQuery = "UPDATE ADDRESS SET STREET = ?, HOUSE_NUMBER = ?, CITY = ? WHERE ADDRESS_ID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, address.getStreet());
                preparedStatement.setString(2, address.getHouseNumber());
                preparedStatement.setString(3, address.getCity().getName());
                preparedStatement.setLong(4, address.getAddressId());

                preparedStatement.executeUpdate();
            }

        } catch (SQLException ex) {
            String message = "Dogodila se pogreška kod ažuriranja adrese u bazi podataka";
            logger.error(message, ex);
        }
    }

    public static boolean updateCompetitionScores(Long competitionId, Set<CompetitionResult> competitionResults) {

        boolean successfullyUpdated = true;
        try (Connection connection = connectToDatabase()) {
            String deleteQuery = "DELETE FROM COMPETITION_RESULTS WHERE COMPETITION_ID = ?";

            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setLong(1, competitionId);
                deleteStatement.executeUpdate();
            }

            String insertQuery = "INSERT INTO COMPETITION_RESULTS (COMPETITION_ID, STUDENT_ID, SCORE) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                for (CompetitionResult result : competitionResults) {
                    insertStatement.setLong(1, competitionId);
                    insertStatement.setLong(2, result.participant().getId());
                    insertStatement.setBigDecimal(3, result.score());
                    insertStatement.executeUpdate();
                }
            }

        } catch (SQLException | IOException ex) {
            successfullyUpdated = false;
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);

        }

        return successfullyUpdated;
    }

    public static boolean updateUsername(User currentUser, String newUsername) {
        boolean successfullyUpdated = true;
        try (Connection connection = connectToDatabase()) {
            String updateQuery = "UPDATE USERS SET USERNAME = ? WHERE USERNAME = ? AND PASSWORD = ?";

            try (PreparedStatement insertStatement = connection.prepareStatement(updateQuery)) {

                insertStatement.setString(1, newUsername);
                insertStatement.setString(2, currentUser.getUsername());
                insertStatement.setString(3, currentUser.getHashedPassword());
                insertStatement.executeUpdate();

            }

        } catch (SQLException | IOException ex) {
            successfullyUpdated = false;
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);

        }

        return successfullyUpdated;
    }

    public static boolean updatePassword(User currentUser, String newPassword) {
        boolean successfullyUpdated = true;
        try (Connection connection = connectToDatabase()) {
            String updateQuery = "UPDATE USERS SET PASSWORD = ? WHERE USERNAME = ? AND PASSWORD = ?";

            try (PreparedStatement insertStatement = connection.prepareStatement(updateQuery)) {

                insertStatement.setString(1, newPassword);
                insertStatement.setString(2, currentUser.getUsername());
                insertStatement.setString(3, currentUser.getHashedPassword());
                insertStatement.executeUpdate();

            }

        } catch (SQLException | IOException ex) {
            successfullyUpdated = false;
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);

        }

        return successfullyUpdated;
    }

    public static boolean updateUserProfilePicture(User currentUser, String newImagePath) {
        boolean successfullyUpdated = true;
        try (Connection connection = connectToDatabase()) {
            String updateQuery = "UPDATE USERS SET PICTURE_PATH = ? WHERE USERNAME = ? AND PASSWORD = ?";

            try (PreparedStatement insertStatement = connection.prepareStatement(updateQuery)) {

                insertStatement.setString(1, newImagePath);
                insertStatement.setString(2, currentUser.getUsername());
                insertStatement.setString(3, currentUser.getHashedPassword());
                insertStatement.executeUpdate();

            }

        } catch (SQLException | IOException ex) {
            successfullyUpdated = false;
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);

        }

        return successfullyUpdated;
    }

    public static boolean updateProject(MathProject projectToUpdate) {
        try (Connection connection = connectToDatabase()) {
            String updateQuery = "UPDATE MATH_PROJECT  SET NAME = ?, DESCRIPTION = ?, END_DATE = ?, WEB_ADDRESS = ?" +
                    "WHERE PROJECT_ID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, projectToUpdate.getName());
                preparedStatement.setString(2, projectToUpdate.getDescription());
                preparedStatement.setDate(3, projectToUpdate.getEndDate() != null ?
                        Date.valueOf(projectToUpdate.getEndDate()) : null);
                preparedStatement.setString(4, projectToUpdate.getProjectWebPageAddress());
                preparedStatement.setLong(5, projectToUpdate.getId());

                preparedStatement.executeUpdate();
            }


            updateAddress(projectToUpdate.getAddress(), connection);


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
            return false;
        }

        return true;
    }

    public static boolean updateProjectCollaborators(Long projectId, Map<MathClub, List<Student>> projectCollaborators) {
        boolean successfullyUpdated = true;
        try (Connection connection = connectToDatabase()) {
            String deleteQuery = "DELETE FROM PROJECT_COLLABORATORS WHERE PROJECT_ID = ?";

            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setLong(1, projectId);
                deleteStatement.executeUpdate();
            }

            String insertQuery = "INSERT INTO PROJECT_COLLABORATORS (PROJECT_ID, MATH_CLUB_ID, STUDENT_ID) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                for (Map.Entry<MathClub, List<Student>> entry : projectCollaborators.entrySet()) {
                    insertStatement.setLong(1, projectId);
                    insertStatement.setLong(2, entry.getKey().getId());
                    for (Student student : entry.getValue()) {
                        insertStatement.setLong(3, student.getId());
                        insertStatement.executeUpdate();
                    }
                }
            }

        } catch (SQLException | IOException ex) {
            successfullyUpdated = false;
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);

        }

        return successfullyUpdated;
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

            Address.AddressBuilder addressBuilder = new Address.AddressBuilder(city).setAddressId(addressId)
                    .setStreet(street).setHouseNumber(houseNumber);

            Address filteredAddress = addressBuilder.build();

            filteredAddresses.add(filteredAddress);
        }
    }

    public static boolean deleteAddress(Address addressForDeletion) {
        boolean addressDeletionSuccess = false;

        try (Connection connection = connectToDatabase()) {

            String deleteAddressSql = "DELETE FROM ADDRESS WHERE ADDRESS_ID = ?;";

            try (PreparedStatement pstmt = connection.prepareStatement(deleteAddressSql)) {
                pstmt.setLong(1, addressForDeletion.getAddressId());
                pstmt.executeUpdate();
                addressDeletionSuccess = true;
            }

        }
        catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod brisanja adrese iz baze podataka";
            logger.error(message, ex);
        }

        return addressDeletionSuccess;
    }
}