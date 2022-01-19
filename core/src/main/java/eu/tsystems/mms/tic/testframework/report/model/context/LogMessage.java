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
import java.util.Optional;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;

import java.io.Serializable;
import java.util.Date;
import org.apache.logging.log4j.message.Message;

/**
 * Copy of {@link LogEvent} because Log4j2 reuses the event instance
 * for passing new messages. This only happens under some circumstances we didn't
 * figure out yet.
 */
public class LogMessage implements Serializable, Loggable {
    private final long timestamp;
    private final String threadName;
    private final String loggerName;
    private final Throwable thrown;
    private final String message;
    private final Level level;
    private final boolean prompt;

    /**
     * Creates a log message based on a Log4J log event
     */
    public LogMessage(LogEvent event) {
        this.timestamp = event.getTimeMillis();
        this.threadName = event.getThreadName();
        this.loggerName = event.getLoggerName();
        this.thrown = event.getThrown();
        this.message = event.getMessage().getFormattedMessage();
        this.level = event.getLevel();
        this.prompt = false;
    }

    /**
     * Creates a prompt log message with {@link Level#INFO}
     */
    public LogMessage(Message message) {
        this(message, Level.INFO);
    }

    /**
     * Creates a prompt log message
     */
    public LogMessage(Message message, Level logLevel) {
        this(LogMessage.class.getSimpleName(), message, logLevel);
    }

    /**
     * Creates a prompt log message with logger name
     */
    public LogMessage(String loggerName, Message message, Level logLevel) {
        this.timestamp = new Date().getTime();
        this.threadName = Thread.currentThread().getName();
        this.loggerName = loggerName;
        this.thrown = message.getThrowable();
        this.message = message.getFormattedMessage();
        this.level = logLevel;
        this.prompt = true;
    }

    public Level getLogLevel() {
        return this.level;
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

    public Optional<Throwable> getThrown() {
        return Optional.ofNullable(this.thrown);
    }

    public boolean isPrompt() {
        return prompt;
    }
}
