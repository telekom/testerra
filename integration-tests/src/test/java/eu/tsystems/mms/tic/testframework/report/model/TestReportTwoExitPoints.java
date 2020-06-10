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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestReportTwoExitPoints implements IFailurePointEntryHelper {

    public static final ExitPointEntry EP1 = new ExitPointEntry(
            TestResultHelper.TestResultFailurePointEntryType.FAILED,
            1,
            1,
            //*[contains(text(),'Exit Point #1 (1 Tests)')]/../..//*[contains(text(),'ReportTestUnderTestCorridorHigh.java:15')]
            "ReportTestUnderTestCorridorHigh.java#test_testHighCorridorFailed1",
            true,
            Arrays.asList("ReportTestUnderTestCorridorHigh - test_testHighCorridorFailed1"),Arrays.asList("Exception")
    );

    public static final ExitPointEntry EP2 = new ExitPointEntry(
            TestResultHelper.TestResultFailurePointEntryType.FAILED,
            2,
            1,
            "ReportTestUnderTestCorridorHigh.java#test_testHighCorridorFailed2",
            true,
            Arrays.asList("ReportTestUnderTestCorridorHigh - test_testHighCorridorFailed2"),Arrays.asList("Exception")
    );

    public static final ExitPointEntry FailedIntoReport = new ExitPointEntry(
            TestResultHelper.TestResultFailurePointEntryType.FAILED,
            0,
            1,
            "test_FailedMinorAnnotatedWithFailInReport",
            true,
            Arrays.asList("ReportTestUnderTestExpectedtoFail - test_FailedMinorAnnotatedWithFailInReport"),
            Arrays.asList("TesterraTestFailureException: Failing of test expected. Description: This is an unknown bug.","Exception")
    );

    public static final ExitPointEntry FailedNotIntoReport = new ExitPointEntry(
            TestResultHelper.TestResultFailurePointEntryType.FAILED,
            41,
            1,
            "test_FailedMinorAnnotatedWithFail_Run6",
            true,
            Arrays.asList("ReportTestUnderTestExpectedtoFail - test_FailedMinorAnnotatedWithFail_Run6"),
            Arrays.asList("TesterraTestFailureException: Failing of test expected. Description: This is a known bug.","Exception")
    );

    public static List<ExitPointEntry> getAllExitPointEntryTestObjects() {
        List testObjects = new ArrayList<ExitPointEntry>() {};
        for (int index = 0; index < new TestReportTwoNumbers().getExitPoints(); index++) {
            ExitPointEntry furtherExitPoint;
            if(index == 0)
                furtherExitPoint = EP1;
            else if(index == 1)
                furtherExitPoint = EP2;
            else {
                furtherExitPoint = new ExitPointEntry(
                        TestResultHelper.TestResultFailurePointEntryType.FAILED,
                        index,
                        1,
                        "",
                        true);
            }
            testObjects.add(furtherExitPoint);
        }
        return testObjects;
    }
}
