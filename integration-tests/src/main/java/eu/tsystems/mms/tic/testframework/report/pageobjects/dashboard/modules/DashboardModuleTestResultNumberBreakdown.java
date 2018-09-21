package eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules;

import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFramePage;
import eu.tsystems.mms.tic.testframework.report.model.MouseAction;
import eu.tsystems.mms.tic.testframework.report.model.TestNumberHelper;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardModuleTestResultNumberBreakdown extends AbstractFramePage {

    // Test Numbers
    @Check
    public final GuiElement numberAllTestsLink = new GuiElement(this.driver, By.id("totalNumberOfTestMethods"), mainFrame);
    public final GuiElement numberAllPassedTestsLink = new GuiElement(this.driver, By.id("totalNumberOfSuccessfulMethods"), mainFrame);
    public final GuiElement numberAllFailedTestsLink = new GuiElement(this.driver, By.id("totalNumberOfFailedMethods"), mainFrame);
    public final GuiElement numberFailedExpectedTestsString = new GuiElement(this.driver, By.id("numberOfFAILED_EXPECTED"), mainFrame);
    public final GuiElement numberAllSkippedTestsLink = new GuiElement(this.driver, By.id("totalNumberOfSkippedMethods"), mainFrame);
    public final GuiElement numberPassedTestsLink = new GuiElement(this.driver, By.id("numberOfPASSED"), mainFrame);
    public final GuiElement numberPassedInheritedTestsLink = new GuiElement(this.driver, By.id("numberOfPreviouslyPassedMethods"), mainFrame);
    public final GuiElement numberPassedMinorTestsLink = new GuiElement(this.driver, By.id("numberOfMINOR"), mainFrame);
    public final GuiElement numberFailedTestsLink = new GuiElement(this.driver, By.id("numberOfFAILED"), mainFrame);
    public final GuiElement numberFailedInheritedTestsLink = new GuiElement(this.driver, By.id("numberOfPreviouslyFailedMethods"), mainFrame);
    public final GuiElement numberFailedMinorTestsLink = new GuiElement(this.driver, By.id("numberOfFAILED_MINOR"), mainFrame);
    public final GuiElement numberSkippedTestsLink = new GuiElement(this.driver, By.id("numberOfSKIPPED"), mainFrame);
    public final GuiElement numberSkippedInheritedTestsLink = new GuiElement(this.driver, By.id("numberOfPreviouslySkippedMethods"), mainFrame);
    public final GuiElement numberExitPointsLinks = new GuiElement(this.driver, By.xpath("//*[@id='exitPointsLink']/a"), mainFrame);

    public final GuiElement deltaAllTestsString = new GuiElement(this.driver, By.id("totalNumberOfTestMethodsDelta"), mainFrame);
    public final GuiElement deltaPassedTestsString = new GuiElement(this.driver, By.id("totalNumberOfSuccesfullMethodsDelta"), mainFrame);
    public final GuiElement deltaFailedTestsString = new GuiElement(this.driver, By.id("totalNumberOfFailedMethodsDelta"), mainFrame);
    public final GuiElement deltaSkippedTestsString = new GuiElement(this.driver, By.id("totalNumberOfSkippedMethodsDelta"), mainFrame);

    public final GuiElement numberFailureAspectsLink = new GuiElement(this.driver, By.xpath("//*[@id='failureAspectsLink']/a"), mainFrame);

    // Test time-data
    @Check
    public final GuiElement testDurationString = new GuiElement(this.driver, By.id("actualRunDuration"), mainFrame);
    public final GuiElement testDurationDeltaString = new GuiElement(this.driver, By.id("actualRunDurationDelta"), mainFrame);
    @Check
    public final GuiElement testStartTimeString = new GuiElement(this.driver, By.id("actualRunStartTime"), mainFrame);
    @Check
    public final GuiElement testEndTimeString = new GuiElement(this.driver, By.id("actualRunEndTime"), mainFrame);

    // Percentages
    @Check
    public final GuiElement testPercentageString = new GuiElement(this.driver, By.id("actualRunPassRate"), mainFrame);
    public final GuiElement testPercentageDeltaString = new GuiElement(this.driver, By.id("actualRunPassRateDelta"), mainFrame);
    public final GuiElement lastRunTestPercentageString = new GuiElement(this.driver, By.id("lastRunPassRate"), mainFrame);

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
                isDisplayed = numberPassedTestsLink.isDisplayed();
                break;
            case PASSEDINHERITED:
                isDisplayed = numberPassedInheritedTestsLink.isDisplayed();
                break;
            case PASSEDMINOR:
                isDisplayed = numberPassedMinorTestsLink.isDisplayed();
                break;
            case FAILED:
                isDisplayed = numberFailedTestsLink.isDisplayed();
                break;
            case FAILEDINHERITED:
                isDisplayed = numberFailedInheritedTestsLink.isDisplayed();
                break;
            case FAILEDMINOR:
                isDisplayed = numberFailedMinorTestsLink.isDisplayed();
                break;
            case SKIPPED:
                isDisplayed = numberSkippedTestsLink.isDisplayed();
                break;
            case SKIPPEDINHERITED:
                isDisplayed = numberSkippedInheritedTestsLink.isDisplayed();
                break;
            case FAILEDEXPECTED:
                isDisplayed = numberFailedExpectedTestsString.isDisplayed();
                break;
            default:
                throw new FennecRuntimeException("Unsupported Test Counter for TestResult: " + testResult);
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
     * @param testResult  the result category (Passed, Failed, Failed Inherited, ...)
     * @param mouseAction the intended mouseAction
     * @return an updated DashboardPage Object
     * @throws
     */
    public DashboardPage triggerNumber(TestResultHelper.TestResult testResult, MouseAction mouseAction) throws Exception {
        GuiElement counter;
        switch (testResult) {
            case PASSED:
                counter = numberPassedTestsLink;
                break;
            case PASSEDINHERITED:
                counter = numberPassedInheritedTestsLink;
                break;
            case PASSEDMINOR:
                counter = numberPassedMinorTestsLink;
                break;
            case FAILED:
                counter = numberFailedTestsLink;
                break;
            case FAILEDINHERITED:
                counter = numberFailedInheritedTestsLink;
                break;
            case FAILEDMINOR:
                counter = numberFailedMinorTestsLink;
                break;
            case SKIPPED:
                counter = numberSkippedTestsLink;
                break;
            case SKIPPEDINHERITED:
                counter = numberSkippedInheritedTestsLink;
                break;
            case FAILEDEXPECTED:
                counter = numberFailedExpectedTestsString;
                break;
            default:
                throw new FennecRuntimeException("Unsupported Test Counter for TestResult: " + testResult);        }
        switch (mouseAction) {
            case CLICK:
                counter.click();
                break;
            case MOUSE_OVER:
                counter.mouseOver();
                break;
            default:
                throw new FennecRuntimeException("Unsupported mouseAction: " + mouseAction.name());
        }
        counter.click();
        return PageFactory.create(DashboardPage.class, this.driver);
    }

    public void assertTestNumbers(boolean isDelta, boolean isInherited, boolean isSkipped, boolean isExpectedToFail, TestNumberHelper testNumberHelper) {

        assertCoreTestNumbers(testNumberHelper, true, true, isSkipped, true);

        //check number of failure aspects and exit points
        AssertCollector.assertEquals(eliminateNumbersOfFaiuresFromLink(numberFailureAspectsLink.getText()), testNumberHelper.getFailureAspects(), "The number failure aspects is NOT correct.");
        AssertCollector.assertEquals(eliminateNumbersOfFaiuresFromLink(numberExitPointsLinks.getText()), testNumberHelper.getExitPoints(), "The number of exit points is NOT correct.");

        //check delta numbers
        if (isDelta) {
            AssertCollector.assertEquals(Integer.parseInt(deltaAllTestsString.getText().replaceAll("[^0-9+/-]", "")), testNumberHelper.getOverallDelta(), "The number of overall delta tests is correct.");
            AssertCollector.assertEquals(Integer.parseInt(deltaPassedTestsString.getText().replaceAll("[^0-9+/-]", "")), testNumberHelper.getPassedDelta(), "The delta number of passed tests is correct.");
            AssertCollector.assertEquals(Integer.parseInt(deltaFailedTestsString.getText().replaceAll("[^0-9+/-]", "")), testNumberHelper.getFailedDelta(), "The delta number of failed tests is correct.");
            AssertCollector.assertEquals(Integer.parseInt(deltaSkippedTestsString.getText().replaceAll("[^0-9+/-]", "")), testNumberHelper.getSkippedDelta(), "The delta number of skipped tests is correct.");
        }

        if (isInherited) {
            String passedInherString = numberPassedInheritedTestsLink.getText();
            String failedInherString = numberFailedInheritedTestsLink.getText();
            String skippedInherString = numberSkippedInheritedTestsLink.getText();

            int actualPassedInher = Integer.parseInt(passedInherString.substring(1, passedInherString.indexOf("i") - 1));
            int actualFailedInher = Integer.parseInt(failedInherString.substring(1, failedInherString.indexOf("i") - 1));
            int actualSkippedInher = Integer.parseInt(skippedInherString.substring(1, skippedInherString.indexOf("i") - 1));

            AssertCollector.assertEquals(actualPassedInher, testNumberHelper.getPassedInherited(), "The number of inherited passed tests is correct.");
            AssertCollector.assertEquals(actualFailedInher, testNumberHelper.getFailedInherited(), "The number of inherited failed tests is correct.");
            AssertCollector.assertEquals(actualSkippedInher, testNumberHelper.getSkippedInherited(), "The number of inherited skipped tests is correct.");
        }


        //check number of expected to fail tests
        if (isExpectedToFail) {
            GuiElement actualExpectedFailed = numberFailedExpectedTestsString;
            AssertCollector.assertEquals(Integer.parseInt(actualExpectedFailed.getText().replaceAll("\\D", "")), testNumberHelper.getExpectedFailed(), "The number of expected failed tests is correct.");
        }
    }


    public void assertCoreTestNumbers(TestNumberHelper testNumberHelper, boolean isPassed, boolean isFailed, boolean isSkipped, boolean isMinor) {

        /* ALL */
        int actualAll = Integer.parseInt(numberAllTestsLink.getText());
        AssertCollector.assertEquals(actualAll, testNumberHelper.getAll(), "The number of overall tests is NOT correct.");

        /* PASSED */
        if (isPassed) {
            String allPassedString = numberAllPassedTestsLink.getText();
            int actualAllPassed = Integer.parseInt(allPassedString.substring(0, allPassedString.indexOf(" ")));
            AssertCollector.assertEquals(actualAllPassed, testNumberHelper.getAllPassed(), "The number of overall passed tests is NOT correct.");
            String passedString = numberPassedTestsLink.getText();
            int actualPassed = Integer.parseInt(passedString.substring(1, passedString.indexOf("P") - 1));
            AssertCollector.assertEquals(actualPassed, testNumberHelper.getPassed(), "The number of passed number tests is NOT correct.");
        }

        /* FAILED */
        if (isFailed) {
            String allFailedString = numberAllFailedTestsLink.getText();
            int actualAllFailed = Integer.parseInt(allFailedString.substring(0, allFailedString.indexOf(" ")));
            AssertCollector.assertEquals(actualAllFailed, testNumberHelper.getAllFailed(), "The number of overall failed tests is NOT correct.");
            String failedString = numberFailedTestsLink.getText();
            int actualFailed = Integer.parseInt(failedString.substring(1, failedString.indexOf("F") - 1));
            AssertCollector.assertEquals(actualFailed, testNumberHelper.getFailed(), "The number of failed tests is NOT correct.");
        }

        /* SKIPPED */
        if (isSkipped) {
            String allSkippedString = numberAllSkippedTestsLink.getText();
            int actualAllSkipped = Integer.parseInt(allSkippedString.substring(0, allSkippedString.indexOf(" ")));
            String skippedString = numberSkippedTestsLink.getText();
            int actualSkipped = Integer.parseInt(skippedString.substring(1, skippedString.indexOf("S") - 1));
            AssertCollector.assertEquals(actualAllSkipped, testNumberHelper.getAllSkipped(), "The number of overall skipped tests is correct.");
            AssertCollector.assertEquals(actualSkipped, testNumberHelper.getSkipped(), "The number of skipped tests is correct.");
        }

        /* MINORS */
        if (isMinor) {
            /* PASSED MINOR */
            String passedMinorString = numberPassedMinorTestsLink.getText();
            int actualPassedMinor = Integer.parseInt(passedMinorString.substring(1, passedMinorString.indexOf("M") - 1));
            AssertCollector.assertEquals(actualPassedMinor, testNumberHelper.getPassedMinor(), "The number of passed with minor tests is NOT correct.");
            /* FAILED MINOR */
            String failedMinorString = numberFailedMinorTestsLink.getText();
            int actualFailedMinor = Integer.parseInt(failedMinorString.substring(1, failedMinorString.indexOf("F") - 1));
            AssertCollector.assertEquals(actualFailedMinor, testNumberHelper.getFailedMinor(), "The number of failed with minor tests is NOT correct.");
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
