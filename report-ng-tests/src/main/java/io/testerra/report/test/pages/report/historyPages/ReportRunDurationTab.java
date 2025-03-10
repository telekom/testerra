package io.testerra.report.test.pages.report.historyPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class ReportRunDurationTab extends AbstractReportHistoryPage {
    @Check
    private final UiElement durationHistoryChart = pageContent.find(By.xpath(".//echart[.//canvas]"));
    @Check
    protected final UiElement topLongestRunsCard = pageContent.find(By.xpath(".//mdc-card[.//div[text()=\"Longest runs\"]]"));
    private final UiElement topLongestRunsLabel = topLongestRunsCard.find(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));
    @Check
    protected final UiElement topLongestTestsCard = pageContent.find(By.xpath(".//mdc-card[.//div[text()=\"Longest methods\"]]"));
    private final UiElement topLongestTestsLink = topLongestTestsCard.find(By.xpath("/mdc-list//span[@class='mdc-list-item__content']"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportRunDurationTab(WebDriver driver) {
        super(driver);
    }

    public List<String> getOrderListOfLongestRuns() {
        return topLongestRunsLabel.list()
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());
    }

    public List<String> getOrderListOfLongestTests() {
        return topLongestTestsLink.list()
                .stream()
                .map(uiElement -> uiElement.expect().text().getActual())
                .collect(Collectors.toList());
    }
}
