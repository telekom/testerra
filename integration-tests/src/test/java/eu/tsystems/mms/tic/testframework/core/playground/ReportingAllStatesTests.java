package eu.tsystems.mms.tic.testframework.core.playground;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.annotations.*;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.lang.reflect.Method;

@FennecClassContext("MyClass")
public class ReportingAllStatesTests extends AbstractTest {

    static {
        System.setProperty("test.foobar.fails.annotation.test.property.one", "one");
    }

    enum How {
        FAST,
        LATE,
        MINOR,
        RETRY
    }

    private void failingStep(How how) {
        switch (how) {
            case FAST:
                Assert.assertEquals(2, 1, "fast fail");
                break;
            case LATE:
                AssertCollector.assertEquals(2, 1, "late fail");
                break;
            case MINOR:
                NonFunctionalAssert.assertEquals(2, 1, "minor fail");
                break;
            case RETRY:
                throw new WebDriverException("Error communicating with the remote browser. It may have died.");
        }
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
//        failingStep();
    }

    @Test
    public void testPassed() throws Exception {
        TestUtils.sleep(1000);
    }

    @DataProvider(name = "dp", parallel = true)
    public Object[][] dp() {

        int size = 20;
        Object[][] objects = new Object[size][1];
        for (int i = 0; i < size; i++) {
            objects[i][0] = "" + (i+1);
        }
        return objects;
    }

    @Test(dataProvider = "dp")
    public void testPassedDP(String dp) throws Exception {

    }

    @Parameters({"param1", "param2"})
    @Test
    public void testWOParameters() {
    }

    @Parameters({"param1", "param2"})
    @Test
    public void testWOptionalParametersSet(@Optional("value1") String param1, @Optional("value2") String param2) {
    }

    @Test(dependsOnMethods = "testPassed")
    public void testPassedDepending() throws Exception {

    }

    @Test
    public void testFailed() throws Exception {

        failingStep(How.FAST);
    }

    static int repairRunPassed = 0;
    static int repairRunMinor = 0;

    @Test
    public void testRepairPassed() throws Exception {
        repairRunPassed++;
        if (repairRunPassed == 1) {
            failingStep(How.RETRY);
        }
    }

    @Test
    public void testRepairMinor() throws Exception {
        repairRunMinor++;
        if (repairRunMinor == 1) {
            failingStep(How.RETRY);
        }
        failingStep(How.MINOR);
    }

    @Test(dependsOnMethods = "testFailed")
    public void testFailedDependsOnFailed() throws Exception {

        failingStep(How.FAST);
    }

    @Test
    @Fails
    public void testFailedExpectedException() throws Exception {

        throw new RuntimeException();
    }

    @Test
    @Fails(validFor = "test=haha")
    public void testFailedExpectedExceptionINVALID_FOR() throws Exception {

        throw new RuntimeException();
    }

    @Test
    @Fails(validFor = "test=huhu")
    public void testFailedExpectedExceptionVALID_FOR() throws Exception {

        throw new RuntimeException();
    }

    @Test
    @Fails(validFor = "unknown.property=haha")
    public void testFailedExpectedExceptionVALID_FOR_unknownProperty() throws Exception {

        throw new RuntimeException();
    }

    @Test
    @Fails(ticketId = 1)
    public void testFailedExpectedAssertion() throws Exception {

        failingStep(How.FAST);
    }

    @Test
    @Fails(ticketId = 1)
    public void testFailedExpectedCollectedAssertions() throws Exception {

        failingStep(How.LATE);
        failingStep(How.LATE);
    }

    @Test
    @Fails(description = "blubb", intoReport = true)
    public void testFailedExpectedButIntoReport() throws Exception {

        failingStep(How.FAST);
    }

    @Test(dependsOnMethods = "testPassed")
    public void testFailedDepending() throws Exception {

        failingStep(How.FAST);
    }

    //
    @InfoMethod
    @Test
    public void testSkippedNoStatus() throws Exception {

        throw new SkipException("must skip");
    }

    @Test
    public void testSkipped() throws Exception {

        throw new SkipException("must skip");
    }

    @Test
    public void testMinor() throws Exception {

        failingStep(How.MINOR);
    }

    @Test
    public void testFailedMinorHard() throws Exception {

        failingStep(How.MINOR);
        failingStep(How.FAST);
    }

    @Test
    public void testFailedMinorSoft() throws Exception {

        failingStep(How.MINOR);
        failingStep(How.LATE);
    }

    @Test
    public void testFailedSoftHard() throws Exception {

        failingStep(How.LATE);
        failingStep(How.FAST);
    }

    @Test
    public void testA() throws Exception {

    }

    @Test(dependsOnMethods = "testA")
    public void testB() throws Exception {
        failingStep(How.FAST);
    }

    @Test(dependsOnMethods = "testB")
    public void testC() throws Exception {
    }

    @Test
    @Fails(ticketId = 2345)
    public void testPassedWithFailsAnnotation() throws Exception {

    }

    @Test
    @Fails(ticketId = 2345, validFor = "test.foobar.fails.annotation.test.property.one=one")
    public void testPassedWithFailsAnnotationValid() throws Exception {

    }

    @Test
    @Fails(ticketId = 2345, validFor = "test.foobar.fails.annotation.test.property.one=two")
    public void testPassedWithFailsAnnotationNotValid() throws Exception {

    }


    @Test
    public void testNewRun() throws Exception {

    }

    @Test
    public void testRerunTest() {
        TestUtils.sleep(RandomUtils.generateRandomInt(20));

        failingStep(How.RETRY);
    }

    @Test
    public void testRerunReverseTest() {

        TestUtils.sleep(RandomUtils.generateRandomInt(20));

        failingStep(How.RETRY);
    }

    @New
    @Test
    public void testNew() {}

    @ReadyForApproval
    @Test
    public void testRFA() {}

    @New
    @ReadyForApproval
    @Test
    public void testNewRFA() {}

    @New
    @ReadyForApproval
    @Test
    public void testNewRFAFailing() {
        failingStep(How.FAST);
    }

    @Test
    public void testFailingComplexThrowable() {
        throw new RuntimeException("1", new RuntimeException("2", new RuntimeException("3")));
    }

    @Test
    public void testSequenceLogs() {
        Timer timer = new Timer(100, 500);
        ThrowablePackedResponse<Object> response = timer.executeSequence(new Timer.Sequence<Object>() {
            @Override
            public void run() throws Throwable {
                setSkipThrowingException(true);
                throw new RuntimeException();
            }
        });
        response.logThrowableAndReturnResponse();
    }

    @Test
    public void testSequenceLogsWOException() {
        Timer timer = new Timer(100, 500);
        ThrowablePackedResponse<Object> response = timer.executeSequence(new Timer.Sequence<Object>() {
            @Override
            public void run() throws Throwable {
                setSkipThrowingException(true);
                setPassState(false);
            }
        });
        response.logThrowableAndReturnResponse();
    }

    @Test
    @SupportMethod
    @New
    @ReadyForApproval
    @InDevelopment
    public void testAllMarkers() throws Exception {
    }

    @InDevelopment
    @Test
    public void testInDevelopment() {

    }

    @Test
    public void testAssertCollector() {
        AssertCollector.fail("failed1");
        AssertCollector.fail("failed2");
        AssertCollector.fail("failed3");
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
    }

    @InfoMethod
    @Test
    public void testInfo() {

    }
}
