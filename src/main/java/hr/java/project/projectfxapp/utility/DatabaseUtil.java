package hr.java.project.projectfxapp.utility;

import hr.java.project.projectfxapp.entities.Address;
import hr.java.project.projectfxapp.entities.ClubMembership;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.enums.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
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

        List<Address> addresses = getAddresses();
        List<Student> students = FileReaderUtil.getStudentsFromFile();

        List<MathClub> mathClubs = new ArrayList<>();

        try(Connection connection = connectToDatabase()){
            String sqlQuery = "SELECT * FROM MATH_CLUB ";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlQuery);
            ResultSet rs = stmt.getResultSet();

            while(rs.next()){

                Long mathClubId = rs.getLong("CLUB_ID");
                String mathClubName = rs.getString("NAME");
                Long addressId = rs.getLong("ADDRESS_ID");

                Address mathClubAddress = addresses.stream()
                        .filter(address -> address.getAddressId().equals(addressId))
                        .findFirst()
                        .get();

                Set<Student> studentsForMathClub = getStudentsForMathClub(mathClubId, students);

                MathClub mathClub = new MathClub(mathClubId, mathClubName, mathClubAddress, studentsForMathClub);

                mathClubs.add(mathClub);
            }

        }catch (SQLException | IOException ex){
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        };

        return  mathClubs;

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
                String studentName = rs.getString("NAME");
                String studentSurname = rs.getString("SURNAME");
                String studentEmail = rs.getString("EMAIL");
                Integer yearOfStudy = rs.getInt("YEAR_OF_STUDY");

                Map<String, Integer> studentGrades = getStudentGrades(studentId);

                Long membershipId = rs.getLong("CLUB_MEMBERSHIP_ID");

                ClubMembership clubMembership = getStudentClubMembership(membershipId);

                Student newStudent = new Student(studentId, studentName, studentSurname, studentEmail,
                        yearOfStudy, studentGrades, clubMembership);

                students.add(newStudent);


            }


        } catch (SQLException | IOException ex) {
            String message = "Dogodila se pogreška kod povezivanja na bazu podataka";
            logger.error(message, ex);
        }

        return students;

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

}
