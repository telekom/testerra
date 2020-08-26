/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.testng.annotations.DataProvider;

public class TestResultHelper {

    /**
     * Enum for all possible TestResult Values that are possible within the Testerra report.
     * <p>
     */
    public enum TestResult {
        PASSED("green", "PASSED", "header passed", "Passed", "test passed"),
        PASSEDMINOR("SkyBlue", "PASSEDMINOR", "header nf", "Minor", "test nf"),
        PASSEDRETRY("#6abd00", "PASSED_RETRY", "todo xpath header", "Passed after Retry", "todo xpathTest"),
        SKIPPED("orange", "SKIPPED", "header skipped", "Skipped", "test skipped"),
        FAILED("red", "FAILED", "header broken", "Failed", "test broken"),
        FAILEDMINOR("DeepPink", "FAILEDMINOR", "header brokennf", "Failed + Minor", "test brokennf"),
        RETRIED("pink", "RETRIED", "header retried", "Retried", "test retried"),
        FAILEDEXPECTED("grey", "FAILEDEXPECTED", "header expectedFailed", "Expected Failed", "test expectedFailed");

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
        PASSEDRETRY(".//*[@title='Passed after Retry']"),
        SKIPPED(".//*[@title='Skipped']"),
        FAILED(".//td[@title='Failed']"),
        FAILEDMINOR(".//*[@title='Failed + Minor']"),
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
            case PASSEDRETRY:
                return new Color(106, 189, 0);
            case SKIPPED:
                return new Color(255, 165, 0);
            case FAILED:
                return new Color(255, 0, 0);
            case FAILEDMINOR:
                return new Color(255, 20, 147);
            case RETRIED:
                return new Color(255, 192, 203);
            case FAILEDEXPECTED:
                return new Color(128, 128, 128);
            default:
                throw new TesterraRuntimeException("Color not implemented: " + testResult.getColor() + " for TestResult: " + testResult.toString());
        }
    }
}
