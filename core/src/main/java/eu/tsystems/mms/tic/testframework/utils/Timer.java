/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by peter on 16.06.14.
 */
public class Timer {

    private static Logger LOGGER = LoggerFactory.getLogger(Timer.class);

    private long startTime = 0;
    private long sleepTimeInMs = 1000;
    private static final long SLEEP_TIME_IN_MS_MININMAL = 50;
    private long durationInMs = 15000;
    private static final long DURATION_IN_MS_MINIMAL = 100;
    private String errorMessage;

    /**
     * starts timer
     *
     * @deprecated - Please use a constructor with sleeptime and duration
     */
    @Deprecated
    public Timer() {
    }

    /**
     * timer with sleeptime
     *
     * @param sleepTimeInMs .
     * @param durationInMs  .
     */
    public Timer(long sleepTimeInMs, long durationInMs) {
        if (sleepTimeInMs > durationInMs) {
            LOGGER.error("SleepTime should not be greater than Duration of Timer. It will result in only one execution: " +
                    sleepTimeInMs + " > " + durationInMs);
        }
        this.sleepTimeInMs = sleepTimeInMs;
        this.durationInMs = durationInMs;
    }

    /**
     * The Sequence abstract to define a sequence within the run method. The Type declaration defines the return object type.
     *
     * @param <T>
     */
    public abstract static class Sequence<T> {

        private T returningObject = null;
        private Boolean passState = null;
        private boolean skipThrowingException = false;

        public boolean isSkipThrowingException() {
            return skipThrowingException;
        }

        public void setSkipThrowingException(boolean skipThrowingException) {
            this.skipThrowingException = skipThrowingException;
        }

        public Boolean getPassState() {
            return passState;
        }

        public void setPassState(Boolean passState) {
            this.passState = passState;
        }

        public T getReturningObject() {
            return returningObject;
        }

        public void setReturningObject(T returningObject) {
            this.returningObject = returningObject;
        }

        /**
         * just starts the run
         */
        public abstract void run() throws Throwable; // to allow to throw every kind or throwable and not only RTE
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Execute a sequence and kill it if it times out. Be careful, for killing it uses Thread.stop(), which
     * is deprecated, but the only way java can kill a thread.
     *
     * @param <T>      Return type.
     * @param sequence Sequence.
     */
    public <T> void executeSequenceThread(final Sequence<T> sequence) {

        final Thread thread = new Thread(() -> {

            try {
                executeSequence(sequence);
            } catch (TimeoutException e) {
                LOGGER.warn("Timeout in executed thread.", e);
            }
        });

        thread.setName("Sequence_" + sequence.hashCode());
        thread.start();
    }

    private void checkTimerValues() {
        if (sleepTimeInMs < SLEEP_TIME_IN_MS_MININMAL) {
            LOGGER.warn("invalid timer sleep time: " + sleepTimeInMs + ", setting it to " + SLEEP_TIME_IN_MS_MININMAL);
            sleepTimeInMs = SLEEP_TIME_IN_MS_MININMAL;
        }
        if (durationInMs < DURATION_IN_MS_MINIMAL) {
            LOGGER.warn("invalid timer duration: " + durationInMs + ", setting it to " + DURATION_IN_MS_MINIMAL);
            durationInMs = DURATION_IN_MS_MINIMAL;
        }
    }

    private void logMessage(String message) {
        LOGGER.debug(message);
    }

    /**
     * exectutes the following sequence
     *
     * @param <T>      .
     * @param sequence .
     * @return .
     */
    public <T> ThrowablePackedResponse<T> executeSequence(Sequence<T> sequence) {
        checkTimerValues();
        startTimer();
        boolean success;
        Throwable catchedThrowable = null;
        int runCount = 1;
        while (!isTimeOver()) {
            try {
                logMessage("##### Starting Sequence Iteration #" + runCount + " #####");
                sequence.run();
                LOGGER.debug("Sequence Iteration #" + runCount + " executed without throwing Throwable");
                /*
                Look at the pass state that could be set explicitly.
                 */
                Boolean passState = sequence.getPassState();
                if (passState == null) {
                    LOGGER.debug("Sequence Iteration #" + runCount + " successful without passState");
                    success = true;
                } else {
                    LOGGER.debug("Sequence Iteration #" + runCount + " pass state: " + passState);
                    success = passState;
                }
            } catch (Throwable throwable) {
                success = false;

                if (throwable instanceof OutOfMemoryError) {
                    throw new TesterraSystemException("OOME catched", throwable);
                } else if (throwable instanceof IllegalArgumentException) {
                    // jump out immediately
                    throw (IllegalArgumentException) throwable;
                } else {
                    catchedThrowable = throwable;
                    LOGGER.info("Sequence Iteration #" + runCount + " failed", throwable);
                }
            }
            runCount++;

            if (success) {
                return new ThrowablePackedResponse<T>(sequence.getReturningObject(), null, true, null);
            }

            try {
                Thread.sleep(sleepTimeInMs);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        // create timeout exception
        TimeoutException timeoutException = createTimeoutException(catchedThrowable);

        // give back a packed response when we have an object to give back or we have to skip throwing something
        if (sequence.getReturningObject() != null || sequence.isSkipThrowingException()) {
            // we return whatever we've got, catchedThrowable can also be null
            return new ThrowablePackedResponse<T>(sequence.getReturningObject(), catchedThrowable, false, timeoutException);
        }

        // throw
        throw timeoutException;
    }

    private TimeoutException createTimeoutException(Throwable catchedThrowable) {
                /*
        create timeout exception
         */
        String message = "Sequence execution timed out " + durationInMs + " ms (polling every " + sleepTimeInMs + " ms)";
        TimeoutException timeoutException;
        if (catchedThrowable != null) {
            /*
            Die lesbare Meldung sollte per setReadableMessageForThrowable gesetzt sein (wie bei GuiElement.find().
            Die Timing-Informationen würden bei einem rethrow verloren gehen, daher wird hier ein neuer Cause erzeugt und
            vorher die lesbare Meldung extrahiert.
            Funktioniert das gut? pele 25.06.2014
             */

            String catchedThrowableMessage;
            if (catchedThrowable instanceof AssertionError) {
                catchedThrowableMessage = catchedThrowable.toString();
            } else {
                catchedThrowableMessage = catchedThrowable.getMessage();
            }
            if (catchedThrowableMessage != null) {
                MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();
                if (currentMethodContext != null) {
                    currentMethodContext.errorContext().setThrowable(catchedThrowableMessage, catchedThrowable);
                }
            }
            timeoutException = new TimeoutException(message, catchedThrowable);
        } else {
            timeoutException = new TimeoutException(message);
        }

        // stack exception if errormessage is given
        if (!StringUtils.isStringEmpty(errorMessage)) {
            timeoutException = new TimeoutException(errorMessage, timeoutException);
        }

        return timeoutException;
    }

    /**
     * starts the timer
     */
    public void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public boolean isTimeOver() {
        return System.currentTimeMillis() > (startTime + durationInMs);
    }

    public long getSleepTimeInMs() {
        return sleepTimeInMs;
    }

    public void setSleepTimeInMs(long sleepTimeInMs) {
        this.sleepTimeInMs = sleepTimeInMs;
    }

    public long getDurationInMs() {
        return durationInMs;
    }

    public void setDurationInMs(long durationInMs) {
        this.durationInMs = durationInMs;
    }

    @Override
    public String toString() {
        return "Timer{" +
                "durationInMs=" + durationInMs +
                ", sleepTimeInMs=" + sleepTimeInMs +
                '}';
    }
}
