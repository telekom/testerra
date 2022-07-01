package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import io.testerra.report.test.pages.ReportMethodPageType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ReportDetailsTab extends AbstractReportMethodPage {

    public final ReportMethodPageType reportMethodPageType = ReportMethodPageType.DETAILS;
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

    @Override
    public ReportMethodPageType getCurrentPageType() {
        return reportMethodPageType;
    }

    @Override
    public void assertPageIsValid() {
        expandStacktrace();
        assertStacktraceIsDisplayed();
        // assertContainsCodeLines();
        // assertFailLureLineIsMarked();
    }

    public void detailPageAssertsFailureAspectsCorrespondsToCorrectStatus(String expectedStatusTitle) {
        String expectedStatusTitleFormatted = expectedStatusTitle.toLowerCase();
        // TODO: use Status Enum instead of hardcoded String
        if (expectedStatusTitleFormatted.equals("expected failed"))
            expectedStatusTitleFormatted = "failed-expected";
        GuiElement failureAspectColoredPart = testFailureAspect.getSubElement(By.xpath("//div[contains(@class, 'status')]"));
        failureAspectColoredPart.asserts(String.format("Failure Aspect status [%s] should correspond to method state [%s]", failureAspectColoredPart.getAttribute("class"), expectedStatusTitleFormatted)).assertAttributeContains("class", expectedStatusTitleFormatted);
    }

    public void detailsPageAssertsTestMethodContainsCorrectFailureAspect(String... correctFailureAspects) {
        String failureAspectCodeLineXPath = "//div[contains(@class,'line') and contains(@class,'error')]/span[@class='au-target']";
        GuiElement failureAspectCodeLine = testOriginCard.getSubElement(By.xpath(failureAspectCodeLineXPath));
        String failureAspectCodeLineAsString = failureAspectCodeLine.getText();
        for (String failureAspect : correctFailureAspects.clone()) {
            if (failureAspectCodeLineAsString.contains(failureAspect)) {
                //return implies assert is correct
                return;
            }
        }
        Assert.fail("One given failure aspect should match the code-line in origin-card!");
    }

    public void detailsPageAssertSkippedTestContainsCorrespondingFailureAspect() {
        boolean skippedTestContainsCorrespondingFailureAspect = skippedTestDependsOnFailedMethod() || skippedTestContainsSkipException() || skippedTestFailsInBeforeMethod() || skippedTestFailsInDataProvider();
        //at least one condition should be true
        Assert.assertTrue(skippedTestContainsCorrespondingFailureAspect, "One skipped condition should correspond to skipped test!");
    }

    private boolean skippedTestContainsSkipException() {
        String failureAspectCodeLineXPath = "//div[contains(@class,'line') and contains(@class,'error')]/span[@class='au-target']";
        GuiElement failureAspectCodeLine = testOriginCard.getSubElement(By.xpath(failureAspectCodeLineXPath));
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
