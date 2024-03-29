package hr.java.project.projectfxapp.utility.html;

import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HtmlGenerator<T> {
    public String generateHtml(T item) {
        if (item instanceof Competition competition) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime competitionTime = competition.getTimeOfCompetition();
            if (now.isAfter(competitionTime)) {
                return generateFinishedCompetitionHtml(competition);
            } else {
                return generateUpcomingCompetitionHtml(competition);
            }

        } else if (item instanceof List<?> && !((List<?>) item).isEmpty()) {
            Object firstItem = ((List<?>) item).get(0);
            if (firstItem instanceof LoginStatistics) {
                return generateLoginStatisticsHtml((List<LoginStatistics>) item);
            }
        } else if (item instanceof Student student) {
            return generateStudentHtml(student);
        }


        return generateContactTheAdministratorHtml();

    }
    private String generateContactTheAdministratorHtml() {

        String htmlContent = "<html><head><title>Error Page</title>" + "<style>" +
                "body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; color: #333; " +
                "text-align: center; margin: 0; padding: 0; }" +
                "header { background-color: #428bca; color: #fff; padding: 20px; }" +
                "h1 { margin-bottom: 10px; }" +
                "p { margin-top: 10px; font-size: 18px; }" +
                "footer { background-color: #333; color: #fff; padding: 10px; margin-top: 20px; }" +
                "</style>" +
                "</head><body>" +
                "<header>" +
                "<h1>Nešto je pošlo po krivu</h1>" +
                "</header>" +
                "<p>Kontaktirajte administratora.</p>" +
                "<footer> <p>&copy; 2023-2024. Sva prava pridržana.</p>" +
                "</body></html>";

        return htmlContent;
    }

    public String generateStudentHtml(Student student) {
        List<Competition> competitions = DatabaseUtil.getCompetitions();
        LocalDate now = LocalDate.now();
        competitions.removeIf(competition -> competition.getTimeOfCompetition().toLocalDate().isAfter(now));
        List<MathProject> projects = DatabaseUtil.getProjects();


        StringBuilder htmlContent = new StringBuilder("<html><head><title>Student Details</title>");

        htmlContent.append("<style>")
                .append("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; color: #333; " +
                        "text-align: center; margin: 0; padding: 0; }")
                .append("header { background-color: #428bca; color: #fff; padding: 20px; }")
                .append("h1 { margin-bottom: 10px; }")
                .append("p { margin-top: 10px; font-size: 18px; }")
                .append("table { border-collapse: collapse; width: 100%; }")
                .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                .append("th { background-color: #428bca; color: #fff; }")
                .append("footer { background-color: #333; color: #fff; padding: 10px; margin-top: 20px; }")
                .append("</style>");

        htmlContent.append("</head><body>");

        htmlContent.append("<header>")
                .append("<h1>Statistika člana</h1>")
                .append("</header>")
                .append("<p><strong>Ime:</strong> ").append(student.getName()).append("</p>")
                .append("<p><strong>Prezime:</strong> ").append(student.getSurname()).append("</p>")
                .append("<p><strong>Spol:</strong> ").append(student.getGender()).append("</p>")
                .append("<p><strong>Email:</strong> ").append(student.getEmail()).append("</p>")
                .append("<p><strong>Godina studija:</strong> ").append(student.getYearOfStudy()).append("</p>")
                .append("<p><strong>Prosječna ocjena:</strong> ").append(student.calculateAverageGrade()).append("</p>")
                .append("<p><strong>Članstvo:</strong> ").append(student.getClubMembership()).append("</p>");

        htmlContent.append("<h2>Ocjene</h2>");

        htmlContent.append("<table>")
                .append("<tr>")
                .append("<th>Predmet</th>")
                .append("<th>Ocjena</th>")
                .append("</tr>");

        for (Map.Entry<String, Integer> entry : student.getGrades().entrySet()) {
            htmlContent.append("<tr>")
                    .append("<td>").append(entry.getKey()).append("</td>")
                    .append("<td>").append(entry.getValue()).append("</td>")
                    .append("</tr>");
        }

        htmlContent.append("</table>");

        htmlContent.append("<h2>Dodatne statistike</h2>")
                .append("<p><strong>Broj pobjeda na natjecanjima:</strong> ")
                .append(getCompetitionWinNumber(competitions, student)).append("</p>")
                .append("<p><strong>Broj sudjelovanja u projektima:</strong> ")
                .append(getNumberOfParticipationInProject(projects, student)).append("</p>")
                .append("<p><strong>Najniža ostvarena ocjena na natjecanjima:</strong> ");

        getLowestScore(competitions, student).ifPresentOrElse(
                htmlContent::append,
                () -> htmlContent.append("Član nije sudjelovao na natjecanju")
        );

        htmlContent.append("</p>")
                .append("<p><strong>Najviša ostvarena ocjena na natjecanjima:</strong> ");

        getHighestScore(competitions, student).ifPresentOrElse(
                htmlContent::append,
                () -> htmlContent.append("Član nije sudjelovao na natjecanju")
        );

        htmlContent.append("</p>");

        htmlContent.append("<footer>")
                .append("<p>&copy; 2023-2024. Sva prava pridržana.</p>")
                .append("<p>Projekt Matematika</p>")
                .append("</footer>")
                .append("</body></html>");

        return htmlContent.toString();
    }

    private Integer getCompetitionWinNumber(List<Competition> competitions, Student currentStudent) {
        Integer numberOfWins = 0;
        for (Competition competition : competitions) {
            Optional<Student> winner = competition.findWinner();
            if (winner.isPresent() && winner.get().equals(currentStudent)) {
                numberOfWins++;
            }
        }
        return numberOfWins;
    }


    private Integer getNumberOfParticipationInProject(List<MathProject> projects, Student currentStudent) {
        Integer numberOfParticipations = 0;
        for (MathProject project : projects) {
            if (project.hasStudentCollaborator(currentStudent)) {
                numberOfParticipations++;
            }
        }
        return numberOfParticipations;
    }


    private Optional<BigDecimal> getLowestScore(List<Competition> competitions, Student currentStudent) {
        BigDecimal lowestScore = null;
        boolean hasParticipated = false;
        for (Competition competition : competitions) {
            if (competition.hasParticipant(currentStudent)) {
                hasParticipated = true;
                BigDecimal competitionScore = competition.getCompetitionResultForParticipant(currentStudent).get().score();

                if (lowestScore == null || competitionScore.compareTo(lowestScore) < 0) {
                    lowestScore = competitionScore;
                }
            }
        }
        if (hasParticipated) {
            return Optional.ofNullable(lowestScore);
        } else {
            return Optional.empty();
        }
    }

    private Optional<BigDecimal> getHighestScore(List<Competition> competitions, Student currentStudent) {
        BigDecimal highestScore = null;
        boolean hasParticipated = false;
        for (Competition competition : competitions) {
            if (competition.hasParticipant(currentStudent)) {
                hasParticipated = true;
                BigDecimal competitionScore = competition.getCompetitionResultForParticipant(currentStudent).get().score();

                if (highestScore == null || competitionScore.compareTo(highestScore) > 0) {
                    highestScore = competitionScore;
                }
            }
        }
        if (hasParticipated) {
            return Optional.ofNullable(highestScore);
        } else {
            return Optional.empty();
        }
    }


    private String generateLoginStatisticsHtml(List<LoginStatistics> item) {
        StringBuilder htmlContent = new StringBuilder("<html><head><title>Statistika prijava</title>");

        htmlContent.append("<style>")
                .append("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; color: #333; " +
                        "text-align: center; margin: 0; padding: 0; }")
                .append("header { background-color: #428bca; color: #fff; padding: 20px; }")
                .append("h1 { margin-bottom: 10px; }")
                .append("p { margin-top: 10px; font-size: 18px; }")
                .append("table { border-collapse: collapse; width: 100%; }")
                .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                .append("th { background-color: #428bca; color: #fff; }")
                .append("footer { background-color: #333; color: #fff; padding: 10px; margin-top: 20px; }")
                .append("</style>");

        htmlContent.append("</head><body>");

        htmlContent.append("<header>")
                .append("<h1>Statistika prijava</h1>")
                .append("</header>");

        htmlContent.append("<h2>Tablica prijava</h2>");

        htmlContent.append("<table>")
                .append("<tr>")
                .append("<th>Korisnik</th>")
                .append("<th>Vrijeme prijave</th>")
                .append("<th>Vrijeme odjave</th>")
                .append("<th>Trajanje prijave</th>")
                .append("</tr>");

        for (LoginStatistics statistics : item) {
            htmlContent.append("<tr>")
                    .append("<td>").append(statistics.username()).append("</td>")
                    .append("<td>").append(statistics.loginTime().format(DateTimeFormatter.
                            ofPattern(ValidationRegex.VALID_LOCAL_DATE_TIME_REGEX.getRegex()))).append("</td>")
                    .append("<td>").append(statistics.logoutTime().format(DateTimeFormatter.
                            ofPattern(ValidationRegex.VALID_LOCAL_DATE_TIME_REGEX.getRegex()))).append("</td>")
                    .append("<td>").append(calculateDuration(statistics.loginDuration())).append("</td>")
                    .append("</tr>");
        }

        htmlContent.append("</table>");

        htmlContent.append("</body></html>");

        return htmlContent.toString();
    }

    private String calculateDuration(Integer integer) {
        int hours = integer / 3600;
        int minutes = (integer % 3600) / 60;
        int seconds = integer % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    private String generateUpcomingCompetitionHtml(Competition competition) {
        StringBuilder htmlContent = new StringBuilder("<html><head><title>Detalji natjecanja</title>");

        htmlContent.append("<style>")
                .append("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; color: #333; " +
                        "text-align: center; margin: 0; padding: 0; }")
                .append("header { background-color: #428bca; color: #fff; padding: 20px; }")
                .append("h1 { margin-bottom: 10px; }")
                .append("p { margin-top: 10px; font-size: 18px; }")
                .append("table { border-collapse: collapse; width: 100%; }")
                .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                .append("th { background-color: #428bca; color: #fff; }")
                .append("footer { background-color: #333; color: #fff; padding: 10px; margin-top: 20px; }")
                .append("</style>");

        htmlContent.append("</head><body>");

        htmlContent.append("<header>")
                .append("<h1>Detalji natjecanja</h1>")
                .append("</header>")
                .append("<p><strong>Naziv:</strong> ").append(competition.getName()).append("</p>")
                .append("<p><strong>Organizator:</strong> ").append(competition.getOrganizer().getName()).append("</p>")
                .append("<p><strong>Opis:</strong> ").append(competition.getDescription()).append("</p>")
                .append("<p><strong>Adresa:</strong> ").append(competition.getAddress()).append("</p>")
                .append("<p><strong>Zgrada:</strong> ").append(competition.getAuditorium().building()).append("</p>")
                .append("<p><strong>Dvorana:</strong> ").append(competition.getAuditorium().hall()).append("</p>")
                .append("<p><strong>Vrijeme natjecanja:</strong> ").append(competition.getTimeOfCompetition()
                        .format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_TIME_REGEX.getRegex()))).append("</p>")
                .append("<p><strong>Broj sudionika:</strong> ").append(competition.getNumberOfParticipants()).append("</p>")
                .append("<p><strong>Prosječan rezultat:</strong> ").append("___________").append("</p>")
                .append("<p><strong>Prosječan rezultat žena:</strong> ").append("___________").append("</p>")
                .append("<p><strong>Prosječan rezultat muškaraca:</strong> ").append("___________").append("</p>")
                .append("<h2>Rezultati</h2>");

        htmlContent.append("<table>")
                .append("<tr>")
                .append("<th>Sudionik</th>")
                .append("<th>Rezultat</th>")
                .append("</tr>");


        for (CompetitionResult result : competition.getCompetitionResults()) {
            htmlContent.append("<tr>")
                    .append("<td>").append(result.participant().getName())
                    .append(" ")
                    .append(result.participant().getSurname()).append("</td>")
                    .append("<td>").append("</td>")
                    .append("</tr>");
        }

        htmlContent.append("</table>");

        htmlContent.append("<footer>")
                .append("<p>&copy; 2023-2024. Sva prava pridržana.</p>")
                .append("<p>Projekt Matematika</p>")
                .append("</footer>")
                .append("</body></html>");


        return htmlContent.toString();
    }


    private String generateFinishedCompetitionHtml(Competition competition) {
        StringBuilder htmlContent = new StringBuilder("<html><head><title>Detalji natjecanja</title>");

        htmlContent.append("<style>")
                .append("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; color: #333; " +
                        "text-align: center; margin: 0; padding: 0; }")
                .append("header { background-color: #428bca; color: #fff; padding: 20px; }")
                .append("h1 { margin-bottom: 10px; }")
                .append("p { margin-top: 10px; font-size: 18px; }")
                .append("table { border-collapse: collapse; width: 100%; }")
                .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                .append("th { background-color: #428bca; color: #fff; }")
                .append("footer { background-color: #333; color: #fff; padding: 10px; margin-top: 20px; }")
                .append("</style>");

        htmlContent.append("</head><body>");

        htmlContent.append("<header>")
                .append("<h1>Detalji natjecanja</h1>")
                .append("</header>")
                .append("<p><strong>Naziv:</strong> ").append(competition.getName()).append("</p>")
                .append("<p><strong>Organizator:</strong> ").append(competition.getOrganizer().getName()).append("</p>")
                .append("<p><strong>Opis:</strong> ").append(competition.getDescription()).append("</p>")
                .append("<p><strong>Adresa:</strong> ").append(competition.getAddress()).append("</p>")
                .append("<p><strong>Zgrada:</strong> ").append(competition.getAuditorium().building()).append("</p>")
                .append("<p><strong>Dvorana:</strong> ").append(competition.getAuditorium().hall()).append("</p>")
                .append("<p><strong>Vrijeme natjecanja:</strong> ").append(competition.getTimeOfCompetition()
                        .format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_TIME_REGEX.getRegex()))).append("</p>")
                .append("<p><strong>Broj sudionika:</strong> ").append(competition.getNumberOfParticipants()).append("</p>")
                .append("<p><strong>Prosječan rezultat:</strong> ").append(competition.getAverageScoreForCompetition()).append("</p>")
                .append("<p><strong>Prosječan rezultat žena:</strong> ").append
                        (competition.getAverageFemaleScoreForCompetition()).append("</p>")
                .append("<p><strong>Prosječan rezultat muškaraca:</strong> ")
                .append(competition.getAverageMaleScoreForCompetition()).append("</p>")
                .append("<h2>Rezultati</h2>");


        htmlContent.append("<table>")
                .append("<tr>")
                .append("<th>Sudionik</th>")
                .append("<th>Rezultat</th>")
                .append("</tr>");

        for (CompetitionResult result : competition.getCompetitionResults()) {
            htmlContent.append("<tr>")
                    .append("<td>").append(result.participant().getName())
                    .append(" ")
                    .append(result.participant().getSurname()).append("</td>")
                    .append("<td>").append(result.score()).append("</td>")
                    .append("</tr>");
        }

        htmlContent.append("</table>");


        htmlContent.append("<footer>")
                .append("<p>&copy; 2023-2024. Sva prava pridržana.</p>")
                .append("<p>Projekt Matematika</p>")
                .append("</footer>")
                .append("</body></html>");

        return htmlContent.toString();
    }


}
