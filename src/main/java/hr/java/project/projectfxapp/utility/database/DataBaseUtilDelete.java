package hr.java.project.projectfxapp.utility.database;

import hr.java.project.projectfxapp.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseUtilDelete {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
    private static final String DATABASE_FILE = "conf/database.properties";

    private static Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));
        String urlDataBase = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(urlDataBase,
                username, password);

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
}
