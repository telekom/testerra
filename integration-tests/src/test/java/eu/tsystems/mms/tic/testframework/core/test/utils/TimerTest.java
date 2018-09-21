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
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by nigr on 04.09.2015.
 */
public class TimerTest extends AbstractTest {

    public static final int DURATION_IN_MS = 1000;
    public static final int SLEEP_TIME_IN_MS = 50;
    public static final int UPPER_BOUND_IN_MS = 200;

    private final String msgMinimumTime = "Timer need at least "+ DURATION_IN_MS + " ms to pass";
    private final String msgMaximumTime = "Sequence execution timed out within variance";

    private final String msgCorrectPass = "Timer passed sequence correctly";
    private final String msgCorrectResponse = "Response was returned correctly";
    private final String msgCorrectThrowable = "Throwable was returned correctly";

    private final String msgTimeout = "Sequence execution timed out "+ DURATION_IN_MS +" ms (polling every " + SLEEP_TIME_IN_MS + " ms)";
    private final String msgTimeoutExceptionThrown = "TimeoutException was thrown";

    private final String msgFennecRuntimeException = "FennecRuntimeException Message";
    private final String msgFennecRuntimeExceptionThrown = "FennecRuntimeException was thrown";

    @Test
    public void testT01_ExecuteSequence() throws Exception {
        Timer timer = new Timer(SLEEP_TIME_IN_MS, DURATION_IN_MS);

        final long timeMillisBegin = System.currentTimeMillis();

        ThrowablePackedResponse<String> out = timer.executeSequence(new Timer.Sequence<String>() {
            @Override
            public void run() {
            }
        });

        final long timeMillisDuration = System.currentTimeMillis() - timeMillisBegin;

        Assert.assertFalse(timer.isTimeOver(), msgCorrectPass);
        Assert.assertTrue(timeMillisDuration < UPPER_BOUND_IN_MS, "Timer passed sequence in less than 200ms");
    }

    @Test
    public void testT02_ExecuteSequence_PassStateTrue() throws Exception {
        Timer timer = new Timer(SLEEP_TIME_IN_MS, DURATION_IN_MS);

        final long timeMillisBegin = System.currentTimeMillis();

        ThrowablePackedResponse<String> out = timer.executeSequence(new Timer.Sequence<String>() {
            @Override
            public void run() {
                setPassState(true);
            }
        });

        final long timeMillisDuration = System.currentTimeMillis() - timeMillisBegin;

        Assert.assertFalse(timer.isTimeOver(), msgCorrectPass);
        Assert.assertTrue(timeMillisDuration < UPPER_BOUND_IN_MS, "Timer passed sequence in less than 200ms");
    }

    @Test
    public void testT03_ExecuteSequence_PassStateTrueAndReturningObject() throws Exception {
        Timer timer = new Timer(SLEEP_TIME_IN_MS, DURATION_IN_MS);

        final long timeMillisBegin = System.currentTimeMillis();

        ThrowablePackedResponse<String> out = timer.executeSequence(new Timer.Sequence<String>() {
            @Override
            public void run() {
                setReturningObject("huhu");
                setPassState(true);
            }
        });

        final long timeMillisDuration = System.currentTimeMillis() - timeMillisBegin;
        String response = out.getResponse();

        Assert.assertFalse(timer.isTimeOver(), msgCorrectPass);
        Assert.assertTrue(timeMillisDuration < UPPER_BOUND_IN_MS, "Timer passed sequence in less than 200ms");
        Assert.assertNotNull(response, msgCorrectResponse);
        Assert.assertEquals(response, "huhu", msgCorrectResponse);
    }

    @Test
    public void testT04_ExecuteSequence_WithException() throws Exception {
        Timer timer = new Timer(SLEEP_TIME_IN_MS, DURATION_IN_MS);

        final long timeMillisBegin = System.currentTimeMillis();

        Throwable throwable = new Throwable();
        ThrowablePackedResponse<String> out = new ThrowablePackedResponse<String>(null, throwable, false, new TimeoutException(throwable));
        TimeoutException timeoutException = null;
        FennecRuntimeException fennecRuntimeException;

        try {
            out = timer.executeSequence(new Timer.Sequence<String>() {
                @Override
                public void run() {
                    setReturningObject("huhu");
                    throw new FennecRuntimeException(msgFennecRuntimeException);
                }
            });
        } catch (TimeoutException e) {
            timeoutException = e;
        }

        final long timeMillisDuration = System.currentTimeMillis() - timeMillisBegin;
        String response = out.getResponse();

        Assert.assertTrue(timer.isTimeOver(), msgCorrectPass);
        Assert.assertTrue(timeMillisDuration >= DURATION_IN_MS, msgMinimumTime);
        Assert.assertTrue(timeMillisDuration <= DURATION_IN_MS + UPPER_BOUND_IN_MS, msgMaximumTime);
        Assert.assertNull(response, msgCorrectResponse);
        Assert.assertNotNull(timeoutException, msgTimeoutExceptionThrown);

        fennecRuntimeException = (FennecRuntimeException) timeoutException.getCause();

        Assert.assertNotNull(fennecRuntimeException, msgFennecRuntimeExceptionThrown);
        Assert.assertEquals(fennecRuntimeException.getMessage(), msgFennecRuntimeException, msgFennecRuntimeExceptionThrown);
    }

    @Test
    public void testT05_ExecuteSequence_PassStateFalse() throws Exception {
        Timer timer = new Timer(SLEEP_TIME_IN_MS, DURATION_IN_MS);

        final long timeMillisBegin = System.currentTimeMillis();

        Throwable throwable = new Throwable();
        ThrowablePackedResponse<String> out = new ThrowablePackedResponse<String>(null, throwable, false, new TimeoutException(throwable));
        TimeoutException timeoutException = null;

        try {
            out = timer.executeSequence(new Timer.Sequence<String>() {
                @Override
                public void run() {
                    setPassState(false);
                }
            });
        } catch (TimeoutException e) {
            timeoutException = e;
        }

        final long timeMillisDuration = System.currentTimeMillis() - timeMillisBegin;
        String response = out.getResponse();

        Assert.assertTrue(timer.isTimeOver(), msgCorrectPass);
        Assert.assertTrue(timeMillisDuration >= DURATION_IN_MS, msgMinimumTime);
        Assert.assertTrue(timeMillisDuration <= DURATION_IN_MS  + UPPER_BOUND_IN_MS, msgMaximumTime);
        Assert.assertNotNull(timeoutException, msgTimeoutExceptionThrown);
        Assert.assertEquals(timeoutException.getMessage(), msgTimeout);
        Assert.assertNull(response, msgCorrectResponse);
    }

    @Test
    public void testT06_ExecuteSequence_PassStateFalseAndReturningObject() throws Exception {
        Timer timer = new Timer(SLEEP_TIME_IN_MS, DURATION_IN_MS);

        final long timeMillisBegin = System.currentTimeMillis();
        boolean isTimeoutExceptionThrown = false;

        Throwable throwable = new Throwable();
        ThrowablePackedResponse<String> out = new ThrowablePackedResponse<String>(null, throwable, false, new TimeoutException(throwable));

        try {
            out = timer.executeSequence(new Timer.Sequence<String>() {
                @Override
                public void run() {
                    setReturningObject("huhu");
                    setPassState(false);
                }
            });
        } catch (TimeoutException e) {
            isTimeoutExceptionThrown = true;
        }

        final long timeMillisDuration = System.currentTimeMillis() - timeMillisBegin;
        String response = out.getResponse();

        Assert.assertTrue(timer.isTimeOver(), msgCorrectPass);
        Assert.assertTrue(timeMillisDuration >= DURATION_IN_MS, msgMinimumTime);
        Assert.assertTrue(timeMillisDuration <= DURATION_IN_MS + UPPER_BOUND_IN_MS, msgMaximumTime);
        Assert.assertFalse(isTimeoutExceptionThrown, msgTimeoutExceptionThrown);
        Assert.assertNotNull(response, msgCorrectResponse);
        Assert.assertEquals(response, "huhu", msgCorrectResponse);
    }

    @Test
    public void testT07_ExecuteSequence_SkipThrowingException() {
        Timer timer = new Timer(SLEEP_TIME_IN_MS, DURATION_IN_MS);
        Throwable throwable = new Throwable();
        ThrowablePackedResponse<String> out = new ThrowablePackedResponse<String>(null, throwable, false, new TimeoutException(throwable));
        FennecRuntimeException fennecRuntimeException;

        boolean isTimeoutExceptionThrown = false;

        try {
            out = timer.executeSequence(new Timer.Sequence<String>() {
                @Override
                public void run() {
                    setSkipThrowingException(true);
                    setReturningObject("huhu");
                    throw new FennecRuntimeException(msgFennecRuntimeException);
                }
            });
        } catch (TimeoutException e) {
            isTimeoutExceptionThrown = true;
        }

        String response = out.getResponse();
        fennecRuntimeException = (FennecRuntimeException) out.getThrowable();

        Assert.assertFalse(isTimeoutExceptionThrown, msgTimeoutExceptionThrown);
        Assert.assertNotNull(response, msgCorrectResponse);
        Assert.assertNotNull(fennecRuntimeException, msgCorrectThrowable);
        Assert.assertEquals(response, "huhu", msgCorrectResponse);
        Assert.assertEquals(fennecRuntimeException.getMessage(), msgFennecRuntimeException, msgCorrectThrowable);
    }
}
