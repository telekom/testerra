package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.annotations.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by fakr on 30.08.2017
 */
public class ReportTestUnderTestAnnotations extends AbstractTest {

    @Test
    @SupportMethod
    @New
    @ReadyForApproval
    @InDevelopment
    @NoRetry
    public void testAllMarkers() throws Exception {
    }

    @Test
    @SupportMethod
    public void testSupportMethodMarker() {
    }

    @Test
    @InDevelopment
    public void testInDevelopmentMarker() {
    }

    @Test
    @NoRetry
    public void testNoRetryMarker() {
    }

    @Test
    @New
    public void testNewMarkerSuccess() {
    }

    @Test
    @ReadyForApproval
    public void testReadyForApprovalMarker() {
    }

    @Test
    @New
    public void testNewMarkerFailure() {
        Assert.assertTrue(false);
    }

    //@Test
    @InfoMethod
    // TODO how is this displayed in report?
    public void testNoStatusMethodMarker() {
    }

    //@Test
    @SkipMetrics
    // TODO how is this displayed in report?
    public void testSkipMetricsMarker() {
    }

    //@Test
    @DismissDryRun
    // TODO how is this displayed in report?
    public void testDismissDryRunMarker() {
    }

}
