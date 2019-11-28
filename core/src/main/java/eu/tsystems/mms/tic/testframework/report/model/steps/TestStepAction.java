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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.utils.Formatter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by piet on 11.03.16.
 */
public class TestStepAction implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    private final String name;

    private final List<TestStepActionEntry> testStepActionEntries = Collections.synchronizedList(new LinkedList<>());
    private static final Formatter formatter = Testerra.injector.getInstance(Formatter.class);

    public TestStepAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<TestStepActionEntry> getTestStepActionEntries() {
        return testStepActionEntries;
    }

    public void addLogMessage(LogMessage logMessage) {
        final TestStepActionEntry testStepActionEntry = new TestStepActionEntry();
        testStepActionEntry.logMessage = logMessage;
        testStepActionEntries.add(testStepActionEntry);
    }

    public void addScreenshots(final Screenshot beforeShot, final Screenshot afterShot) {
        final TestStepActionEntry testStepActionEntry = new TestStepActionEntry();
        testStepActionEntry.beforeScreenshot = beforeShot;
        testStepActionEntry.afterScreenshot = afterShot;
        testStepActionEntries.add(testStepActionEntry);
    }

    public void addFailingLogMessage(final String msg) {
        LogMessage logMessage = new LogMessage(
                "ERROR",
                formatter.formatDate(new Date()),
                Thread.currentThread().getName(),
                "Test Failed",
                msg);
        final TestStepActionEntry testStepActionEntry = new TestStepActionEntry();
        testStepActionEntry.logMessage = logMessage;
        testStepActionEntries.add(testStepActionEntry);
    }
}

