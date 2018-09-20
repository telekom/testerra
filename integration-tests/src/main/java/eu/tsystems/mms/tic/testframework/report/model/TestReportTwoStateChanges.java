package eu.tsystems.mms.tic.testframework.report.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fakr on 19.09.2017
 */
public class TestReportTwoStateChanges {

    public static final int NUMBER_OF_METHODS = 22;

    public static final MethodStateChangesEntry SC4 = new MethodStateChangesEntry(
            TestResultHelper.TestResultChangedMethodState.PASSED,
            TestResultHelper.TestResultChangedMethodState.INHERITED_PASSED,
            "test_PassedInheritedMinor1",
            "ReportTestUnderTestPassed"
    );

    public static final MethodStateChangesEntry SC5 = new MethodStateChangesEntry(
            TestResultHelper.TestResultChangedMethodState.FAILED,
            TestResultHelper.TestResultChangedMethodState.INHERITED_FAILED,
            "test_FailedInheritedMinor1",
            "My_Context"
    );

    public static final MethodStateChangesEntry SC6 = new MethodStateChangesEntry(
            TestResultHelper.TestResultChangedMethodState.SKIPPED,
            TestResultHelper.TestResultChangedMethodState.INHERITED_SKIPPED,
            "test_TestStateSkippedInherited1",
            "ReportTestUnderTestSkipped"
    );

    public static List<MethodStateChangesEntry> getAllMethodStateChangesEntries() {
        return Arrays.asList(SC4, SC5, SC6);
    }

}
