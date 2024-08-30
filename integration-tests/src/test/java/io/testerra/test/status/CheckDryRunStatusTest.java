/*
 * Testerra
 *
 * (C) 2024, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package io.testerra.test.status;

import eu.tsystems.mms.tic.testframework.core.utils.Log4jFileReader;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created on 2024-08-30
 *
 * @author mgn
 */
public class CheckDryRunStatusTest extends TesterraTest {

    // log file reader
    private final Log4jFileReader LOG_4_J_FILE_READER = new Log4jFileReader("logs/pre-test-dry-run-log4j.log");

    // terms for searching log file
    private final String METHOD_END_WORKER_SEARCH_TERM = "finish.MethodEndWorker";

    /**
     * Test execution with active tt.dryrun flag are results in passed test methods and past configuration methods
     *
     * Except:
     * For data provider methods there is no TestNG hook. If a data provider method fails, it also fails in a dry run. The corresponding tests are skipped, not passed.
     *
     */

    @DataProvider
    public static Object[][] provideExpectedStatusPerMethod() {
        return new Object[][]{
                {"testT07_NonExistingDataProvider", Status.FAILED},
                {"beforeClassSetup01", Status.PASSED},
                {"beforeClassSetup02", Status.PASSED},
                {"beforeClassSetup03", Status.PASSED},
                {"beforeMethodSetup01", Status.PASSED},
                {"beforeMethodSetup02", Status.PASSED},
                {"afterMethodSetup01", Status.PASSED},
                {"afterMethodSetup02", Status.PASSED},
                {"afterClassSetup01", Status.PASSED},
                {"afterClassSetup02", Status.PASSED},
                {"beforeClassBeforeMethodSetup03", Status.PASSED},
                {"dataProviderThrowingAssertion", Status.FAILED},
                {"dataProviderThrowingException", Status.FAILED},
                {"dataProviderInClassThrowingException", Status.FAILED},
        };
    }

    @DataProvider
    public static Object[][] provideTestMethodsSkipped() {
        return new Object[][]{
                {"testT01_interceptCrashedDataProvider", Status.SKIPPED, "java.lang.AssertionError"},
                {"testT02_crashedDataProvider", Status.SKIPPED, "java.lang.AssertionError"},
                {"testT03_AssertFailedDataProvider", Status.SKIPPED, "java.lang.AssertionError"},
        };
    }

    @DataProvider
    public static Object[][] provideFinalTestResult() {
        return new Object[][]{
                {"*** Stats: SuiteContexts:  3", "SuiteContext"},
                {"*** Stats: TestContexts:   3", "TestContext"},
                {"*** Stats: ClassContexts:  16", "ClassContext"},
                {"*** Stats: MethodContexts: 73", "MethodContexts"},
                {"*** Stats: Test Methods Count: 49 (49 relevant)", "Test methods"},
                {"*** Stats: Failed: 1", "Failed tests"},
                {"*** Stats: Skipped: 3", "Skipped tests"},
                {"*** Stats: Passed: 45 ⊃ Repaired: 11", "Passed tests"}
        };
    }

    @Test(dataProvider = "provideExpectedStatusPerMethod")
    public void testT01_verifySimpleTestStatus(final String methodName, final Status expectedTestStatus) {
        LOG_4_J_FILE_READER.assertTestStatusPerMethod(METHOD_END_WORKER_SEARCH_TERM, methodName, expectedTestStatus);
    }

    @Test(dataProvider = "provideTestMethodsSkipped")
    public void testT02_verifySkippedStatus(final String methodName, final Status expectedInitialStatus, final String skippedInformationInLog) {

        final String expectedInitialStatusTitle = expectedInitialStatus.title;

        // check basic test status in log
        final List<String> foundEntries = LOG_4_J_FILE_READER.filterLogForTestMethod(METHOD_END_WORKER_SEARCH_TERM, methodName);
        final String logLine = foundEntries.get(0);

        Assert.assertEquals(foundEntries.size(), 1, "correct amount of method end entries found.");
        Assert.assertTrue(logLine.contains(expectedInitialStatusTitle), String.format("'%s' has status '%s' in log line '%s'", methodName, expectedInitialStatusTitle, logLine));

        // check entry with information for skipped
        final String skippedMethodEntry = LOG_4_J_FILE_READER.filterLogForFollowingEntry(logLine);
        Assert.assertTrue(skippedMethodEntry.contains(skippedInformationInLog),
                String.format("status '%s' found in entry '%s'", skippedInformationInLog, skippedMethodEntry));
    }

    @Test(dataProvider = "provideFinalTestResult")
    public void testT02_verifyCompleteResult(final String resultString, final String testObject) {
        List<String> foundEntries = LOG_4_J_FILE_READER.filterLogForString(resultString);
        Assert.assertEquals(foundEntries.size(), 1, String.format("The count of %s should contains in log with the string '%s'.", testObject, resultString));
    }
}
