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
 *     Peter Lehmann
 *     pele
 */
/*
 * Created on 01.01.2012
 *
 * Copyright(c) 2012 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.interop.LoggingActor;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.LoggingDispatcher;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * BaseLoggingActor allows to use log4j logs for HTML Reports.
 */
public class BaseLoggingActor extends AppenderSkeleton {

    @Deprecated
    private static final List<LoggingActor> LOGGING_ACTORS = new LinkedList<>();
    public static final Layout CONSOLE_LAYOUT = new PatternLayout("%d{dd.MM.yyyy HH:mm:ss.SSS} [%t] [%-5p]: %c{2} -%X{ids} %m");

    @Override
    public void close() {
        // Nothing to do here.
    }

    /**
     * Layout should be given.
     *
     * @return Returns always true.
     */
    @Override
    public boolean requiresLayout() {
        return false;
    }

    /**
     * Appends a LoggingEvent to the HTML Report using Reporter.log().
     *
     * @param event The event to be logged.
     */
    @Override
    protected void append(final LoggingEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ");

        // enhance with method context id
        SessionContext currentSessionContext = ExecutionContextController.getCurrentSessionContext();
        if (currentSessionContext != null) {
            sb.append("[SCID:").append(currentSessionContext.id).append("]");
        }

        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        boolean withMCID = false;
        if (methodContext != null) {
            sb.append("[MCID:").append(methodContext.id).append("]");
            withMCID = true;
        }

        /**
         * Add the IDs for TestStep and Action
         */
        appendForReport(event);
//        TestStepAction testStepAction = appendForReport(event);
//        if (testStepAction != null) {
//            sb.append("[TSID:").append(testStepAction.getTestStep().getId()).append("-").append(testStepAction.getId()).append("]");
//        }

        event.setProperty("ids", sb.toString());
        String formattedMessage = CONSOLE_LAYOUT.format(event);

        // append for console
        if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            System.err.println(formattedMessage);
        } else {
            System.out.println(formattedMessage);
        }

        // append for any other actors
        for (LoggingActor loggingActor : LOGGING_ACTORS) {
            loggingActor.process(event, formattedMessage, withMCID);
        }
    }

    /**
     * Appends a LoggingEvent to the HTML Report using Reporter.log().
     *
     * @param event The event to be logged.
     */
    private TestStepAction appendForReport(final LoggingEvent event) {

        /**
         * We dont log any messages from steps package,
         * because logs from {@link TestStep} also triggering logs by {@link LoggingDispatcher}
         * which may result in a callstack loop.
         */
        if (event.getLoggerName().startsWith(TestStep.class.getPackage().getName())) {
            return null;
        }

        /*
        Throwable toggle
         */
        //final String htmlOrigMessage = StringUtils.prepareStringForHTML(origMessage);
//        if (event.getThrowableInformation() != null) {
//            String[] throwableStrRep = event.getThrowableInformation().getThrowableStrRep();
//
//            if (throwableStrRep != null) {
//
//                /*
//                 * Reformat log
//                 */
//                String id = System.currentTimeMillis() + "";
//                out = "<a href=\"javascript:toggleElement('exception-" + id + "')\">"
//                                + htmlOrigMessage
//                                + "</a><br/><div id='exception-" + id + "' class='stackTrace'>";
//
//                for (String line : throwableStrRep) {
//                    line = StringUtils.prepareStringForHTML(line);
//                    out += line + "<br/>";
//                }
//
//                out += "</div>";
//            }
//            else {
//                out = htmlOrigMessage;
//            }
//        }
//        else {
//            out = htmlOrigMessage;
//        }

        /*
         * Add log message.
         */
        LogMessage logMessage = new LogMessage(event);
        return LoggingDispatcher.addLogMessage(logMessage);
    }

    @Deprecated
    public static void registerLoggingActor(LoggingActor loggingActor) {
        LOGGING_ACTORS.add(loggingActor);
    }

}
