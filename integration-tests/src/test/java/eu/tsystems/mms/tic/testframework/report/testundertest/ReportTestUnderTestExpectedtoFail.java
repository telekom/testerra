package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.report.general.TestsUnderTestGroup;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.ExitPointCreaterTestClass1;
import org.testng.annotations.Test;

import java.util.UUID;

public class ReportTestUnderTestExpectedtoFail extends AbstractTest {

    private String uniqueFailureAspectMessage = "matchting unique failure aspect: " + UUID.randomUUID().toString();

    @Fails(description = "This is a known bug.")
    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_FailedMinorAnnotatedWithFail_Run4() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Fails(description = "This is a known bug.")
    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_FailedMinorAnnotatedWithFail_Run5() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Fails(description = "This is a known bug.")
    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_FailedMinorAnnotatedWithFail_Run6() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Fails(description = "This is an unknown bug.", intoReport = true)
    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_FailedMinorAnnotatedWithFailInReport() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_UnexpectedFailedWithRelatedExpectedFailed() throws Exception {
        throw new Exception(uniqueFailureAspectMessage);
    }

    @Fails(description = "Known issue with same aspect as unmarked failed test")
    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_ExpectedFailedWithRelatedUnexpectedFailed() throws Exception {
        throw new Exception(uniqueFailureAspectMessage);
    }
}
