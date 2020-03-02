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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * BaseLoggingActor allows to use log4j logs for HTML Reports.
 */
public class DefaultLogAppender extends AppenderSkeleton implements TesterraLogger {
    private LogFormatter formatter = new DefaultLogFormatter();

    @Override
    public DefaultLogAppender setFormatter(LogFormatter formatter) {
        this.formatter = formatter;
        return this;
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
        String formattedMessage = this.formatter.format(event);

        // append for console
        if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            System.err.println(formattedMessage);
        } else {
            System.out.println(formattedMessage);
        }
    }
}
