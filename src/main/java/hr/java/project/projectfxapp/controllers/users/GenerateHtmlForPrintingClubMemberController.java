package hr.java.project.projectfxapp.controllers.users;

import hr.java.project.projectfxapp.entities.Student;
import hr.java.project.projectfxapp.utility.html.HtmlGenerator;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.web.WebView;

public class GenerateHtmlForPrintingClubMemberController {

    @FXML
    private WebView pageForPrintingWebView;

    public void initialize() {
        Student memberToPrint = SessionManager.getInstance().getCurrentStudent();
        HtmlGenerator<Student> htmlGenerator = new HtmlGenerator<>();
        String html = htmlGenerator.generateHtml(memberToPrint);
        pageForPrintingWebView.getEngine().loadContent(html);
    }

    public void print(ActionEvent actionEvent) {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null && printerJob.showPrintDialog(null)) {
            pageForPrintingWebView.getEngine().print(printerJob);
            printerJob.endJob();
        }
    }
}
