package io.testerra.report.test.pages.report.historyPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.report.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportTestClassesTab extends AbstractReportHistoryPage {
    @Check
    private final UiElement classesHistoryChart = pageContent.find(By.xpath(".//echart[.//canvas]"));
    @Check
    private final UiElement testClassSelect = pageContent.find(By.xpath(".//mdc-select[@label = 'Class']"));
    @Check
    private final UiElement testStatusSelect = pageContent.find(By.xpath(".//mdc-select[@label = 'Status']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportTestClassesTab(WebDriver driver) {
        super(driver);
    }

    public void assertClassesHistoryChartMatchesScreenshot(double pixelDistance, String referenceImageName) {
        this.classesHistoryChart.expect().screenshot().pixelDistance(referenceImageName).isLowerThan(pixelDistance);
    }

    public ReportTestClassesTab selectClassName(String label) {
        return selectDropBoxElement(this.testClassSelect, label, ReportTestClassesTab.class);
    }

    public ReportTestClassesTab selectTestStatus(Status status) {
        return selectDropBoxElement(this.testStatusSelect, status.title, ReportTestClassesTab.class);
    }
}
