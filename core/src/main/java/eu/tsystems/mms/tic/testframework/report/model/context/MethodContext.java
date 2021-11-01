/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.internal.Counters;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepController;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.testng.ITestResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Collectors;

/**
 * Holds the informations of an test method.
 *
 * @author mibu
 */
public class MethodContext extends AbstractContext implements SynchronizableContext {

    public enum Type {
        TEST_METHOD,
        CONFIGURATION_METHOD
    }

    private ITestResult testResult;
    private TestStatusController.Status status = TestStatusController.Status.NO_RUN;
    private final Type methodType;
    private List<Object> parameterValues;
    private int retryNumber = 0;
    public int methodRunIndex = -1;
    public String threadName = "unrelated";
    private TestStep lastFailedStep;
    private Class failureCorridorClass = FailureCorridor.High.class;

    /**
     * @deprecated
     */
    public final List<String> infos = new LinkedList<>();
    private final List<SessionContext> sessionContexts = new LinkedList<>();
    public String priorityMessage = null;
    private final TestStepController testStepController = new TestStepController();
    private List<MethodContext> relatedMethodContexts = new LinkedList<>();
    private final List<MethodContext> dependsOnMethodContexts = new LinkedList<>();
    private List<CustomContext> customContexts;

    /**
     * Public constructor. Creates a new <code>MethodContext</code> object.
     *
     * @param name The test method name.
     * @param methodType method type.
     * @param classContext .
     */
    public MethodContext(
            final String name,
            final Type methodType,
            final ClassContext classContext
    ) {
        this.name = name;
        this.parentContext = classContext;
        this.methodRunIndex = Counters.increaseMethodExecutionCounter();
        this.methodType = methodType;
    }

    public void setRetryCounter(int retryCounter) {
        this.retryNumber = retryCounter;
    }

    public int getRetryCounter() {
        return this.retryNumber;
    }

    /**
     * @deprecated Use {@link #getFailureCorridorClass()} instead
     */
    public FailureCorridor.Value getFailureCorridorValue() {
        if (this.failureCorridorClass.equals(FailureCorridor.High.class)) {
            return FailureCorridor.Value.HIGH;
        } else if (this.failureCorridorClass.equals(FailureCorridor.Mid.class)) {
            return FailureCorridor.Value.MID;
        } else {
            return FailureCorridor.Value.LOW;
        }
    }

    public void setFailureCorridorClass(Class failureCorridorClass) {
        this.failureCorridorClass = failureCorridorClass;
    }

    public Class getFailureCorridorClass() {
        return this.failureCorridorClass;
    }

    /**
     * @deprecated Use {@link #readCustomContexts()} instead
     */
    public List<CustomContext> getCustomContexts() {
        if (this.customContexts == null) {
            this.customContexts = new LinkedList<>();
        }
        return customContexts;
    }

    public void addCustomContext(CustomContext customContext) {
        this.getCustomContexts().add(customContext);
    }

    public Stream<CustomContext> readCustomContexts() {
        if (this.customContexts == null) {
            return Stream.empty();
        } else {
            return this.customContexts.stream();
        }
    }

    public Type getMethodType() {
        return this.methodType;
    }

    public Stream<SessionContext> readSessionContexts() {
        return sessionContexts.stream();
    }

    public void addSessionContext(SessionContext sessionContext) {
        if (!this.sessionContexts.contains(sessionContext)) {
            this.sessionContexts.add(sessionContext);
            sessionContext.addMethodContext(this);
            sessionContext.parentContext = this;
            Testerra.getEventBus().post(new ContextUpdateEvent().setContext(this));
        }
    }

    public ClassContext getClassContext() {
        return (ClassContext) this.parentContext;
    }

    @Deprecated
    public TestContext getTestContext() {
        return this.getClassContext().getTestContext();
    }

    @Deprecated
    public SuiteContext getSuiteContext() {
        return this.getTestContext().getSuiteContext();
    }

    @Deprecated
    public ExecutionContext getExecutionContext() {
        return this.getSuiteContext().getExecutionContext();
    }

    @Deprecated
    public int getNumAssertions() {
        return (int)readErrors().filter(errorContext -> !errorContext.isOptional()).count();
    }

    @Deprecated
    public int getNumOptionalAssertions() {
        return (int)readErrors().filter(ErrorContext::isOptional).count();
    }

    public Stream<MethodContext> readRelatedMethodContexts() {
        return this.relatedMethodContexts.stream();
    }

    public Stream<MethodContext> readDependsOnMethodContexts() {
        return this.dependsOnMethodContexts.stream();
    }

    public void setRelatedMethodContexts(List<MethodContext> relatedMethodContexts) {
        this.relatedMethodContexts = relatedMethodContexts;
    }

    public void addDependsOnMethod(MethodContext methodContext) {
        if (!this.dependsOnMethodContexts.contains(methodContext)) {
            this.dependsOnMethodContexts.add(methodContext);
        }
    }

    private Stream<TestStepAction> readTestStepActions() {
        return this.readTestSteps().flatMap(testStep -> testStep.getTestStepActions().stream());
    }

    public TestStepAction addLogMessage(LogMessage logMessage) {
        return testStepController.addLogMessage(logMessage);
    }

    public TestStep getTestStep(String name) {
        return this.testStepController.getTestStep(name);
    }

    public Stream<TestStep> readTestSteps() {
        return this.testStepController.getTestSteps().stream();
    }

    public TestStep getCurrentTestStep() {
        return this.testStepController.getCurrentTestStep();
    }

    public int getLastFailedTestStepIndex() {
        return this.testStepController.getTestSteps().indexOf(this.lastFailedStep);
    }

    public void setFailedStep(TestStep step) {
        this.lastFailedStep = step;
    }

    public Stream<ErrorContext> readErrors() {
        return readTestStepActions().flatMap(TestStepAction::readErrors);
    }

    @Override
    public boolean equals(final Object obj) {

        if (!(obj instanceof MethodContext)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        return obj.toString().equals(toString());
    }

    /**
     * checks if test was skipped
     *
     * @return the skipped
     */
    public boolean isSkipped() {
        return status == TestStatusController.Status.SKIPPED;
    }

    /**
     * Gets whether the test method execution was successful.
     *
     * @return The value of <code>successful</code>.
     */
    public boolean isSuccessful() {
        return status == TestStatusController.Status.PASSED || status == TestStatusController.Status.MINOR;
    }

    public void addOptionalAssertion(Throwable throwable) {
        getCurrentTestStep().getCurrentTestStepAction().addAssertion(new ErrorContext(throwable, true));
    }

    public void addError(Throwable throwable) {
        addError(new ErrorContext(throwable, false));
    }

    public void addError(ErrorContext errorContext) {
        getCurrentTestStep().getCurrentTestStepAction().addAssertion(errorContext);
    }

    public boolean isRetry() {
        return status == TestStatusController.Status.FAILED_RETRIED;
    }

    public boolean isFailed() {
        switch (status) {
            case FAILED_EXPECTED:
            case FAILED:
            case FAILED_MINOR:
            case FAILED_RETRIED:
                return true;

            default:
                return false;
        }
    }

    public boolean isPassed() {
        switch (status) {
            case PASSED:
            case PASSED_RETRY:
            case MINOR:
            case MINOR_RETRY:
                return true;

            default:
                return false;
        }
    }

    @Deprecated
    public void addPriorityMessage(String msg) {

        if (priorityMessage == null) {
            priorityMessage = "";
        }

        if (!priorityMessage.contains(msg)) {
            priorityMessage += msg;
        }
    }

    public void setThreadName() {
        final Thread currentThread = Thread.currentThread();
        this.threadName = currentThread.getName() + "#" + currentThread.getId();
    }

    public boolean isConfigMethod() {
        return methodType == Type.CONFIGURATION_METHOD;
    }

    public boolean isTestMethod() {
        return !isConfigMethod();
    }

    public boolean isExpectedFailed() {
        return status == TestStatusController.Status.FAILED_EXPECTED;
    }

    public boolean hasBeenRetried() {
        return retryNumber > 0;
    }

    @Override
    public TestStatusController.Status getStatus() {
        return status;
    }

    public void setStatus(TestStatusController.Status status) {
        this.status = status;
    }

    public boolean isRepresentationalTestMethod() {
        if (isConfigMethod()) {
            return false;
        }
        switch (status) {
            case NO_RUN:
            case FAILED_RETRIED:
            case INFO:
            case FAILED_EXPECTED:
                return false;

            case PASSED:
            case PASSED_RETRY:
            case MINOR:
            case MINOR_RETRY:
            case FAILED:
            case FAILED_MINOR:
            case SKIPPED:
                return true;

            default:
                throw new SystemException("Method state not implemented: " + status);
        }
    }

    @Override
    public String toString() {
        return "MethodContext{" +
                "methodRunIndex=" + methodRunIndex +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Publish the screenshots to the report into the current errorContext.
     */
    public void addScreenshots(Stream<Screenshot> screenshots) {
        screenshots.forEach(this::addScreenshot);
    }

    public void addScreenshot(Screenshot screenshot) {
        this.testStepController.getCurrentTestStep().getCurrentTestStepAction().addScreenshot(screenshot);
    }

    /**
     * Proper parameter names are available by setting {https://stackoverflow.com/questions/6759880/getting-the-name-of-a-method-parameter}
     */
    public Parameter[] getParameters() {
        return getTestNgResult().map(testResult -> testResult.getMethod().getConstructorOrMethod().getMethod().getParameters()).orElse(new Parameter[]{});
    }

    public MethodContext setParameterValues(Object[] parameters) {
        // TestNG method parameters can be NULL
        this.parameterValues = Arrays.stream(parameters).filter(Objects::nonNull).collect(Collectors.toList());
        return this;
    }

    public List<Object> getParameterValues() {
        if (this.parameterValues == null) {
            return Collections.emptyList();
        } else {
            return this.parameterValues;
        }
    }

    public Optional<ITestResult> getTestNgResult() {
        return Optional.ofNullable(this.testResult);
    }

    public MethodContext setTestNgResult(ITestResult testResult) {
        this.testResult = testResult;
        return this;
    }

    public Stream<Annotation> readAnnotations() {
        return getTestNgResult()
                        .map(testResult -> Stream.of(testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotations()))
                        .orElse(Stream.empty());
    }

}
