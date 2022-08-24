package io.testerra.report.test.pages.report.sideBarPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.utils.LogLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

public class ReportLogsPage extends AbstractReportPage {

    @Check
    private final GuiElement testLogLevelSelect = pageContent.getSubElement(By.xpath("//mdc-select[@label='Log level']"));

    @Check
    private final GuiElement testSearchbarInput = pageContent.getSubElement(By.xpath("//label[@label='Search']//input"));

    @Check
    private final GuiElement testLogReportLines = pageContent.getSubElement(By.xpath("//virtual-log-view"));

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

    public void assertMarkedLogLinesContainText(String expectedText) {
        Actions a = new Actions(getWebDriver());
        boolean allLogLinesMarkedAsExpected = true;

        for (int x = 0; x < 5; x++) { //5 scrolls are enough to reach page bottom and cover all log lines
            final GuiElement markedLineParts = testLogReportLines.getSubElement(By.xpath("//span//mark"));
            if (markedLineParts.isDisplayed()) {
                if (!markedLineParts.getList()
                        .stream()
                        .map(GuiElement::getText)
                        .map(String::toUpperCase)
                        .allMatch(i -> i.contains(expectedText.toUpperCase()))) {
                    allLogLinesMarkedAsExpected = false;
                    break;
                }
            }
            a.sendKeys(Keys.PAGE_DOWN).build().perform();
        }
        Assert.assertTrue(allLogLinesMarkedAsExpected, String.format("There should be parts highlighted corresponding to the current filter.\n[Filter: %s]", expectedText));
    }

    public ReportLogsPage search(String s) {
        testSearchbarInput.clear();
        testSearchbarInput.type(s.trim());
        return PageFactory.create(ReportLogsPage.class, getWebDriver());
    }

    public ReportLogsPage selectLogLevel(LogLevel logLevel) {
        return selectDropBoxElement(this.testLogLevelSelect, logLevel.getTitle(), ReportLogsPage.class);
    }
}
