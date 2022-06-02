package io.testerra.report.test.pages.report;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.utils.LogLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportLogsPage extends AbstractReportPage {

    @Check
    private final GuiElement testLogLevelSelect = pageContent.getSubElement(By.xpath("//mdc-select[@label='Log level']"));

    @Check
    private final GuiElement testSearchbarInput = pageContent.getSubElement(By.xpath("//label[@label='Search']//input"));

    @Check
    private final GuiElement testLogReportLines = pageContent.getSubElement(By.xpath("//virtual-log-view//compose"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportLogsPage(WebDriver driver) {
        super(driver);
    }

    public void assertLogReportContainsCorrectLogLevel(LogLevel logLevel) {
        testLogReportLines.getList()
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertTrue(i.contains(logLevel.getTitle()),
                        "Log report should contain only log statements with selected logLevel: " + logLevel.getTitle()));
    }

    public void assertLogReportIsCorrectWhenDifferentLogLevelAreSelected() {
        for (LogLevel level : LogLevel.values()) {
            selectDropBoxElement(testLogLevelSelect, level.getTitle());
            assertLogReportContainsCorrectLogLevel(level);
        }
    }

    public Set<String> getReportLines() {
        return testLogReportLines.getList()
                .stream()
                .map(GuiElement::getText)
                .map(i -> i.split("]:")[1])
                .map(i -> i.split("-")[1])
                .collect(Collectors.toSet());
    }

    public void assertMarkedLogLinesContainText(String expectedText) {
        List<GuiElement> markedLineParts = testLogReportLines.getSubElement(By.tagName("mark")).getList();
        Assert.assertTrue(markedLineParts.size() > 0, "There should be at least one mark in the report!");
        markedLineParts
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertTrue(i.contains(expectedText),
                        String.format("All highlighted text parts should contain the searchbar input.\n[Filter: %s]\n[Actual: %s]", expectedText, i)));
    }

    public void assertLogReportIsCorrectWhenSearchingForDifferentLogLines() {
        Set<String> reportLines = getReportLines();
        Assert.assertTrue(reportLines.size() > 0, "There should be at least one line!");
        for (String line : getReportLines()) {
            testSearchbarInput.type(line);
            TimerUtils.sleep(3000);
            assertMarkedLogLinesContainText(line);
            testSearchbarInput.clear();
        }
    }
}
