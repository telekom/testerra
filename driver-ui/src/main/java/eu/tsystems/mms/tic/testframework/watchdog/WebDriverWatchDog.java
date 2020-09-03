/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.watchdog;

import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionAbortEvent;
import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.SecUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.TimingConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WebDriverWatchDog {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverWatchDog.class);

    private static class WatchDogThread extends Thread {

        private boolean stop = false;

        private final WatchDogRunnable watchDogRunnable;

        public WatchDogThread(WatchDogRunnable target) {
            super(target);
            this.watchDogRunnable = target;
        }

        private void stopWatchDog() throws InterruptedException {
            watchDogRunnable.stop();
            stop = true;

            // dont join, its not bad when it ends with the whole process
            //            join(THREAD_JOIN_TIMEOUT_MS);
        }

        public boolean isStop() {
            return stop;
        }
    }

    /**
     * threadId + threadName = nrOfIntervalsAlive
     */
    private static final Map<String, Integer> ACTIVE_FORWARDS = new HashMap<String, Integer>();

    private static class WatchDogRunnable implements Runnable {

        private boolean stop = false;

        private void stop() throws InterruptedException {
            stop = true;
        }

        private boolean isStop() {
            return stop;
        }

        @Override
        public void run() {
            LOGGER.debug("Started");
            while (!isStop()) {
                final Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
                final List<String> actualThreads = new ArrayList<String>();
                for (Thread thread : allStackTraces.keySet()) {
                    final String name = thread.getName();

                    /*
                    Check selenium sessions "Forwarding" that do a socket read (i think they all do) for more that the
                    specified time. Close them if they are in the timeout.
                     */
                    if (name != null && name.startsWith("Forwarding")) {
                        final StackTraceElement[] stackTrace = thread.getStackTrace();
                        if (stackTrace != null && stackTrace.length > 0) {
                            final StackTraceElement stackTraceElement = stackTrace[0];
                            if (stackTraceElement.toString().contains("java.net.SocketInputStream.socketRead0")) {
                                final long threadId = thread.getId();

                                /*
                                get readable stacktrace
                                 */
                                String readableStacktrace = "";
                                for (StackTraceElement traceElement : stackTrace) {
                                    readableStacktrace += traceElement.toString() + "\n";
                                }

                                /*
                                hash the fingerprint
                                 */
                                String stackTraceFingerprint = SecUtils.hash(readableStacktrace);

                                /*
                                Fingerprint
                                 */
                                final String key = "Thread " + threadId + ": " + name + " (" + stackTraceFingerprint + ")";

                                /*
                                add to actual thread list of found treads
                                 */
                                actualThreads.add(key);

                                /*
                                update map for this thread
                                */
                                int newCount = 1;
                                if (ACTIVE_FORWARDS.containsKey(key)) {
                                    newCount = ACTIVE_FORWARDS.get(key) + 1;
                                }
                                ACTIVE_FORWARDS.put(key, newCount);

                                final int passedSeconds = newCount * TimingConstants.WATCHDOG_THREAD_POLL_INTERVAL_SECONDS;

                                if (passedSeconds > TimingConstants.WATCHDOG_FIRST_ANNOUNCEMENT_SECONDS) {
                                    LOGGER.warn("(" + passedSeconds + "/" + TimingConstants.WEBDRIVER_COMMAND_TIMEOUT_SECONDS +
                                            " s) hanging a while now: " + key);
                                }

                                /*
                                Check for max interval
                                 */
                                if (passedSeconds > TimingConstants.WATCHDOG_THREAD_HANGING_TIMEOUT_SECONDS) {

                                    LOGGER.error("Hung thread, awaiting safe stop: " + key);

                                    if (passedSeconds > TimingConstants.WATCHDOG_FORCE_QUIT_TIMEOUT_SECONDS) {
                                        /*
                                        Generate Report and give up
                                         */
                                        try {
                                            ReportInfo.getDashboardWarning().addInfo(0, "Watchdog stopped the test");
                                            TesterraListener.getEventBus().post(new ExecutionAbortEvent());
                                        } finally {
                                            System.err.println("Causing stacktrace on thread " + threadId + ":\n" + readableStacktrace);
                                            System.err.println("\n" +
                                                    "\n" +
                                                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                                                    "\n" +
                                                    "STUCK SELENIUM COMMAND: " + key + "\n" +
                                                    "\n" +
                                                    "No chance to recover from that. \n" +
                                                    "\n" +
                                                    "The reason could be a stuck basic auth window, have a look at the browser!\n" +
                                                    "The browser windows used are still open.\n" +
                                                    "\n" +
                                                    "Exiting without parachute, system.exit..............................\n" +
                                                    "\n" +
                                                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                                                    "\n");


                                            // update crashed execution context
                                            ExecutionContextController.getCurrentExecutionContext().crashed = true;
                                            ContextUpdateEvent event = new ContextUpdateEvent().setContext(ExecutionContextController.getCurrentExecutionContext());
                                            TesterraListener.getEventBus().post(event);

                                            System.exit(99);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                /*
                Remove old threads from list
                 */
                List<String> toRemove = new ArrayList<String>();
                for (String key : ACTIVE_FORWARDS.keySet()) {
                    if (!actualThreads.contains(key)) {
                        toRemove.add(key);
                    }
                }
                for (String key : toRemove) {
                    LOGGER.debug("Session gone: " + key);
                    ACTIVE_FORWARDS.remove(key);
                }

                try {
                    Thread.sleep(TimingConstants.WATCHDOG_THREAD_POLL_INTERVAL_SECONDS * 1000);
                } catch (InterruptedException e) {
                    LOGGER.debug("Interrupted", e);
                }
            }
        }
    }

    private static final WatchDogThread WATCH_DOG_THREAD = new WatchDogThread(new WatchDogRunnable());

    private static boolean started = false;

    public static void start() {
        if (started) {
            return;
        }

        LOGGER.debug("Starting...");
        WATCH_DOG_THREAD.start();
        started = true;
    }

    public static void stop() {
        if (!started) {
            return;
        }

        try {
            LOGGER.debug("Stopping...");
            WATCH_DOG_THREAD.stopWatchDog();
            LOGGER.debug("Stopped");
        } catch (InterruptedException e) {
            LOGGER.error("Exception stopping WebDriverWatchDog", e);
        }
        started = false;
    }

}
