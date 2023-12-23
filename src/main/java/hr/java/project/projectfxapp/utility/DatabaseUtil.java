package hr.java.project.projectfxapp.utility;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.City;
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

public class DatabaseUtil {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
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



    public static List<Address> getAddresses(){
        List<Address> addresses = new ArrayList<>();

        try(Connection connection = connectToDatabase()){
            String sqlQuery = "SELECT * FROM ADDRESS";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while(rs.next()){

                Long addressId = rs.getLong("ADDRESS_ID");
                String streetName = rs.getString("STREET");
                String houseNumber = rs.getString("HOUSE_NUMBER");
                City city = City.getCityFromStringName(rs.getString("CITY"));

                Address.AdressBuilder address = new Address.AdressBuilder(city)
                        .setHouseNumber(houseNumber)
                        .setId(addressId)
                        .setStreet(streetName);

                addresses.add(address.build());
            }

        }catch (SQLException | IOException ex){
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        };

        return  addresses;

    }


    public static List<MathClub> getMathClubs(){

        List<MathClub> mathClubs = new ArrayList<>();

        try(Connection connection = connectToDatabase()){
            String sqlQuery = "SELECT * FROM MATH_CLUB ";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while(rs.next()){

                Long mathClubId = rs.getLong("CLUB_ID");
                Optional <MathClub> mathClub = getMathClub(mathClubId);

                mathClub.ifPresent(mathClubs::add);
            }

        }catch (SQLException | IOException ex){
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        };

        return  mathClubs;

    }

    private static Optional<MathClub> getMathClub(Long mathClubId) {
        List<Address> addresses = getAddresses();
        List<Student> students = getStudents();

        MathClub mathClub = null;

        try(Connection connection = connectToDatabase()){
            String sqlQuery = String.format("SELECT * FROM MATH_CLUB WHERE CLUB_ID = %d", mathClubId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while(rs.next()){

                String mathClubName = rs.getString("NAME");
                Long addressId = rs.getLong("ADDRESS_ID");

                Address mathClubAddress = addresses.stream()
                        .filter(address -> address.getAddressId().equals(addressId))
                        .findFirst()
                        .get();

                Set<Student> studentsForMathClub = getStudentsForMathClub(mathClubId, students);

                mathClub = new MathClub(mathClubId, mathClubName, mathClubAddress, studentsForMathClub);

            }

        }catch (SQLException | IOException ex){
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        };

        return  Optional.ofNullable(mathClub);


    }


    public static Set<Student> getStudentsForMathClub(Long mathClubId, List<Student> students) {

        Set<Student> studentsForMathClub = new HashSet<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT * FROM MATH_CLUB_STUDENTS MCS JOIN STUDENT S ON MCS.STUDENT_ID" +
                    " = S.STUDENT_ID WHERE MCS.CLUB_ID = %d;", mathClubId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                Long studentForMathClubId = rs.getLong("STUDENT_ID");

                Optional<Student> studentForMathClub = students.stream()
                        .filter(student -> student.getId().equals(studentForMathClubId))
                        .findFirst();

                studentForMathClub.ifPresent(studentsForMathClub::add);

            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return studentsForMathClub;

    }


    public static List<Student> getStudents() {

        List<Student> students = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM STUDENT";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {

                Long studentId = rs.getLong("STUDENT_ID");
                Optional<Student> newStudent = getStudent(studentId);

                newStudent.ifPresent(students::add);


            }


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

                String studentName = rs.getString("NAME");
                String studentSurname = rs.getString("SURNAME");
                String studentEmail = rs.getString("EMAIL");
                Integer yearOfStudy = rs.getInt("YEAR_OF_STUDY");

                Map<String, Integer> studentGrades = getStudentGrades(studentId);

                Long membershipId = rs.getLong("CLUB_MEMBERSHIP_ID");

                ClubMembership clubMembership = getStudentClubMembership(membershipId);

                student = new Student(studentId, studentName, studentSurname, studentEmail,
                        yearOfStudy, studentGrades, clubMembership);


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

                return  new ClubMembership(clubId, joinDate);

            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return null;
    }

    private static Map<String, Integer> getStudentGrades(Long studentId){

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


                competitionAddress.ifPresent(address -> {

                    Competition newCompetition = new Competition(competitionId, competitionName, competitionDescription
                            ,address, auditorium, dateAndTimeOfCompetition, competitionResults);

                    competitions.add(newCompetition);
                });


            }


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

            while (rs.next()) {
                Long projectId = rs.getLong("PROJECT_ID");
                String projectName = rs.getString("NAME");
                String projectDescription = rs.getString("DESCRIPTION");

                Map<MathClub, List<Student>> projectCollaborators = getProjectCollaborators(projectId);

                MathProject newMathProject = new MathProject(projectId, projectName, projectDescription, projectCollaborators);

                mathProjects.add(newMathProject);


            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return mathProjects;

    }

    private static Map<MathClub, List<Student>> getProjectCollaborators(Long projectId) {
        Map<MathClub, List<Student>> mathProjectCollaborators = new HashMap<>();

        List<Student> studentCollaborators = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            String sqlQuery = String.format("SELECT * FROM PROJECT_COLLABORATORS WHERE PROJECT_ID = %d", projectId);
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();



            while (rs.next()) {
                Long mathClubCollaboratorId = rs.getLong("MATH_CLUB_ID");
                Optional<MathClub> mathClubCollaborator = getMathClub(mathClubCollaboratorId);
                Long studentCollaboratorId = rs.getLong("STUDENT_ID");
                Optional <Student> studentCollaborator = getStudent(studentCollaboratorId);


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


    public static void saveMathClubs(List<MathClub> mathClubs){
        try (Connection connection = connectToDatabase()) {
            for (MathClub mathClub : mathClubs) {

                String insertMathClubSql = "INSERT INTO MATH_CLUB(NAME, ADDRESS_ID) VALUES(?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insertMathClubSql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, mathClub.getName());
                pstmt.setLong(2, mathClub.getAddress().getAddressId());
                pstmt.executeUpdate();


                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Long mathclubId = generatedKeys.getLong(1);

                    for (Student member : mathClub.getStudents()) {
                        String insertMembersIntoMathClubStudentsSql = "INSERT INTO MATH_CLUB_STUDENTS(CLUB_ID, STUDENT_ID) VALUES(?, ?);";
                        pstmt = connection.prepareStatement(insertMembersIntoMathClubStudentsSql);
                        pstmt.setLong(1, mathclubId);
                        pstmt.setLong(2, member.getId());
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja matematičkih klubova u bazu podataka";
            logger.error(message, ex);
        }
    }

    public static void saveMathProjects(List<MathProject> mathProjects){
        try (Connection connection = connectToDatabase()) {
            for (MathProject mathProject: mathProjects) {

                String insertMathProjectSql = "INSERT INTO MATH_PROJECT(NAME, DESCRIPTION) VALUES(?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insertMathProjectSql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, mathProject.getName());
                pstmt.setString(2, mathProject.getDescription());
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
        }
    }

    public static void saveMathCompetitions(List<Competition> mathCompetitions){
        try (Connection connection = connectToDatabase()) {
            for (Competition mathCompetition: mathCompetitions) {

                String insertCompetitionProjectSql = "INSERT INTO COMPETITION(NAME, DESCRIPTION, ADDRESS_ID, " +
                        "TIME_OF_COMPETITION, AUDITORIUM_BUILDING, AUDITORIUM_HALL, DATE_OF_COMPETITION) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insertCompetitionProjectSql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, mathCompetition.getName());
                pstmt.setString(2, mathCompetition.getDescription());
                pstmt.setLong(3, mathCompetition.getAddress().getAddressId());

                Time sqlTime = Time.valueOf(mathCompetition.getTimeOfCompetition().toLocalTime());
                pstmt.setTime(4, sqlTime);

                pstmt.setString(5, mathCompetition.getAuditorium().building());
                pstmt.setString(6, mathCompetition.getAuditorium().hall());
                pstmt.setDate(7, Date.valueOf(mathCompetition.getTimeOfCompetition().toLocalDate()));
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
        }
    }


    public static void saveStudents(List<Student> students){
        try (Connection connection = connectToDatabase()) {
            for (Student student: students) {

                Long clubMembershipId = addClubMembershipForStudent(student);

                String insertStudentSql = "INSERT INTO STUDENT(NAME,SURNAME,EMAIL," +
                        "YEAR_OF_STUDY, CLUB_MEMBERSHIP_ID) " +
                        "VALUES(?, ?, ?, ?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insertStudentSql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getSurname());
                pstmt.setString(3, student.getEmail());
                pstmt.setInt(4, student.getYearOfStudy());
                pstmt.setLong(5, clubMembershipId);
                pstmt.executeUpdate();


                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {

                    Long studentId = generatedKeys.getLong(1);
                    Map<String,Integer> studentGrades = student.getGrades();
                    Long studentMathClubId = student.getClubMembership().getClubId();

                    saveStudentGrades(studentId, studentGrades);
                    addStudentToClub(studentId, studentMathClubId);

                }
            }
        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod spremanja studenata u bazu podataka";
            logger.error(message, ex);
        }
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


}
