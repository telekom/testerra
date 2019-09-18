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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.report.model.steps;

import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by piet on 11.03.16.
 */
public class TestStepController implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    private static final List<TestStepEventListener> listeners = new LinkedList<>();

    /*
    Test Steps Contexts
     */

    private List<TestStep> testSteps = Collections.synchronizedList(new LinkedList<TestStep>());

    public TestStep getCurrentTestStep() {
        // if there are no test steps yet, create an initial one
        if (testSteps.size() == 0) {
            TestStep testStep = new TestStep(null, 1);
            testSteps.add(testStep);
            return testStep;
        }

        // get the last TestStep
        TestStep testStep = testSteps.get(testSteps.size() - 1);

        if (testStep.isClosed()) {
            testStep = new TestStep(null, testStep.getNumber() + 1);
            testSteps.add(testStep);
        }

        return testStep;
    }

    public void addLogMessage(LogMessage logMessage) {
        String actionContext = null;

        for (TestStepEventListener listener : listeners) {
            String context = listener.getTestStepActionContext(logMessage);
            if (context != null) {
                actionContext = context;
                break;
            }
        }

        TestStepAction testStepAction = getCurrentTestStep().getTestStepAction(actionContext);
        // ok, now add log messages
        testStepAction.addLogMessage(logMessage);
    }

    public TestStep announceTestStep(final String name) {
        // create a new one if empty
        TestStep testStep;
        if (testSteps.size() == 0) {
            testStep = new TestStep(name, 1);
            testSteps.add(testStep);
        } else {
            // get the last TestStep
            testStep = testSteps.get(testSteps.size() - 1);

            // create new step if the steps differs
            if (!StringUtils.equals(testStep.getName(), name)) {
                testStep = new TestStep(name, testStep.getNumber() + 1);
                testSteps.add(testStep);
            }
        }

        return testStep;
    }

    public void announceTestStepAction(final String name) {
        getCurrentTestStep().getTestStepAction(name); // it is basically the same
    }

    public List<TestStep> getTestSteps() {
        return testSteps;
    }

    public enum OnExec {
        BEFORE, AFTER
    }

    public static void addScreenshotsToCurrentAction(final Screenshot beforeShot, final Screenshot afterShot) {
        if (beforeShot == null && afterShot == null) {
            return;
        }

        final MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            TestStep actualTestStep = methodContext.steps().getCurrentTestStep();

            if (!actualTestStep.hasActions()) {
                actualTestStep.getTestStepActions().add(new TestStepAction("Screenshot", 1));
            }
            final TestStepAction actualAction = actualTestStep.getCurrentTestStepAction();
            actualAction.addScreenshots(beforeShot, afterShot);
        }
    }

    public static void addEventListener(TestStepEventListener testStepEventListener) {
        listeners.add(testStepEventListener);
    }
}
