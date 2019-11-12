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

import eu.tsystems.mms.tic.testframework.report.model.Serial;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by piet on 11.03.16.
 */
public class TestStep implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    private String name;
    private int number;
    private List<TestStepAction> testStepActions = Collections.synchronizedList(new LinkedList<>());
    private boolean closed = false;

    public TestStep(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public List<TestStepAction> getTestStepActions() {
        return testStepActions;
    }

    public TestStepAction getTestStepAction(final String context) {
        // if there are no test steps actions yet, create an initial one
        if (testStepActions.size() == 0) {
            TestStepAction testStepAction = new TestStepAction(context, 1);
            testStepActions.add(testStepAction);
            return testStepAction;
        }
        // get the last TestStepAction
        TestStepAction testStepAction = testStepActions.get(testStepActions.size() - 1);

        // if the last TestStepAction name is NOT the same as the current contextual one, then we have to create a new one
        if (!StringUtils.equals(testStepAction.getName(), context)) {
            testStepAction = new TestStepAction(context, testStepAction.getNumber() + 1);
            testStepActions.add(testStepAction);
        }

        return testStepAction;
    }

    public void close() {
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    public TestStepAction getCurrentTestStepAction() {
        if (testStepActions.size() == 0) {
            return getTestStepAction("Internal");
        }
        return testStepActions.get(testStepActions.size() - 1);
    }

    /**
     * Announce a new Test Step.
     *
     * @param name
     */
    public static TestStep begin(final String name) {
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        TestStep testStep = null;
        if (methodContext != null) {
            testStep = methodContext.steps().announceTestStep(name);
        }

        if (testStep == null) {
            testStep = new TestStep("dummy", 1);
        }

        return testStep;
    }

    /**
     * End the current test step. In most cases this is not needed. Just announce a new one!
     */
    public static void end() {
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            TestStep actualTestStep = methodContext.steps().getCurrentTestStep();
            if (actualTestStep != null) {
                actualTestStep.close();
            }
        }
    }

    public boolean hasActions() {
        return testStepActions.size() > 0;
    }

    @Override
    public String toString() {
        if (name != null) {
            String nameLowerCase = name.toLowerCase();
            if (nameLowerCase.contains("step") || nameLowerCase.contains("schritt")) {
                return name;
            } else {
                return "Step " + name;
            }
        } else {
            return "Step " + number;
        }
    }
}
