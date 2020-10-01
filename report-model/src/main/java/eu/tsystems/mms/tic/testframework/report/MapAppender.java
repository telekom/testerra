/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

public class MapAppender extends AbstractAppender {

    private ConcurrentMap<String, LogEvent> eventMap = new ConcurrentHashMap<>();

    public MapAppender() {
        super("MapAppender", null, null, true, null);
    }

    @Override
    public void append(LogEvent event) {
        /**
         * We dont log any messages from steps package,
         * because logs from {@link TestStep} also triggering logs by {@link LoggingDispatcher}
         * which may result in a callstack loop.
         */
        if (event.getLoggerName().startsWith(TestStep.class.getPackage().getName())) {
            return;
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
        LoggingDispatcher.getInstance().addLogMessage(logMessage);
    }
}
