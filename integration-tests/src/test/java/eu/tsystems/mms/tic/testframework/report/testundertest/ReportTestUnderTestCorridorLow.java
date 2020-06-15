package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.general.TestsUnderTestGroup;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ExitPointCreatorTestClass2;
import org.testng.annotations.Test;

@TestContext
public class ReportTestUnderTestCorridorLow extends AbstractTest {

    @FailureCorridor.Low
    @Test
    public void test_testLowCorridorFailed1() throws Exception {
        TestStep.begin("Test-Step-1");
        TestStep.begin("Test-Step-2");
        TestStep.begin("Test-Step-3");
        throw new Exception();
    }

    @FailureCorridor.Low
    @Test
    public void test_testLowCorridorFailed2() throws Exception {
        Assert.assertTrue(false);
    }

    @FailureCorridor.Low
    @Test
    public void test_testLowCorridorFailed3() throws Exception {
        ExitPointCreatorTestClass2.testCreatorForDifferentExitPoints2();
    }

    @FailureCorridor.Low
    @Test
    public void test_testLowCorridorFailed4() throws Exception {
        ExitPointCreatorTestClass2.testCreatorForDifferentExitPoints2();
    }

    @FailureCorridor.Low
    @Test
    public void test_testLowCorridorFailed5() throws Exception {
        ExitPointCreatorTestClass2.testCreatorForDifferentExitPoints2();
    }

    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_FailedMinor1() throws Exception {
        NonFunctionalAssert.assertTrue(false);
        throw new Exception();
    }

}
