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

import eu.tsystems.mms.tic.testframework.internal.IDUtils;
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

    private final String id = IDUtils.getB64encXID();
    private final String name;
    private final TestStep testStep;

    private final List<TestStepActionEntry> testStepActionEntries = Collections.synchronizedList(new LinkedList<>());

    public TestStepAction(TestStep testStep, String name) {
        this.testStep = testStep;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public TestStep getTestStep() {
        return testStep;
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

