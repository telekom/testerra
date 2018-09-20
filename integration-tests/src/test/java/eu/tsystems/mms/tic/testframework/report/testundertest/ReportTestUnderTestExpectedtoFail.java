package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.report.general.TestsUnderTestGroup;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ExitPointCreaterTestClass1;
import org.testng.annotations.Test;

/**
 * Created by riwa on 21.03.2017.
 */

public class ReportTestUnderTestExpectedtoFail extends AbstractTest {

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
    public void test_FailedMinorAnnotatedWithFailInReport() throws Exception{
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }
}
