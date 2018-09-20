package eu.tsystems.mms.tic.testframework.report.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fakr on 19.09.2017
 */
public class TestReportOneAcknowledgements {

    public static final int NUMBER_OF_METHODS = 4;

    public static final MethodStateAcknowledgementEntry ACK1 = new MethodStateAcknowledgementEntry(
            TestResultHelper.TestResultChangedMethodState.NO_RUN,
            TestResultHelper.TestResultChangedMethodState.FAILED,
            "testNewMarkerFailure",
            "ReportTestUnderTestAnnotations",
            Arrays.asList(ReportAnnotationType.NEW)
    );

    public static final MethodStateAcknowledgementEntry ACK2 = new MethodStateAcknowledgementEntry(
            TestResultHelper.TestResultChangedMethodState.NO_RUN,
            TestResultHelper.TestResultChangedMethodState.PASSED,
            "testAllMarkers",
            "ReportTestUnderTestAnnotations",
            Arrays.asList(ReportAnnotationType.NEW, ReportAnnotationType.READY_FOR_APPROVAL, ReportAnnotationType.SUPPORT_METHOD)
    );

    public static final MethodStateAcknowledgementEntry ACK3 = new MethodStateAcknowledgementEntry(
            TestResultHelper.TestResultChangedMethodState.NO_RUN,
            TestResultHelper.TestResultChangedMethodState.PASSED,
            "testReadyForApprovalMarker",
            "ReportTestUnderTestAnnotations",
            Arrays.asList(ReportAnnotationType.READY_FOR_APPROVAL)
    );

    public static final MethodStateAcknowledgementEntry ACK4 = new MethodStateAcknowledgementEntry(
            TestResultHelper.TestResultChangedMethodState.NO_RUN,
            TestResultHelper.TestResultChangedMethodState.PASSED,
            "testNewMarkerSuccess",
            "ReportTestUnderTestAnnotations",
            Arrays.asList(ReportAnnotationType.NEW)
    );

    public static List<MethodStateAcknowledgementEntry> getAllMethodStateAcknowledgementEntries() {
        return Arrays.asList(ACK1, ACK2, ACK3, ACK4);
    }

}
