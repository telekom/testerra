package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import org.testng.annotations.DataProvider;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sagu on 04.05.2017.
 * // TODO rework of DataProvider necessary
 */
public class TestResultHelper {

    /**
     * Enum for all possible TestResult Values that are possible within the xeta report.
     * <p>
     * TODO: unify xPath-Header -> Jira-Ticket: XETA-572
     */
    public enum TestResult {
        PASSED("green", "PASSED", "header passed", "Passed", "test passed"),
        FAILED("red", "FAILED", "header broken", "Failed", "test broken"),
        PASSEDMINOR("SkyBlue", "PASSEDMINOR", "header nf", "Minor", "test nf"),
        FAILEDMINOR("DeepPink","FAILEDMINOR", "header brokennf", "Failed with Minor", "test brokennf"),
        SKIPPED("orange","SKIPPED", "header skipped", "Skipped", "test skipped"),
        FAILEDEXPECTED("grey","FAILEDEXPECTED", "header expectedFailed", "Expected Failed", "test expectedFailed");

        private final String color;
        private final String testState;
        private final String xpathDashboardHeader;
        private final String xpathClassesDetailsHeader;
        private final String xpathTest;

        TestResult(String color, String testState, String xpathDashboardHeader, String xpathClassesDetailsHeader, String xpathTest) {
            this.color = color;
            this.testState = testState;
            this.xpathDashboardHeader = xpathDashboardHeader;
            this.xpathClassesDetailsHeader = xpathClassesDetailsHeader;
            this.xpathTest = xpathTest;
        }

        public String getColor() {
            return color;
        }

        public String getTestState() {
            return testState;
        }

        public String getDashboardHeaderXPath() {
            return xpathDashboardHeader;
        }

        public String getXpathClassesDetailsHeader() {
            return xpathClassesDetailsHeader;
        }

        public String getTestXPath() {
            return xpathTest;
        }
    }

    public enum TestResultClassesColumn {

        PASSED(".//*[@title='Passed']"),
        FAILED("(.//*[@title='Failed'])[2]"),
        PASSEDMINOR(".//*[@title='Minor']"),
        FAILEDMINOR(".//*[@title='Failed with Minor']"),
        SKIPPED(".//*[@title='Skipped']"),
        FAILEDEXPECTED(".//*[@title='Expected Failed']"),
        ALL("");

        private final String xpathNumber;

        TestResultClassesColumn(String numberXPath) {
            this.xpathNumber = numberXPath;
        }

        public String getNumberXPath() {
            return xpathNumber;
        }
    }

    public enum TestResultFailurePointEntryType {

        FAILED(".//*[@class='textleft resultsTable broken']"),
        FAILEDEXPECTED_NOT_INTOREPORT(".//*[@class='textleft resultsTable broken expfailed']"),
        FAILEDEXPECTED_INTOREPORT(".//*[@class='textleft resultsTable broken']"),
        ALL(".//*[contains(@class, 'textleft resultsTable broken')]");

        private final String xpath;

        TestResultFailurePointEntryType(String xpath) {
            this.xpath = xpath;
        }

        public String getXPath() {
            return xpath;
        }

        public String getClassAttribute() {
            return xpath.substring(xpath.indexOf("'") + 1, xpath.lastIndexOf("'"));
        }

    }

    public enum TestResultChangedMethodState {
        NO_RUN,
        PASSED,
        FAILED,
        FAILED_EXPECTED,
        SKIPPED,
        INHERITED_PASSED,
        INHERITED_FAILED,
        INHERITED_SKIPPED,
        FAILED_RETRY;
    }

    /**
     * Delievers a Dataprovider for all possible test results.
     *
     * @return
     */
    @DataProvider(parallel = true)
    public Object[][] getAllTestResults() {
        return excludeTestResults();
    }

    /**
     * Delievers a Dataprovider for all possible test results except the results SKIPPED and SKIPPED_INHERITED.
     *
     * @return
     */
    @DataProvider(parallel = true)
    public Object[][] getTestResultsExceptSkipped() {
        return excludeTestResults(TestResult.SKIPPED);
    }


    @DataProvider(parallel = true)
    public Object[][] getTestResultsMinor() {
        return excludeTestResults(TestResult.FAILED, TestResult.PASSED, TestResult.SKIPPED);
    }

    @DataProvider(parallel = true)
    public Object[][] getTestResultsFailed() {
        //TODO
        return excludeTestResults(TestResult.PASSED,TestResult.PASSEDMINOR, TestResult.SKIPPED);
    }

    @DataProvider(parallel = true)
    public Object[][] getTestResultsPassedAndFailedWithMinor() {
        return excludeTestResults(TestResult.FAILED, TestResult.PASSEDMINOR, TestResult.SKIPPED);
    }

    private Object[][] excludeTestResults(TestResult... excludedTestResults) {
        //write all possible test results to a list
        List<TestResult> allTestResults = new LinkedList<TestResult>(Arrays.asList(TestResult.values()));
        for (TestResult testResult : excludedTestResults) {
            allTestResults.remove(testResult);
        }

        Object[] testResults = allTestResults.toArray();

        Object[][] result = new Object[testResults.length][1];
        for (int i = 0; i < testResults.length; i++) {
            result[i][0] = testResults[i];
        }
        return result;
    }

    public static Color convertColorForTestResult(TestResult testResult) {
        switch (testResult) {
            case PASSED:
                return new Color(0, 128, 0);
            case FAILED:
                return new Color(255, 0, 0);
            case PASSEDMINOR:
                return new Color(135, 206, 235);
            case FAILEDMINOR:
                return new Color(255, 20, 147);
            case SKIPPED:
                return new Color(255, 165, 0);
            case FAILEDEXPECTED:
                return new Color(0,0,0);
            //case RETRIED:
                //TODO
            default:
                throw new FennecRuntimeException("Color not implemented: " + testResult.getColor() + " for TestResult: " + testResult.toString());
        }
    }
}
