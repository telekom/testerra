package io.testerra.report.test.pages.report.historyPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportRunDurationTab extends AbstractReportHistoryPage {
    @Check
    private final UiElement durationHistoryChart = pageContent.find(By.xpath(".//echart[.//canvas]"));
    @Check
    protected final UiElement topFlakyTestsCard = pageContent.find(By.xpath(".//mdc-card[.//div[text()=\"Longest runs\"]]"));
    @Check
    protected final UiElement topFailingTestsCard = pageContent.find(By.xpath(".//mdc-card[.//div[text()=\"Longest methods\"]]"));
    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportRunDurationTab(WebDriver driver) {
        super(driver);
    }
}
