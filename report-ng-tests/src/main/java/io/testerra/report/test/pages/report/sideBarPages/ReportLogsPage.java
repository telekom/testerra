package io.testerra.report.test.pages.report.sideBarPages;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.utils.LogLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
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
    private final GuiElement testLogReportLines = pageContent.getSubElement(By.xpath("//virtual-log-view"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportLogsPage(WebDriver driver) {
        super(driver);
    }

    // TODO: separate action and assert
    //  read Lines in test
    //  call utils class/method with read lines for asserting the expected LogLevel
    public void assertLogReportContainsCorrectLogLevel(LogLevel logLevel) {
        testLogReportLines.getList()
                .stream()
                .map(GuiElement::getText)
                .forEach(i -> Assert.assertTrue(i.contains(logLevel.getTitle()),
                        "Log report should contain only log statements with selected logLevel: " + logLevel.getTitle()));
    }

    // TODO: separate actions and asserts
    //  select Level per Level from Test, like per DataProvider
    //  assert from test after selction

    public void assertLogReportIsCorrectWhenDifferentLogLevelAreSelected() {
        for (LogLevel level : LogLevel.values()) {
            selectDropBoxElement(testLogLevelSelect, level.getTitle());
            assertLogReportContainsCorrectLogLevel(level);
        }
    }

    // TODO: evaluate if the searching constraints are ok
    public Set<String> getReportLines() {
        return testLogReportLines.getList()
                .stream()
                .map(GuiElement::getText)
                .map(i -> i.split("]:")[1])
                .map(i -> i.split("-")[1])
                .collect(Collectors.toSet());
    }

    // TODO: why assert at first --> can be separate check of needed in test
    public void assertLogReportIsCorrectWhenSearchingForDifferentLogLines() {
        Set<String> reportLines = getReportLines();
        Assert.assertTrue(reportLines.size() > 0, "There should be at least one line!");

        // TODO: separate action and asserts, no reading of existing lines and splitting
        //  dataprovider with expected values: search value from test (Enum with testnames, or use TestStatus,...)
        //  assert afterwards in test
        //  implement working search method on page with clear as first step
        //  implement correct POP: after input new Page is instantiated to avoid explicit sleeps

        for (String line : getReportLines()) {
            testSearchbarInput.type(line);
            TimerUtils.sleep(3000);
            assertMarkedLogLinesContainText(line);
            testSearchbarInput.clear();
        }
    }

    public void assertMarkedLogLinesContainText(String expectedText) {
        Actions a = new Actions(getWebDriver());
        for(int x = 0; x < 5; x++) {
            List<GuiElement> markedLineParts = testLogReportLines.getSubElement(By.xpath("//span//mark")).getList();
            if (markedLineParts
                    .stream()
                    .map(GuiElement::getText).anyMatch(i -> i.contains(expectedText))){
                return;
            }
            a.sendKeys(Keys.PAGE_DOWN).build().perform();
        }
        Assert.fail(String.format("There should be parts highlighted corresponding to the current filter.\n[Filter: %s]", expectedText));
    }


    public void search(String s) {
        testSearchbarInput.type(s.trim());
    }

    public void clearSearch() {
        testSearchbarInput.clear();
    }

}
