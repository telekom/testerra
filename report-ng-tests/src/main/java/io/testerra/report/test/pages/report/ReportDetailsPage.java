package io.testerra.report.test.pages.report;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ReportDetailsPage extends AbstractMethodReportPage {


    @Check
    private final GuiElement pageContent = new GuiElement(getWebDriver(), By.xpath("//router-view[@class='au-target']//mdc-layout-grid"));
    @Check
    private final GuiElement testFailureAspect = pageContent.getSubElement(By.xpath("//mdc-card[./div[text()='Failure Aspect']]"));

    //mandatory?
    @Check
    private final GuiElement testOriginCard = pageContent.getSubElement(By.xpath("//mdc-card[./div[contains(text(), 'Origin')]]"));

    @Check
    private final GuiElement testStacktraceCard = pageContent.getSubElement(By.xpath("//mdc-card[./div[contains(text(), 'Stacktrace')]]"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportDetailsPage(WebDriver driver) {
        super(driver);
    }

    public String getFailureAspect() {
        String x = testFailureAspect.getText();
        return x.split("\n")[1];
    }

    public void assertFailureAspectsCorrespondsToCorrectStatus(String expectedStatusTitle) {
        String expectedStatusTitleFormatted = expectedStatusTitle.toLowerCase();
        if (expectedStatusTitleFormatted.equals("expected failed")) expectedStatusTitleFormatted = "failed-expected";
        GuiElement failureAspectColoredPart = testFailureAspect.getSubElement(By.xpath("//div[contains(@class, 'status')]"));
        failureAspectColoredPart.asserts(
                        String.format("Failure Aspect status [%s] should correspond to method state [%s]",
                                failureAspectColoredPart.getAttribute("class"), expectedStatusTitleFormatted))
                .assertAttributeContains("class", expectedStatusTitleFormatted);
    }

    public ReportDetailsPage clickStacktraceDropdown() {
        GuiElement stacktraceDropdown = testStacktraceCard.getSubElement(By.xpath("//mdc-expandable//div[@class='mdc-expandable__caption']"));
        stacktraceDropdown.asserts().assertIsDisplayed();
        stacktraceDropdown.click();
        return PageFactory.create(ReportDetailsPage.class, getWebDriver());
    }

    public void assertStacktraceIsDisplayed() {
        GuiElement stacktrace = testStacktraceCard.getSubElement(By.xpath("//mdc-expandable//div[@class='code-view']"));
        stacktrace.asserts().assertIsDisplayed();
        Assert.assertTrue(stacktrace.getNumberOfFoundElements() > 0, "There should be some line of stack trace displayed!");
    }

    public void assertContainsCodeLines() {
        GuiElement failureOrigin = testOriginCard.getSubElement(By.xpath("//div[@class='code-view']"));
        failureOrigin.asserts().assertIsDisplayed();
        Assert.assertTrue(failureOrigin.getNumberOfFoundElements() > 0, "There should be some line of code displayed!");
    }

    public void assertFailLureLineIsMarked() {
        GuiElement markedFailureCodeLine = testOriginCard.getSubElement(By.xpath("//div[@class='code-view']//div[contains(@class,'error')]"));
        markedFailureCodeLine.asserts().assertIsDisplayed();
    }
}
