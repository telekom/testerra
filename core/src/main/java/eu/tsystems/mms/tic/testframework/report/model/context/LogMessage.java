/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;

import java.io.Serializable;
import java.util.Date;

/**
 * Clone of {@link LogEvent}
 */
public class LogMessage implements Serializable, Loggable {
    private final long timestamp;
    private final String threadName;
    private final String loggerName;
    private final Throwable thrown;
    private final String message;
    private final Level level;

    public LogMessage(LogEvent event) {
        this.timestamp = event.getTimeMillis();
        this.threadName = event.getThreadName();
        this.loggerName = event.getLoggerName();
        this.thrown = event.getThrown();
        this.message = event.getMessage().getFormattedMessage();
        this.level = event.getLevel();
    }

    public Level getLogLevel() {
        return this.level;
    }

    /**
     * Required by velocity templates
     */
    @Deprecated
    public Date getDate() {
        return new Date(this.getTimestamp());
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getThreadName() {
        return this.threadName;
    }

    public String getLoggerName() {
        return this.loggerName;
    }

    public String getMessage() {
        return this.message;
    }

    public Throwable getThrown() {
        return this.thrown;
    }
}
