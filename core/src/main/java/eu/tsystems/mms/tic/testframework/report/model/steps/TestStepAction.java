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
package eu.tsystems.mms.tic.testframework.report.model.steps;

import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by piet on 11.03.16.
 */
public class TestStepAction implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    private final String name;
    private final TestStep testStep;
    private long timestamp;

    private final List<TestStepActionEntry> testStepActionEntries = Collections.synchronizedList(new LinkedList<>());

    public TestStepAction(TestStep testStep, String name) {
        this.testStep = testStep;
        this.name = name;
        this.timestamp = System.currentTimeMillis();
    }

    public TestStep getTestStep() {
        return testStep;
    }

    /**
     * Usually, a TestStep already contains a {@link TestStepActionEntry} which contains a {@link LogMessage},
     * where the timestamp can be retrieved by calling {@link LogMessage#getTimestamp()}
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

    public List<TestStepActionEntry> getTestStepActionEntries() {
        return testStepActionEntries;
    }

    public void addLogMessage(LogMessage logMessage) {
        // We take the first real log message timestamp here
        if (testStepActionEntries.size()==0) {
            this.timestamp = logMessage.getTimestamp();
        }
        TestStepActionEntry testStepActionEntry = new TestStepActionEntry();
        testStepActionEntry.logMessage = logMessage;
        testStepActionEntries.add(testStepActionEntry);
    }

    public void addScreenshots(final Screenshot beforeShot, final Screenshot afterShot) {
        TestStepActionEntry testStepActionEntry = new TestStepActionEntry();
        testStepActionEntry.beforeScreenshot = beforeShot;
        testStepActionEntry.afterScreenshot = afterShot;
        testStepActionEntries.add(testStepActionEntry);
    }
//
//    public void addFailingLogMessage(final String msg) {
//        LogMessage logMessage = new LogMessage(
//            Level.ERROR,
//            System.currentTimeMillis(),
//            Thread.currentThread().getName(),
//            "Test Failed",
//            msg
//        );
//        final TestStepActionEntry testStepActionEntry = new TestStepActionEntry();
//        testStepActionEntry.logMessage = logMessage;
//        testStepActionEntries.add(testStepActionEntry);
//    }
}

