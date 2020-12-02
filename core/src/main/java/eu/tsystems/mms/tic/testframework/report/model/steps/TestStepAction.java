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
    private long timestamp;
    private List<ErrorContext> optionalAssertions;
    private List<ErrorContext> collectedAssertions;
    private final List<LogEvent> logEvents = new LinkedList<>();
    private List<Screenshot> screenshots;
    private List<ClickPathEvent> clickPathEvents;

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

    public void addOptionalAssertion(ErrorContext errorContext) {
        if (this.optionalAssertions == null) {
            this.optionalAssertions = new LinkedList<>();
        }
        this.optionalAssertions.add(errorContext);
    }

    public Stream<ErrorContext> readOptionalAssertions() {
        if (this.optionalAssertions == null) {
            return Stream.empty();
        } else {
            return this.optionalAssertions.stream();
        }
    }

    public void addCollectedAssertions(List<ErrorContext> errorContexts) {
        if (this.collectedAssertions == null) {
            this.collectedAssertions = new LinkedList<>();
        }
        this.collectedAssertions.addAll(errorContexts);
    }

    public Stream<ErrorContext> readCollectedAssertions() {
        if (this.collectedAssertions == null) {
            return Stream.empty();
        } else {
            return this.collectedAssertions.stream();
        }
    }

    public void addLogEvent(LogEvent logEvent) {
        this.logEvents.add(logEvent);
    }

    public Stream<LogEvent> readLogEvents() {
        return this.logEvents.stream();
    }

    public void addClickPathEvent(ClickPathEvent event) {
        if (this.clickPathEvents == null) {
            this.clickPathEvents = new LinkedList<>();
        }
        this.clickPathEvents.add(event);
    }

    public Stream<ClickPathEvent> readClickPathEvents() {
        if (this.clickPathEvents == null) {
            return Stream.empty();
        } else {
            return this.clickPathEvents.stream();
        }
    }

    public void addScreenshot(Screenshot screenshot) {
        if (this.screenshots == null) {
            this.screenshots = new LinkedList<>();
        }
        this.screenshots.add(screenshot);
    }

    public Stream<Screenshot> readScreenshots() {
        if (this.screenshots == null) {
            return Stream.empty();
        } else {
            return this.screenshots.stream();
        }
    }

    /**
     * @deprecated Use {@link #readScreenshots()} ()} instead
     */
    public Collection<Screenshot> getScreenshots() {
        return this.readScreenshots().collect(Collectors.toList());
    }
}

