/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
 package eu.tsystems.mms.tic.testframework.report.model.steps;

import eu.tsystems.mms.tic.testframework.clickpath.ClickPathEvent;
import eu.tsystems.mms.tic.testframework.report.model.context.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.core.LogEvent;

public class TestStepAction implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    private final String name;
    private final long timestamp;
    private final List<Object> entries = new LinkedList<>();


    public TestStepAction(String name) {
        this.name = name;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Usually, a TestStep already contains a {@link LogEvent},
     * where the timestamp can be retrieved by calling {@link LogEvent#getTimeMillis()}
     * But when running under platform, the log messages are handled differently and not stored to the database within entities.
     * In this case, the TestSteps are stored WITHOUT log messages and WITHOUT timestamp, which is still required to find log messages according to time ranges.
     * Therefore, the TestStepAction gets it's own timestamp.
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    public String getName() {
        return name;
    }

    public <T> Stream<T> readEntries(Class<T> clazz) {
        return this.entries.stream().filter(clazz::isInstance).map(clazz::cast);
    }

    public void addAssertion(ErrorContext errorContext) {
        this.entries.add(errorContext);
    }

    @Deprecated
    public Stream<ErrorContext> readOptionalAssertions() {
        return readEntries(ErrorContext.class).filter(ErrorContext::isOptional);
    }

    @Deprecated
    public Stream<ErrorContext> readCollectedAssertions() {
        return readEntries(ErrorContext.class).filter(errorContext -> !errorContext.isOptional());
    }

    public void addLogMessage(LogMessage logMessage) {
        this.entries.add(logMessage);
    }

    public void addClickPathEvent(ClickPathEvent event) {
        this.entries.add(event);
    }

    public void addScreenshot(Screenshot screenshot) {
        this.entries.add(screenshot);
    }

    /**
     * @deprecated  Use {@link #readEntries()} instead
     * Used in methodDetailsStep.vm
     */
    public Collection<LogMessage> getLogMessages() {
        return this.readEntries(LogEvent.class).map(LogMessage::new).collect(Collectors.toList());
    }

    /**
     * @deprecated Use {@link #readEntries()} instead
     * Used in methodDetailsStep.vm
     */
    public Collection<Screenshot> getScreenshots() {
        return this.readEntries(Screenshot.class).collect(Collectors.toList());
    }

    public Stream<Object> readEntries() {
        return this.entries.stream();
    }
}

