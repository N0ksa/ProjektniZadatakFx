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

        Competition competitionToPrint = SessionManager.getInstance().getCurrentCompetition();

        HtmlGenerator<Competition> htmlGenerator = new HtmlGenerator<>();

        String competitionHtml = htmlGenerator.generateHtml(competitionToPrint);

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
