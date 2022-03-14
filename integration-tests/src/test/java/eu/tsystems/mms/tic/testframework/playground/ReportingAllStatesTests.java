/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.InfoMethod;
import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssert;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

@TestClassContext(name = "MyClass")
public class ReportingAllStatesTests extends AbstractWebDriverTest {

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
                OptionalAssert.assertEquals(2, 1, "minor fail");
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
        TimerUtils.sleep(1000);
    }

    @DataProvider(name = "dp", parallel = true)
    public Object[][] dp() {

        int size = 20;
        Object[][] objects = new Object[size][1];
        for (int i = 0; i < size; i++) {
            objects[i][0] = "" + (i + 1);
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
    @Fails
    public void testRetryAnalyzerWithFails() {
        throw new WebDriverException("Error communicating with the remote browser. It may have died.");
    }

    @Test
    @Fails(validFor = "test.foobar.fails.annotation.test.property.one=one")
    public void testRetryAnalyzerWithFailsValid() {
        throw new WebDriverException("Error communicating with the remote browser. It may have died.");
    }

    @Test
    @Fails(validFor = "test.foobar.fails.annotation.test.property.one=foo")
    public void testRetryAnalyzerWithFailsInvalid() {
        throw new WebDriverException("Error communicating with the remote browser. It may have died.");
    }

    @Test
    public void testNewRun() throws Exception {

    }

    @Test
    public void testRerunTest() {
        TimerUtils.sleep(RandomUtils.generateRandomInt(20));

        failingStep(How.RETRY);
    }

    @Test
    public void testRerunReverseTest() {

        TimerUtils.sleep(RandomUtils.generateRandomInt(20));

        failingStep(How.RETRY);
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
    public void testAssertCollector() {
        AssertCollector.fail("failed1");
        AssertCollector.fail("failed2");
        AssertCollector.fail("failed3");
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
    }

}
