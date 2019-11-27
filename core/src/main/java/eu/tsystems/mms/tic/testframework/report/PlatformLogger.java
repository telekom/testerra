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

import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class PlatformLogger extends BaseLoggingActor {

    public static final Layout CONSOLE_LAYOUT_MCID = new PatternLayout("%d{"+DATE_FORMAT+"} [%t] [%-5p]: %c{2} - [MCID:%X{mcid}] %m");
    public static final String SPLITTER = "---";
    public static final Layout LAYOUT = new PatternLayout(LOGGER_PATTERN);

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
        // enhance with method context id
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        String formattedMessage;
        if (methodContext != null) {
            event.setProperty("mcid", methodContext.id);
            formattedMessage = CONSOLE_LAYOUT_MCID.format(event);
            // append for console
            if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
                System.err.println(formattedMessage);
            }
            else {
                System.out.println(formattedMessage);
            }
        } else {
            super.append(event);
        }
    }
}
