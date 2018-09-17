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
 * Created on 22.02.2013
 *
 * Copyright(c) 2013 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.logging;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class LoggingTests.
 *
 * @author pele
 */
public class FailsTest extends AbstractTest {

    @Test
    public void test1Failed() throws Exception {
        Assert.assertTrue(false, "Must fail");
    }

    @Test(dependsOnMethods = "test1Failed")
    public void test2Skipped() throws Exception {
        // Shall skip.
    }

    /**
     * FailedWithFailsAnnotation_IntParameter
     * <p/>
     * Description: Test for FailedWithFailsAnnotation_IntParameter
     */
    @Test // to system test
    @Fails(ticketId = 123)
    public void testFailedWithFailsAnnotation_IntParameter() {
        throw new FennecRuntimeException("This method MUST fail.");
    }

    /**
     * FailedWithFailsAnnotation_StringParameter
     * <p/>
     * Description: Test for testFailedWithFailsAnnotation_StringParameter
     */
    @Test // to system test
    @Fails(ticketString = "Ticket 123")
    public void testFailedWithFailsAnnotation_StringParameter() {
        throw new FennecRuntimeException("This method MUST fail.");
    }

    /**
     * testFailedWithFailsAnnotation_Description
     * <p/>
     * Description: Test for testFailedWithFailsAnnotation_Description
     */
    @Test // to system test
    @Fails(description = "must fail")
    public void testFailedWithFailsAnnotation_Description() {
        throw new FennecRuntimeException("This method MUST fail.");
    }

    @Test
    public void testFailedWithFailsAnnotation_Method() {
        DummyClass dummyClass = new DummyClass();
        dummyClass.doFail();
    }

    @Test
    public void testFailedWithFailsAnnotation_Method_withTimer() {
        DummyClass dummyClass = new DummyClass();
        dummyClass.doFailWithTimer();
    }

    @Test
    public void testFailedWithFailsAnnotation_MethodInherited() {
        DummyClass dummyClass = new DummyClass();
        dummyClass.doInheritedFail();
    }

    @Test
    public void testFailedWithFailsAnnotation_MethodOtherSignature() {
        DummyClass dummyClass = new DummyClass();
        dummyClass.doFail("foo");
    }

    @Test
    public void testFailingWithExpectedFailed() {
        DummyClass dummyClass = new DummyClass();
        dummyClass.doFail();
    }

}

class DummyClass {

    public void doFail(boolean reverse) {
        Assert.assertTrue(!reverse);
    }

    @Fails(description = "must fail because argument was null")
    public void doFail(String foo) { Assert.assertEquals(foo, "bar"); }

    @Fails(description = "must fail due to failing method in class")
    public void doFail() {
        Assert.assertTrue(false);
    }

    public void doInheritedFail() {
        this.doFail();
    }

    @Fails(description = "this is not the correct one")
    public void doFailWithTimer(int dummy) {
        // nothing here, its a dummy method
    }

    @Fails(description = "must fail")
    public void doFailWithTimer() {
        Timer timer = new Timer(100, 100);
        timer.setErrorMessage("timer fails");
        timer.executeSequence(new Timer.Sequence<Object>() {
            @Override
            public void run() throws Throwable {
                setPassState(false);
            }
        });
    }

    @Fails(description = "this is also not the correct one")
    public void doFailWithTimer(String dummy) {
        // nothing here, its a dummy method
    }

}
