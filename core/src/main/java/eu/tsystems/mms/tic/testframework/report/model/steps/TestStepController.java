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

import eu.tsystems.mms.tic.testframework.report.model.Serial;
import eu.tsystems.mms.tic.testframework.report.model.context.LogMessage;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class TestStepController implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    private static final List<TestStepHandler> handlers = new LinkedList<>();

    /*
    Test Steps Contexts
     */

    private final List<TestStep> testSteps = new LinkedList<>();

    public TestStep getCurrentTestStep() {
        // if there are no active test step yet, create a new initial one
        if (testSteps.size() == 0) {
            return getTestStep(TestStep.INTERNAL);
        }

        // get the last TestStep
        TestStep testStep = getLastStep();

        if (testStep.isClosed()) {
            return getTestStep(TestStep.INTERNAL);
        }

        return testStep;
    }

    public TestStepAction addLogMessage(LogMessage logMessage) {
        String actionContext = null;

        for (TestStepHandler listener : handlers) {
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

    public TestStep getTestStep(String name) {
        // create a new one if empty
        TestStep testStep;
        if (testSteps.size() == 0) {
            testStep = createTestStep(name);
        } else {
            // get the last TestStep
            testStep = getLastStep();

            // create new step if the steps differs
            if (!StringUtils.equals(testStep.getName(), name)) {
                testStep = createTestStep(name);
            }
        }

        return testStep;
    }

    private TestStep createTestStep(String name) {
        TestStep testStep = new TestStep(name);
        testSteps.add(testStep);
        testStep.open();
        return testStep;
    }

    public List<TestStep> getTestSteps() {
        return testSteps;
    }

    public static void addHandler(TestStepHandler handler) {
        handlers.add(handler);
    }
}
