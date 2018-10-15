package eu.tsystems.mms.tic.testframework.report.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fakr on 16.08.2017
 */
public class TestReportTwoExitPoints implements IFailurePointEntryHelper {

    public static final ExitPointEntry EP1 = new ExitPointEntry(
            TestResultHelper.TestResultFailurePointEntryType.FAILED,
            1,
            1,
            //*[contains(text(),'Exit Point #1 (1 Tests)')]/../..//*[contains(text(),'ReportTestUnderTestCorridorHigh.java:15')]
            "ReportTestUnderTestCorridorHigh.java:15",
            true,
            Arrays.asList("ReportTestUnderTestCorridorHigh - test_testHighCorridorFailed1"),Arrays.asList("Exception")
    );

    public static final ExitPointEntry EP2 = new ExitPointEntry(
            TestResultHelper.TestResultFailurePointEntryType.FAILED,
            2,
            1,
            "ReportTestUnderTestCorridorHigh.java:20",
            true,
            Arrays.asList("ReportTestUnderTestCorridorHigh - test_testHighCorridorFailed2"),Arrays.asList("Exception")
    );

    public static final ExitPointEntry FailedIntoReport = new ExitPointEntry(
            TestResultHelper.TestResultFailurePointEntryType.FAILED,
            0,
            1,
            "",
            true,
            Arrays.asList("ReportTestUnderTestExpectedtoFail - test_FailedMinorAnnotatedWithFailInReport"),
            Arrays.asList("FennecTestFailureException: Failing of test expected. Description: This is an unknown bug.","Exception")
    );

    public static final ExitPointEntry FailedNotIntoReport = new ExitPointEntry(
            TestResultHelper.TestResultFailurePointEntryType.FAILED,
            0,
            1,
            "",
            true,
            Arrays.asList("ReportTestUnderTestExpectedtoFail - test_FailedMinorAnnotatedWithFail_Run6"),
            Arrays.asList("FennecTestFailureException: Failing of test expected. Description: This is a known bug.","Exception")
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
