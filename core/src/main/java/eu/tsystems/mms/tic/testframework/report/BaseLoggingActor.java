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
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.LoggingDispatcher;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * BaseLoggingActor allows to use log4j logs for HTML Reports.
 */
public class BaseLoggingActor extends AppenderSkeleton {

    private static final List<LoggingActor> LOGGING_ACTORS = new LinkedList<>();

    /*
    !!!!

    LOGGER_PATTERN must match the split pattern and elements in createLogMessage()!

    !!!
     */
    private static final String DATE_FORMAT = "dd.MM.yyyy-HH:mm:ss.SSS";

    public static final String LOGGER_PATTERN = "%p---%d{" + DATE_FORMAT + "}---%t---%c{1}---%m%n";

    public static final Layout CONSOLE_LAYOUT = new PatternLayout("%d{dd.MM.yyyy HH:mm:ss.SSS} [%t] [%-5p]: %c{2} - %m");
    public static final Layout CONSOLE_LAYOUT_MCID = new PatternLayout("%d{dd.MM.yyyy HH:mm:ss.SSS} [%t] [%-5p]: %c{2} - [MCID:%X{mcid}] %m");

    public static final String SPLITTER = "---";
    public static final Layout LAYOUT = new PatternLayout(LOGGER_PATTERN);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);

    public static LogMessage createLogMessage(final String msg) {
        if (msg == null) {
            return null;
        }

        String[] split = msg.split(SPLITTER);
        try {
            return new LogMessage(split[0], split[1], split[2], split[3], split[4]);
        } catch (ArrayIndexOutOfBoundsException e) {
            /*
            I can't use logging to log a logging error :)
             */
            System.err.println("Error creating LogMessage from: " + msg);
            e.printStackTrace();
            return null;
        }
    }

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
        appendForReport(event);

        // enhance with method context id
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        String formattedMessage;
        boolean withMCID = false;
        if (methodContext != null) {
            event.setProperty("mcid", methodContext.id);
            formattedMessage = CONSOLE_LAYOUT_MCID.format(event);
            withMCID = true;
        }
        else {
            formattedMessage = CONSOLE_LAYOUT.format(event);
        }

        // append for console
        if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            System.err.println(formattedMessage);
        }
        else {
            System.out.println(formattedMessage);
        }

        // append for any other actors
        for (LoggingActor loggingActor : LOGGING_ACTORS) {
            loggingActor.process(event, formattedMessage, withMCID);
        }

    }

    private static final String PLACEHOLDER = "###LOGMESSAGE###";

    /**
     * Appends a LoggingEvent to the HTML Report using Reporter.log().
     *
     * @param event The event to be logged.
     */
    private void appendForReport(final LoggingEvent event) {
        if (event != null) {
            /*
            We can't create a "new" message und just format it. So we create a formatted output from orig event and
            just replace the old message with the new content.
             */
            final String origMessage = event.getMessage().toString();
            final String formattedMessage = LAYOUT.format(event);
            final String formattedTemplate = formattedMessage.replace(origMessage, PLACEHOLDER);

            String out;

            /*
            Throwable toggle
             */
            final String htmlOrigMessage = StringUtils.prepareStringForHTML(origMessage);
            if (event.getThrowableInformation() != null) {
                String[] throwableStrRep = event.getThrowableInformation().getThrowableStrRep();

                if (throwableStrRep != null) {

                    /*
                     * Reformat log
                     */
                    String id = System.currentTimeMillis() + "";
                    out = "<a href=\"javascript:toggleElement('exception-" + id + "')\">"
                                    + htmlOrigMessage
                                    + "</a><br/><div id='exception-" + id + "' class='stackTrace'>";

                    for (String line : throwableStrRep) {
                        line = StringUtils.prepareStringForHTML(line);
                        out += line + "<br/>";
                    }

                    out += "</div>";
                }
                else {
                    out = htmlOrigMessage;
                }
            }
            else {
                out = htmlOrigMessage;
            }

            /*
             * Add log message.
             */
            final String finalLogMessage = formattedTemplate.replace(PLACEHOLDER, out);
            LogMessage logMessage = createLogMessage(finalLogMessage);
            LoggingDispatcher.addLogMessage(logMessage);
        }
    }

    public static void registerLoggingActor(LoggingActor loggingActor) {
        LOGGING_ACTORS.add(loggingActor);
    }

}
