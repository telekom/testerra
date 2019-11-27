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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import java.text.SimpleDateFormat;

/**
 * Allows to use log4j logs for HTML Reports.
 */
public class BaseLoggingActor extends AppenderSkeleton {
    /*
    !!!!

    LOGGER_PATTERN must match the split pattern and elements in createLogMessage()!

    !!!
     */
    protected static final String DATE_FORMAT = "dd.MM.yyyy-HH:mm:ss.SSS";
    public static final String LOGGER_PATTERN = "%p---%d{" + DATE_FORMAT + "}---%t---%c{1}---%m%n";
    public static final Layout CONSOLE_LAYOUT = new PatternLayout("%d{"+DATE_FORMAT+"} [%t] [%-5p]: %c{2} - %m");
    public static final String SPLITTER = "---";
    public static final Layout LAYOUT = new PatternLayout(LOGGER_PATTERN);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);

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
        String formattedMessage = CONSOLE_LAYOUT.format(event);

        // append for console
        if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            System.err.println(formattedMessage);
        } else {
            System.out.println(formattedMessage);
        }
    }
}
