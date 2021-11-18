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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * A static wrapper for {@link MethodContext#readTestSteps()}
 * Created by piet on 11.03.16.
 */
public class TestStep implements Serializable, Loggable {

    private static final long serialVersionUID = Serial.SERIAL;
    public static final String SETUP="Setup";
    public static final String TEARDOWN="TearDown";
    public static final String INTERNAL="Internal";

    private final String name;
    private final List<TestStepAction> testStepActions = new LinkedList<>();
    private boolean closed = false;

    TestStep(String name) {
        this.name = name;
    }

    public boolean isInternalTestStep() {
        switch (name) {
            case SETUP:
            case TEARDOWN:
            case INTERNAL:
                return true;
            default:
                return false;
        }
    }

    public String getName() {
        return name;
    }

    public List<TestStepAction> getTestStepActions() {
        return testStepActions;
    }

    /**
     * @return The last action if it matches the name, otherwise a new action is returned.
     */
    public TestStepAction getTestStepAction(String name) {
        if (name==null || name.isEmpty()) {
            name = INTERNAL;
        }
        // if there are no test steps actions yet, create an initial one
        if (testStepActions.size() == 0) {
            TestStepAction testStepAction = new TestStepAction(name);
            testStepActions.add(testStepAction);
            return testStepAction;
        }
        // get the last TestStepAction
        TestStepAction testStepAction = getLastAction();

        // if the last TestStepAction name is NOT the same as the current contextual one, then we have to create a new one
        if (!StringUtils.equals(testStepAction.getName(), name)) {
            testStepAction = new TestStepAction(name);
            testStepActions.add(testStepAction);
        }

        return testStepAction;
    }

    public void open() {
        if (!isInternalTestStep()) {
            log().info("Begin " + name);
        }
    }

    public void close() {
        closed = true;
        if (!isInternalTestStep()) {
            log().info("End " + name);
        }
    }

    public boolean isClosed() {
        return closed;
    }

    private TestStepAction getLastAction() {
        return testStepActions.get(testStepActions.size() - 1);
    }

    public TestStepAction getCurrentTestStepAction() {
        if (testStepActions.size() == 0) {
            return getTestStepAction(INTERNAL);
        }
        return getLastAction();
    }

    /**
     * Announce a new Test Step.
     *
     * @param name
     */
    public static TestStep begin(final String name) {
        Optional<MethodContext> optionalMethodContext = ExecutionContextController.getMethodContextForThread();
        TestStep testStep;
        if (optionalMethodContext.isPresent()) {
            testStep = optionalMethodContext.get().getTestStep(name);
        } else {
            testStep = new TestStep(name);
        }

        return testStep;
    }

    /**
     * End the current test step. In most cases this is not needed. Just announce a new one!
     */
    public static void end() {
        ExecutionContextController.getMethodContextForThread().ifPresent(methodContext -> {
            methodContext.getCurrentTestStep().close();
        });
    }

    public boolean hasActions() {
        return testStepActions.size() > 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
