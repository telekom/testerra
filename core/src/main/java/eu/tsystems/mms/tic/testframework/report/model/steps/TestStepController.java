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

public class TestStepController implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    private static final List<TestStepEventListener> listeners = new LinkedList<>();

    /*
    Test Steps Contexts
     */

    private final List<TestStep> testSteps = Collections.synchronizedList(new LinkedList<>());

    public TestStep getCurrentTestStep() {
        // if there are no active test step yet, create a new initial one
        if (testSteps.size() == 0) {
            return announceTestStep(TestStep.INTERNAL);
        }

        // get the last TestStep
        TestStep testStep = getLastStep();

        if (testStep.isClosed()) {
            return announceTestStep(TestStep.INTERNAL);
        }

        return testStep;
    }

    public TestStepAction addLogMessage(LogMessage logMessage) {
        String actionContext = null;

        for (TestStepEventListener listener : listeners) {
            String context = listener.getTestStepActionContext(logMessage);
            if (context != null) {
                actionContext = context;
                break;
            }
        }

        TestStep currentTestStep = getCurrentTestStep();
        TestStepAction testStepAction;
        if (actionContext==null) {
            testStepAction = currentTestStep.getCurrentTestStepAction();
        } else {
            testStepAction = currentTestStep.getTestStepAction(actionContext);
        }
        testStepAction.addLogMessage(logMessage);
        return testStepAction;
    }

    private TestStep getLastStep() {
        return testSteps.get(testSteps.size()-1);
    }

    public TestStep announceTestStep(String name) {
        // create a new one if empty
        TestStep testStep;
        if (testSteps.size() == 0) {
            testStep = new TestStep(name);
            testSteps.add(testStep);
        } else {
            // get the last TestStep
            testStep = getLastStep();

            // create new step if the steps differs
            if (!StringUtils.equals(testStep.getName(), name)) {
                testStep = new TestStep(name);
                testSteps.add(testStep);
            }
        }

        return testStep;
    }

    public void announceTestStepAction(String name) {
        getCurrentTestStep().getTestStepAction(name); // it is basically the same
    }

    public List<TestStep> getTestSteps() {
        return testSteps;
    }

    public enum OnExec {
        BEFORE, AFTER
    }

    public static void addEventListener(TestStepEventListener testStepEventListener) {
        listeners.add(testStepEventListener);
    }
}
