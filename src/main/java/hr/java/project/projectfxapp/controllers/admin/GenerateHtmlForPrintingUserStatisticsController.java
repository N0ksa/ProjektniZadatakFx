package hr.java.project.projectfxapp.controllers.admin;

import hr.java.project.projectfxapp.entities.LoginStatistics;
import hr.java.project.projectfxapp.utility.files.SerializationUtil;
import hr.java.project.projectfxapp.utility.html.HtmlGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.web.WebView;

import java.util.List;

public class GenerateHtmlForPrintingUserStatisticsController {

    @FXML
    private WebView pageForPrintingWebView;

    public void initialize() {
        List<LoginStatistics> statisticsToPrint = SerializationUtil.deserializeLoginStatisticsList();
        HtmlGenerator<List<LoginStatistics>> htmlGenerator = new HtmlGenerator<>();
        String statisticsHtml = htmlGenerator.generateHtml(statisticsToPrint);
        pageForPrintingWebView.getEngine().loadContent(statisticsHtml);

    }

    public void print(ActionEvent actionEvent) {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null && printerJob.showPrintDialog(null)) {
            pageForPrintingWebView.getEngine().print(printerJob);
            printerJob.endJob();
        }
    }
}
