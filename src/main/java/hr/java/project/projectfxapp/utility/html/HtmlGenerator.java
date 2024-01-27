package hr.java.project.projectfxapp.utility.html;

import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.CompetitionResult;
import hr.java.project.projectfxapp.entities.LoginStatistics;
import hr.java.project.projectfxapp.enums.ValidationRegex;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HtmlGenerator<T> {

    public String generateHtml(T item) {
        if (item instanceof Competition competition) {
            LocalDateTime now = LocalDateTime.now().minusDays(1);
            LocalDateTime competitionTime = competition.getTimeOfCompetition();
            if(now.isAfter(competitionTime)){
                return generateFinishedCompetitionHtml(competition);
            }
            else{
                return generateUpcomingCompetitionHtml(competition);
            }
            
        }
        else if (item instanceof List<?> && !((List<?>) item).isEmpty()) {
            Object firstItem = ((List<?>) item).get(0);
            if (firstItem instanceof LoginStatistics) {
                return generateLoginStatisticsHtml((List<LoginStatistics>) item);
            }
        }


        return generateContactTheAdministratorHtml();

    }

    private String generateContactTheAdministratorHtml() {
        StringBuilder htmlContent = new StringBuilder("<html><head><title>Error Page</title>");

        htmlContent.append("<style>")
                .append("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; color: #333; " +
                        "text-align: center; margin: 0; padding: 0; }")
                .append("header { background-color: #428bca; color: #fff; padding: 20px; }")
                .append("h1 { margin-bottom: 10px; }")
                .append("p { margin-top: 10px; font-size: 18px; }")
                .append("footer { background-color: #333; color: #fff; padding: 10px; margin-top: 20px; }")
                .append("</style>");

        htmlContent.append("</head><body>");

        htmlContent.append("<header>")
                .append("<h1>Nešto je pošlo po krivu</h1>")
                .append("</header>")
                .append("<p>Kontaktirajte administratora.</p>")
                .append("<footer> <p>&copy; 2023-2024. Sva prava pridržana.</p>");

        htmlContent.append("</body></html>");

        return htmlContent.toString();
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
                    .append("<td>").append("").append("</td>")
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
