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
package eu.tsystems.mms.tic.testframework.report;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.utils.LoggingDispatcher;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import org.apache.log4j.spi.LoggingEvent;

public class ReportLogAppender extends BaseLoggingActor {

    @Inject
    public ReportLogAppender(Formatter formatter) {
        super(formatter);
    }

    /**
     * Appends a LoggingEvent to the HTML Report using Reporter.log().
     *
     * @param event The event to be logged.
     */
    @Override
    protected void append(final LoggingEvent event) {
        appendForReport(event);
        super.append(event);
    }

    /**
     * Appends a LoggingEvent to the HTML Report using Reporter.log().
     *
     * @param event The event to be logged.
     */
    private void appendForReport(final LoggingEvent event) {
        /**
         * We dont log any messages from steps package,
         * because logs from {@link TestStep} also triggering logs by {@link LoggingDispatcher}
         * which may result in a callstack loop.
         */
        if (event.getLoggerName().startsWith(TestStep.class.getPackage().getName())) {
            return;
        }

        /*
        We can't create a "new" message und just format it. So we create a formatted output from orig event and
        just replace the old message with the new content.
         */
//        final String origMessage = event.getMessage().toString();
//        final String formattedMessage = LAYOUT.format(event);
//        final String formattedTemplate = formattedMessage.replace(origMessage, PLACEHOLDER);
//
//        String out;
//
//        /*
//        Throwable toggle
//         */
//        final String htmlOrigMessage = StringUtils.prepareStringForHTML(origMessage);
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
//                    + htmlOrigMessage
//                    + "</a><br/><div id='exception-" + id + "' class='stackTrace'>";
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
        LoggingDispatcher.addLogMessage(logMessage);
    }
}
