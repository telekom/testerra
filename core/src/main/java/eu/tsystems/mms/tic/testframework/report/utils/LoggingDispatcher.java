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
 * Created on 01.01.2012
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.report.utils;

import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A container class for logging and reporting. Creates testreport files and controls the logging.
 *
 * @author sepr, mrgi
 */
public final class LoggingDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingDispatcher.class);

    /**
     * List that contains -thread link offers-. If the method linkThread is called from a test steps, the link offer
     * will be stored here until the thread itself catches it and establishes the link. The offer contains the
     * threadname of the thread to link and the testresult of the current steps. The link is established when the
     * testresult is referenced to the thread to link.
     */
    private static final ConcurrentHashMap<String, ITestResult> THREAD_LINK_OFFERS = new ConcurrentHashMap<>();

    private static boolean STOP_REPORT_LOGGING = false;

    /**
     * Collection of unrelated logs.
     */
    static final List<LogMessage> UNRELATED_LOGS = Collections.synchronizedList(new LinkedList<LogMessage>());

    /**
     * Adds a logMessage message to MethodContext. Called in TesterraListener.class
     *
     * @param logMessage The logMessage message.
     */
    public static void addLogMessage(final LogMessage logMessage) {
        pAddLogMessage(logMessage);
    }

    /**
     * Adds a logMessage message to MethodContext. Called in TesterraListener.class
     *
     * @param logMessage The logMessage message.
     */
    private static void pAddLogMessage(LogMessage logMessage) {
        if (STOP_REPORT_LOGGING) {
            return;
        }

        // check if a thread link offer exists
        final String threadName = Thread.currentThread().getId() + "";
        if (THREAD_LINK_OFFERS.containsKey(threadName)) {
            ITestResult iTestResult = THREAD_LINK_OFFERS.get(threadName);
            // use the test result in this thread as well
            ExecutionContextController.setCurrentTestResult(iTestResult, iTestResult.getTestContext());
            THREAD_LINK_OFFERS.remove(threadName);
        }

        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext == null) {
            UNRELATED_LOGS.add(logMessage);
            return;
        }

        methodContext.addLogMessage(logMessage);
    }


    /**
     * Private constructor.
     */
    private LoggingDispatcher() {
    }

    public static void stopReportLogging() {
        STOP_REPORT_LOGGING = true;
    }

    /**
     * Link a thread to the current steps, so that the log appears in the steps (report).
     *
     * @param thread .
     */
    public static void linkThread(Thread thread) {
        if (ExecutionContextController.getCurrentTestResult() == null) {
            LOGGER.error("Linkage offer failed. There is no test result set, yet.");
            return;
        }

        String threadName = thread.getId() + "";
        // store the current test result to be used by the other thread as well
        THREAD_LINK_OFFERS.put(threadName, ExecutionContextController.getCurrentTestResult());
    }

}
