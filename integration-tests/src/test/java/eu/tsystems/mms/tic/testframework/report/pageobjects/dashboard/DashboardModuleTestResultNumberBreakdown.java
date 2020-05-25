package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractFramePage;
import eu.tsystems.mms.tic.testframework.report.model.MouseAction;
import eu.tsystems.mms.tic.testframework.report.model.TestNumberHelper;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardModuleTestResultNumberBreakdown extends AbstractFramePage {

    // Test Numbers
    @Check
    public final GuiElement numberAllTests = new GuiElement(this.getWebDriver(), By.id("totalNumberOfTestMethods"), mainFrame);

    public final GuiElement numberOfAllSuccessfulTests = new GuiElement(this.getWebDriver(), By.id("totalNumberOfSuccessfulMethods"), mainFrame);
    public final GuiElement numberPassedTests = new GuiElement(this.getWebDriver(), By.id("numberOfPASSED"), mainFrame);
    public final GuiElement numberPassedMinorTests = new GuiElement(this.getWebDriver(), By.id("numberOfMINOR"), mainFrame);
    public final GuiElement numberPassedRetryTests = new GuiElement(this.getWebDriver(), By.id("numberOfPASSED_RETRY"), mainFrame);

    public final GuiElement numberAllSkippedTests = new GuiElement(this.getWebDriver(), By.id("totalNumberOfSkippedMethods"), mainFrame);
    public final GuiElement numberSkippedTests = new GuiElement(this.getWebDriver(), By.id("numberOfSKIPPED"), mainFrame);

    public final GuiElement numberOfAllBrokenTests = new GuiElement(this.getWebDriver(), By.id("totalNumberOfFailedMethods"), mainFrame);
    public final GuiElement numberFailedTests = new GuiElement(this.getWebDriver(), By.id("numberOfFAILED"), mainFrame);
    public final GuiElement numberFailedMinorTests = new GuiElement(this.getWebDriver(), By.id("numberOfFAILED_MINOR"), mainFrame);
    public final GuiElement numberFailedRetriedTests = new GuiElement(this.getWebDriver(), By.id("numberOfFAILED_RETRIED"), mainFrame);
    public final GuiElement numberFailedExpectedTests = new GuiElement(this.getWebDriver(), By.id("numberOfFAILED_EXPECTED"), mainFrame);

    public final GuiElement numberExitPoints = new GuiElement(this.getWebDriver(), By.xpath("//*[@id='exitPointsLink']/a"), mainFrame);
    public final GuiElement numberFailureAspects = new GuiElement(this.getWebDriver(), By.xpath("//*[@id='failureAspectsLink']/a"), mainFrame);

    // Test time-data
    @Check
    public final GuiElement testDurationString = new GuiElement(this.getWebDriver(), By.id("actualRunDuration"), mainFrame);
    @Check
    public final GuiElement testStartTimeString = new GuiElement(this.getWebDriver(), By.id("actualRunStartTime"), mainFrame);
    @Check
    public final GuiElement testEndTimeString = new GuiElement(this.getWebDriver(), By.id("actualRunEndTime"), mainFrame);

    // Percentages
    public final GuiElement testPercentageString = new GuiElement(this.getWebDriver(), By.id("actualRunPassRate"), mainFrame);

    public DashboardModuleTestResultNumberBreakdown(WebDriver driver) {
        super(driver);
    }

    /**
     * Checks if a specific number for a specific testresult is displayed.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return the correctness of the assumption for the testresult
     * @throws Exception
     */
    public boolean isNumberDisplayed(TestResultHelper.TestResult testResult) throws Exception {
        boolean isDisplayed;
        switch (testResult) {
            case PASSED:
                isDisplayed = numberPassedTests.isDisplayed();
                break;
            case PASSEDMINOR:
                isDisplayed = numberPassedMinorTests.isDisplayed();
                break;
            case PASSEDRETRY:
                isDisplayed = numberPassedRetryTests.isDisplayed();
                break;
            case SKIPPED:
                isDisplayed = numberSkippedTests.isDisplayed();
                break;
            case FAILED:
                isDisplayed = numberFailedTests.isDisplayed();
                break;
            case FAILEDMINOR:
                isDisplayed = numberFailedMinorTests.isDisplayed();
                break;
            case RETRIED:
                isDisplayed = numberFailedRetriedTests.isDisplayed();
                break;
            case FAILEDEXPECTED:
                isDisplayed = numberFailedExpectedTests.isDisplayed();
                break;
            default:
                throw new TesterraRuntimeException("Unsupported Test Counter for TestResult: " + testResult);
        }
        return isDisplayed;
    }

    /**
     * Triggers the mouseOver Event depending on a given testresult category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return an updated DashboardPage Object
     * @throws Exception
     */
    public DashboardPage hoverNumberForTestResult(TestResultHelper.TestResult testResult) throws Exception {
        return triggerNumber(testResult, MouseAction.MOUSE_OVER);
    }

    /**
     * Triggers the click Event depending on a given testresult category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @return an updated DashboardPage Object
     * @throws Exception
     */
    public DashboardPage clickNumberForTestResult(TestResultHelper.TestResult testResult) throws Exception {
        return triggerNumber(testResult, MouseAction.CLICK);
    }

    /**
     * Triggers a given Event for a number depending on a given testresult category.
     *
     * @param testResult the result category (Passed, Failed, Failed Inherited, ...)
     * @param mouseAction the intended mouseAction
     * @return an updated DashboardPage Object
     * @throws
     */
    public DashboardPage triggerNumber(TestResultHelper.TestResult testResult, MouseAction mouseAction) throws Exception {
        GuiElement counter;
        switch (testResult) {
            case PASSED:
                counter = numberPassedTests;
                break;
            case PASSEDMINOR:
                counter = numberPassedMinorTests;
                break;
            case PASSEDRETRY:
                counter = numberPassedRetryTests;
                break;
            case SKIPPED:
                counter = numberSkippedTests;
                break;
            case FAILED:
                counter = numberFailedTests;
                break;
            case FAILEDMINOR:
                counter = numberFailedMinorTests;
                break;
            case RETRIED:
                counter = numberFailedRetriedTests;
                break;
            case FAILEDEXPECTED:
                counter = numberFailedExpectedTests;
                break;
            default:
                throw new TesterraRuntimeException("Unsupported Test Counter for TestResult: " + testResult);
        }
        switch (mouseAction) {
            case CLICK:
                counter.click();
                break;
            case MOUSE_OVER:
                counter.mouseOver();
                break;
            default:
                throw new TesterraRuntimeException("Unsupported mouseAction: " + mouseAction.name());
        }
        counter.click();
        return PageFactory.create(DashboardPage.class, this.getWebDriver());
    }

    public void assertTestNumbers(TestNumberHelper testNumberHelper) {

        //check numbers of passed, failed and skipped tests
        assertCoreTestNumbers(testNumberHelper);

        //check number of failure aspects and exit points
        AssertCollector.assertEquals(eliminateNumbersOfFaiuresFromLink(numberFailureAspects.getText()), testNumberHelper.getFailureAspects(), "The number failure aspects is NOT correct.");
        AssertCollector.assertEquals(eliminateNumbersOfFaiuresFromLink(numberExitPoints.getText()), testNumberHelper.getExitPoints(), "The number of exit points is NOT correct.");

    }

    public void assertCoreTestNumbers(TestNumberHelper testNumberHelper) {

        /* ALL */
        int actualAll = Integer.parseInt(numberAllTests.getText());
        AssertCollector.assertEquals(actualAll, testNumberHelper.getAll(), "The number of overall tests is NOT correct.");

        /* PASSED */
        //check all successful
        String allPassedString = numberOfAllSuccessfulTests.getText();
        int actualAllPassed = Integer.parseInt(allPassedString.substring(0, allPassedString.indexOf(" ")));
        AssertCollector.assertEquals(actualAllPassed, testNumberHelper.getAllSuccessful(), "The number of overall passed tests is NOT correct.");
        if (testNumberHelper.getAllSuccessful() > 0) {
            //check passed
            String passedString = numberPassedTests.getText();
            int actualPassed = Integer.parseInt(passedString.substring(1, passedString.indexOf("P") - 1));
            AssertCollector.assertEquals(actualPassed, testNumberHelper.getPassed(), "The number of passed number tests is NOT correct.");

            //check minor
            if (testNumberHelper.getPassedMinor() > 0) {
                String passedMinorString = numberPassedMinorTests.getText();
                int actualPassedMinor = Integer.parseInt(passedMinorString.substring(1, passedMinorString.indexOf("M") - 1));
                AssertCollector.assertEquals(actualPassedMinor, testNumberHelper.getPassedMinor(), "The number of passed with minor tests is NOT correct.");
            }

            //check retry
            if (testNumberHelper.getPassedRetry() > 0) {
                String passedRetryString = numberPassedRetryTests.getText();
                int actualPassedRetry = Integer.parseInt(passedRetryString.substring(1, passedRetryString.indexOf("P") - 1));
                AssertCollector.assertEquals(actualPassedRetry, testNumberHelper.getPassedRetry(), "The number of passed with retry tests is NOT correct");
            }
        }

        /* SKIPPED */
        //check all skipped
        String allSkippedString = numberAllSkippedTests.getText();
        int actualAllSkipped = Integer.parseInt(allSkippedString.substring(0, allSkippedString.indexOf(" ")));
        AssertCollector.assertEquals(actualAllSkipped, testNumberHelper.getAllSkipped(), "The number of overall skipped tests is correct.");
        if (testNumberHelper.getAllSkipped() > 0) {
            String skippedString = numberSkippedTests.getText();
            int actualSkipped = Integer.parseInt(skippedString.substring(1, skippedString.indexOf("S") - 1));
            AssertCollector.assertEquals(actualSkipped, testNumberHelper.getSkipped(), "The number of skipped tests is correct.");
        }

        /* BROKEN */
        //all broken
        String allFailedString = numberOfAllBrokenTests.getText();
        int actualAllFailed = Integer.parseInt(allFailedString.substring(0, allFailedString.indexOf(" ")));
        AssertCollector.assertEquals(actualAllFailed, testNumberHelper.getAllBroken(), "The number of overall failed tests is NOT correct.");
        if (testNumberHelper.getAllBroken() > 0) {
            //failed
            String failedString = numberFailedTests.getText();
            int actualFailed = Integer.parseInt(failedString.substring(1, failedString.indexOf("F") - 1));
            AssertCollector.assertEquals(actualFailed, testNumberHelper.getFailed(), "The number of failed tests is NOT correct.");

            //check minor
            if (testNumberHelper.getFailedMinor() > 0) {
                String failedMinorString = numberFailedMinorTests.getText();
                int actualFailedMinor = Integer.parseInt(failedMinorString.substring(1, failedMinorString.indexOf("F") - 1));
                AssertCollector.assertEquals(actualFailedMinor, testNumberHelper.getFailedMinor(), "The number of failed with minor tests is NOT correct.");
            }

            //check retried
            if (testNumberHelper.getFailedRetried() > 0) {
                String failedRetried = numberFailedRetriedTests.getText();
                int actualFailedRetried = Integer.parseInt(failedRetried.substring(1, failedRetried.indexOf("R") - 1));
                AssertCollector.assertEquals(actualFailedRetried, testNumberHelper.getFailedRetried(), "The number of retried tests is NOT correct.");
            }

            //check expected to fail
            if (testNumberHelper.getFailedExpected() > 0) {
                String failedExpected = numberFailedExpectedTests.getText();
                int actualFailedExpected = Integer.parseInt(failedExpected.substring(1, failedExpected.indexOf("E") - 1));
                AssertCollector.assertEquals(actualFailedExpected, testNumberHelper.getFailedExpected(), "The number of failed expected tests is NOT correct.");
            }
        }
    }

    /**
     * A small helper method to make the code more robust.
     * "10 Failures Aspects": separate the int value
     *
     * @return
     */
    private int eliminateNumbersOfFaiuresFromLink(String linkText) {
        //TODO pele we need an id for the int value
        String[] linkTextParts = linkText.split(" ");
        return Integer.parseInt(linkTextParts[0]);
    }
}
