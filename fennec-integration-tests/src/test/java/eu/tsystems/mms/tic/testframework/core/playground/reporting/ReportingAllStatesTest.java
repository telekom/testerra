/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/*
 * Created on 07.03.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.annotations.*;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import eu.tsystems.mms.tic.testhelper.AbstractReportingTest;
import org.openqa.selenium.WebDriverException;
import org.testng.SkipException;
import org.testng.annotations.*;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
@FennecClassContext
//@Listeners(FennecListener.class)
public class ReportingAllStatesTest extends AbstractReportingTest {

    static {
        System.setProperty(FennecProperties.FAILED_TESTS_MAX_RETRIES, "2");
        System.setProperty("test.foobar.fails.annotation.test.property.one", "one");
        Flags.XETA_FAILURE_CORRIDOR_ACTIVE = true;
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
//        failingStep();
    }

    @Test
    public void testPassed() throws Exception {

    }

    @DataProvider(name = "dp")
    public Object[][] dp() {

        Object[][] objects = new Object[2][1];
        objects[0][0] = "1";
        objects[1][0] = "2";
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

        failingStep(How.HARD);
    }

    @Test(dependsOnMethods = "testFailed")
    public void testFailedDependsOnFailed() throws Exception {

        failingStep(How.HARD);
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

        failingStep(How.HARD);
    }

    @Test
    @Fails(ticketId = 1)
    public void testFailedExpectedCollectedAssertions() throws Exception {

        failingStep(How.SOFT);
        failingStep(How.SOFT);
    }

    @Test
    @Fails(description = "blubb", intoReport = true)
    public void testFailedExpectedButIntoReport() throws Exception {

        failingStep(How.HARD);
    }

    @Test(dependsOnMethods = "testPassed")
    public void testFailedDepending() throws Exception {

        failingStep(How.HARD);
    }

    //
    @NoStatusMethod
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
        failingStep(How.HARD);
    }

    @Test
    public void testFailedMinorSoft() throws Exception {

        failingStep(How.MINOR);
        failingStep(How.SOFT);
    }

    @Test
    public void testFailedSoftHard() throws Exception {

        failingStep(How.SOFT);
        failingStep(How.HARD);
    }

    @Test
    public void testA() throws Exception {

    }

    @Test(dependsOnMethods = "testA")
    public void testB() throws Exception {

        failingStep();
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

        throw new WebDriverException("Error communicating with the remote browser. It may have died.");
    }

    @Test
    public void testRerunReverseTest() {

        TestUtils.sleep(RandomUtils.generateRandomInt(20));

        throw new WebDriverException("Error communicating with the remote browser. It may have died.");
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
        failingStep();
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
}
