package hr.java.project.projectfxapp.utility.database;

import hr.java.project.projectfxapp.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class DataBaseUtilUpdateAndSave {
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
                        "ADDRESS_ID, END_DATE) VALUES(?, ?, ?, ?, ?, ?)";

                PreparedStatement pstmt = connection.prepareStatement(insertMathProjectSql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, mathProject.getName());
                pstmt.setString(2, mathProject.getDescription());
                pstmt.setDate(3, Date.valueOf(mathProject.getStartDate()));
                pstmt.setLong(4, mathProject.getOrganizer().getId());
                pstmt.setLong(5, mathProject.getAddress().getAddressId());
                pstmt.setDate(6, mathProject.getEndDate() != null ? Date.valueOf(mathProject.getEndDate()) : null);
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

            updateAddress(competitionToUpdate.getAddress());
            updateCompetitionScores(competitionToUpdate.getId(), competitionToUpdate.getCompetitionResults());


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
            return false;
        }

        return true;
    }

    private static void updateAddress(Address address) {
        try (Connection connection = connectToDatabase()) {
            String updateQuery = "UPDATE ADDRESS SET STREET = ?, HOUSE_NUMBER = ?, CITY = ? WHERE ADDRESS_ID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, address.getStreet());
                preparedStatement.setString(2, address.getHouseNumber());
                preparedStatement.setString(3, address.getCity().getName());
                preparedStatement.setLong(4, address.getAddressId());

                preparedStatement.executeUpdate();
            }

        } catch (SQLException | IOException ex) {
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
            String updateQuery = "UPDATE MATH_PROJECT  SET NAME = ?, DESCRIPTION = ?, END_DATE = ? WHERE PROJECT_ID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, projectToUpdate.getName());
                preparedStatement.setString(2, projectToUpdate.getDescription());
                preparedStatement.setDate(3, projectToUpdate.getEndDate() != null ?
                        Date.valueOf(projectToUpdate.getEndDate()) : null);
                preparedStatement.setLong(4, projectToUpdate.getId());

                preparedStatement.executeUpdate();
            }


            updateAddress(projectToUpdate.getAddress());


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


}
