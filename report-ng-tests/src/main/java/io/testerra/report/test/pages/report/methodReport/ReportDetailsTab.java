package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.Locale;

public class ReportDetailsTab extends AbstractReportMethodPage {
    @Check
    private final GuiElement pageContent = new GuiElement(getWebDriver(), By.xpath("//router-view[@class='au-target']//mdc-layout-grid"));
    @Check
    private final GuiElement testFailureAspect = pageContent.getSubElement(By.xpath("//mdc-card[./div[text()='Failure Aspect']]"));
    //TODO: mandatory?
    private final GuiElement testOriginCard = pageContent.getSubElement(By.xpath("//mdc-card[./div[contains(text(), 'Origin')]]"));
    @Check
    private final GuiElement testStacktraceCard = pageContent.getSubElement(By.xpath("//mdc-card[./div[contains(text(), 'Stacktrace')]]"));

    public ReportDetailsTab(WebDriver driver) {
        super(driver);
    }

    public void assertFailureAspectsCorrespondsToCorrectStatus(String expectedStatusTitle) {
        String expectedStatusTitleFormatted = expectedStatusTitle.toLowerCase();
        if (expectedStatusTitleFormatted.equals(Status.FAILED_EXPECTED.title.toLowerCase(Locale.ROOT)))
            expectedStatusTitleFormatted = "failed-expected";
        GuiElement failureAspectColoredPart = testFailureAspect.getSubElement(By.xpath("//div[contains(@class, 'status')]"));
        failureAspectColoredPart.asserts(String.format("Failure Aspect status [%s] should correspond to method state [%s]", failureAspectColoredPart.getAttribute("class"), expectedStatusTitleFormatted)).assertAttributeContains("class", expectedStatusTitleFormatted);
    }

    public void assertTestMethodContainsCorrectFailureAspect(String correctFailureAspect) {
        String failureAspectCodeLineXPath = "//div[contains(@class,'line') and contains(@class,'error')]/span[@class='au-target']";
        GuiElement failureAspectCodeLine = testOriginCard.getSubElement(By.xpath(failureAspectCodeLineXPath));
        String failureAspectCodeLineAsString = failureAspectCodeLine.getText();
        Assert.assertTrue(failureAspectCodeLineAsString.contains(correctFailureAspect),
                "Given failure aspect should match the code-line in origin-card!");
    }

    public void assertSkippedTestContainsCorrespondingFailureAspect() {
        boolean skippedTestContainsCorrespondingFailureAspect = skippedTestDependsOnFailedMethod() || skippedTestContainsSkipException() || skippedTestFailsInBeforeMethod() || skippedTestFailsInDataProvider();
        //at least one condition should be true
        Assert.assertTrue(skippedTestContainsCorrespondingFailureAspect, "One skipped condition should correspond to skipped test!");
    }

    private boolean skippedTestContainsSkipException() {
        String failureAspectCodeLineXPath = "//div[contains(@class,'line') and contains(@class,'error')]/span[@class='au-target']";
        GuiElement failureAspectCodeLine = testOriginCard.getSubElement(By.xpath(failureAspectCodeLineXPath));
        // TODO: why isDisplayed? if needed: better with ternary: return isDisplayed? getText : false
        if (failureAspectCodeLine.isDisplayed()) return failureAspectCodeLine.getText().contains("SkipException");
        return false;

    }

    private boolean skippedTestFailsInDataProvider() {
        //check whether code contains '@DataProvider'
        GuiElement dataProviderCode = testOriginCard.getSubElement(By.xpath("//div[contains(@class,'line')]//span[contains(text(),'@DataProvider')]"));
        return dataProviderCode.isDisplayed();
    }

    private boolean skippedTestFailsInBeforeMethod() {
        //check whether code contains '@BeforeMethod()'
        GuiElement beforeMethodCode = testOriginCard.getSubElement(By.xpath("//div[contains(@class,'line')]//span[contains(text(),'@BeforeMethod()')]"));
        return beforeMethodCode.isDisplayed();
    }

    private boolean skippedTestDependsOnFailedMethod() {
        String failureAspect = testFailureAspect.getText().split("\n")[1];
        String expectedContainedText = "depends on not successfully finished methods";
        return failureAspect.contains(expectedContainedText);
    }

    public void assertPageIsValid(){
        expandStacktrace();
        assertStacktraceIsDisplayed();
        assertContainsCodeLines();
        assertFailLureLineIsMarked();
    }

    private void expandStacktrace() {
        GuiElement expander = testStacktraceCard.getSubElement(By.xpath("//mdc-expandable"));
        expander.asserts().assertIsDisplayed();
        expander.click();
    }

    private void assertStacktraceIsDisplayed() {
        GuiElement stacktrace = testStacktraceCard.getSubElement(By.xpath("//mdc-expandable//div[@class='code-view']"));
        stacktrace.asserts().assertIsDisplayed();
        Assert.assertTrue(stacktrace.getNumberOfFoundElements() > 0, "There should be some line of stack trace displayed!");
    }

    private void assertContainsCodeLines() {
        GuiElement failureOrigin = testOriginCard.getSubElement(By.xpath("//div[@class='code-view']"));
        failureOrigin.asserts().assertIsDisplayed();
        Assert.assertTrue(failureOrigin.getNumberOfFoundElements() > 0, "There should be some line of code displayed!");
    }

    private void assertFailLureLineIsMarked() {
        GuiElement markedFailureCodeLine = testOriginCard.getSubElement(By.xpath("//div[@class='code-view']//div[contains(@class,'error')]"));
        markedFailureCodeLine.asserts().assertIsDisplayed();
    }

    public String getFailureAspect() {
        return testFailureAspect.getText().split("\n")[1];
    }
}
