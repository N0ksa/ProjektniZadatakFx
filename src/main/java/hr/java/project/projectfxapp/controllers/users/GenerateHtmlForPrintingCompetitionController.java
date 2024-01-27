package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import hr.java.project.projectfxapp.utility.html.HtmlGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.web.WebView;

public class GenerateHtmlForPrintingCompetitionController {
    @FXML
    private WebView pageForPrintingWebView;

    
    public void initialize() {

        // Assuming you have a Competition instance, replace it with your actual instance
        Competition competitionToPrint = SessionManager.getInstance().getCurrentCompetition();

        // Initialize the HtmlGenerator with the appropriate type (e.g., Competition)
        HtmlGenerator<Competition> htmlGenerator = new HtmlGenerator<>();

        // Generate HTML content for the Competition
        String competitionHtml = htmlGenerator.generateHtml(competitionToPrint);

        // Load HTML content into the WebView
        pageForPrintingWebView.getEngine().loadContent(competitionHtml);
    }


    public void print(ActionEvent actionEvent) {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null && printerJob.showPrintDialog(null)) {
            pageForPrintingWebView.getEngine().print(printerJob);
            printerJob.endJob();
        }
    }
}
