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
        //TODO sagu rework
        PASSED("green", "PASSED", "header passed", "Passed", "test passed"),
        PASSEDMINOR("SkyBlue", "PASSEDMINOR", "header nf", "Minor", "test nf"),
        SKIPPED("orange","SKIPPED", "header skipped", "Skipped", "test skipped"),
        FAILED("red", "FAILED", "header broken", "Failed", "test broken"),
        FAILEDMINOR("DeepPink","FAILEDMINOR", "header brokennf", "Failed with Minor", "test brokennf"),
        RETRIED("pink","RETRIED", "header retried", "Retried", "test retried"),
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
        ALL(""),
        PASSED(".//*[@title='Passed']"),
        PASSEDMINOR(".//*[@title='Minor']"),
        SKIPPED(".//*[@title='Skipped']"),
        FAILED("(.//*[@title='Failed'])[2]"),
        FAILEDMINOR(".//*[@title='Failed with Minor']"),
        RETRIED(".//*[@title='Retried']"),
        FAILEDEXPECTED(".//*[@title='Expected Failed']");

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

    /**
     * Delievers a Dataprovider for all possible test results.
     *
     * @return
     */
    @DataProvider(parallel = true)
    public Object[][] getAllTestResults() {
        return excludeTestResults();
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
            case PASSEDMINOR:
                return new Color(135, 206, 235);
            case SKIPPED:
                return new Color(255, 165, 0);
            case FAILED:
                return new Color(255, 0, 0);
            case FAILEDMINOR:
                return new Color(255, 20, 147);
            case RETRIED:
                return new Color(255,182,193);
            case FAILEDEXPECTED:
                return new Color(0,0,0);
            default:
                throw new FennecRuntimeException("Color not implemented: " + testResult.getColor() + " for TestResult: " + testResult.toString());
        }
    }
}
