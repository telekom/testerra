package io.testerra.report.test.pages.report.historyPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class ReportTestRunTab extends AbstractReportHistoryPage {
    @Check
    private final UiElement testHistoryChart = pageContent.find(By.xpath(".//test-history-chart//echart[.//canvas]"));
    @Check
    private final UiElement testStatusSelect = pageContent.find(By.xpath(".//mdc-select[@label = 'Status']"));
    @Check
    protected final UiElement historyStatisticsCard = pageContent.find(By.xpath(".//history-statistics-card"));
    @Check
    private final UiElement statusShareChart = pageContent.find(By.xpath(".//status-share-chart//echart[.//canvas]"));
    @Check
    protected final UiElement topFlakyTestsCard = pageContent.find(By.xpath(".//mdc-card[.//div[text()=\"Top 3 flaky tests\"]]"));
    private final UiElement topFlakyTestsLink = topFlakyTestsCard.find(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));
    @Check
    protected final UiElement topFailingTestsCard = pageContent.find(By.xpath(".//mdc-card[.//div[text()=\"Top 3 failing tests\"]]"));
    private final UiElement topFailingTestsLink = topFailingTestsCard.find(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportTestRunTab(WebDriver driver) {
        super(driver);
    }

    private By getXpathToStatistics(String statisticsName) {
        final String xPathToStatisticsItemTemplate = ".//mdc-list-item[.//span[text()='%s']]";
        return By.xpath(String.format(xPathToStatisticsItemTemplate, statisticsName));
    }

    public void assertHistoryChartMatchesScreenshot(double pixelDistance) {
        this.testHistoryChart.expect().screenshot().pixelDistance("test_history_chart").isLowerThan(pixelDistance);
    }

    public List<String> getOrderListOfTopFlakyTests() {
        return topFlakyTestsLink.list()
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());
    }

    public List<String> getOrderListOfTopFailingTests() {
        return topFailingTestsLink.list()
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());
    }

    public void checkStatistics(String statisticsName, String value) {
        final UiElement testsStatusElement = historyStatisticsCard.find((getXpathToStatistics(statisticsName)));
        testsStatusElement.expect().text().contains(value).is(true);
    }
}
