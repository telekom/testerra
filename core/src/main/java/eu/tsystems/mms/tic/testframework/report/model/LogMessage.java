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
package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.BaseLoggingActor;

import java.io.Serializable;

/**
 * Created by piet on 08.12.16.
 */
public class LogMessage implements Serializable {

    public final String logLevel;
    public final String date;
    public final String threadName;
    public final String loggerName;
    public final String message;

    public LogMessage(String logLevel, String date, String threadName, String loggerName, String message) {
        this.logLevel = logLevel;
        this.date = date;
        this.threadName = threadName;
        this.loggerName = loggerName;
        this.message = message;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getDate() {
        return date;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return logLevel + BaseLoggingActor.SPLITTER +
                date + BaseLoggingActor.SPLITTER +
                threadName + BaseLoggingActor.SPLITTER +
                loggerName + BaseLoggingActor.SPLITTER +
                message;
    }
}
