package eu.tsystems.mms.tic.testframework.report.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fakr on 19.09.2017
 */
public class TestReportOneStateChanges {

    public static final int NUMBER_OF_METHODS = 96;

    public static final MethodStateChangesEntry SC1 = new MethodStateChangesEntry(
            TestResultHelper.TestResultChangedMethodState.NO_RUN,
            TestResultHelper.TestResultChangedMethodState.PASSED,
            "test_TestStatePassed1",
            "ReportTestUnderTestPassed"
    );

    public static final MethodStateChangesEntry SC2 = new MethodStateChangesEntry(
            TestResultHelper.TestResultChangedMethodState.NO_RUN,
            TestResultHelper.TestResultChangedMethodState.FAILED,
            "test_FailedMinor1",
            "My_Context"
    );

    public static final MethodStateChangesEntry SC3 = new MethodStateChangesEntry(
            TestResultHelper.TestResultChangedMethodState.NO_RUN,
            TestResultHelper.TestResultChangedMethodState.SKIPPED,
            "test_TestStateSkipped1",
            "ReportTestUnderTestSkipped"
    );

    public static List<MethodStateChangesEntry> getAllMethodStateChangesEntries() {
        return Arrays.asList(SC1, SC2, SC3);
    }

}
