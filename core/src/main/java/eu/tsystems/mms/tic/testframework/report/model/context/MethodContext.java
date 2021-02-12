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
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.internal.Counters;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepController;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.core.LogEvent;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

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

    /**
     * @deprecated
     */
    public ITestResult testResult;
    /**
     * @deprecated
     */
    public ITestContext iTestContext;
    /**
     * @deprecated
     */
    public ITestNGMethod iTestNgMethod;

    private TestStatusController.Status status = TestStatusController.Status.NO_RUN;
    private final Type methodType;
    private List<Object> parameterValues;
    public int retryNumber = 0;
    public int methodRunIndex = -1;
    public String threadName = "unrelated";
    private TestStep lastFailedStep;
    private Class failureCorridorClass = FailureCorridor.High.class;
    private int hashCodeOfTestResult = 0;
    public final List<String> infos = new LinkedList<>();
    private List<SessionContext> sessionContexts = new LinkedList<>();
    public String priorityMessage = null;
    private final TestStepController testStepController = new TestStepController();
    private List<MethodContext> relatedMethodContexts = new LinkedList<>();
    private List<MethodContext> dependsOnMethodContexts = new LinkedList<>();
    private List<CustomContext> customContexts;
    private ErrorContext errorContext;
    private int numAssertions = 0;
    private int numOptionalAssertions = 0;

    /**
     * Public constructor. Creates a new <code>MethodContext</code> object.
     *
     * @param name             The test method name.
     * @param methodType       method type.
     * @param classContext     .
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
            sessionContext.parentContext = this;
            TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(this));
        }
    }

    public ClassContext getClassContext() {
        return (ClassContext)this.parentContext;
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

    public int getNumAssertions() {
        return numAssertions;
    }

    public int getNumOptionalAssertions() {
        return numOptionalAssertions;
    }

    /**
     * @deprecated Use {@link #readTestSteps()} instead
     */
    public List<ErrorContext> getNonFunctionalInfos() {
        return this.readTestStepActions()
                .flatMap(TestStepAction::readOptionalAssertions)
                .collect(Collectors.toList());
    }

    /**
     * @deprecated Use {@link #readCollectedAssertions()} instead
     */
    public List<ErrorContext> getCollectedAssertions() {
        return readCollectedAssertions().collect(Collectors.toList());
    }

    /**
     * @deprecated Use {@link #readTestSteps()} instead
     */
    public Stream<ErrorContext> readCollectedAssertions() {
        return this.readTestStepActions()
                .flatMap(TestStepAction::readCollectedAssertions);
    }

    /**
     * Used in methodDependencies.vm
     * @deprecated Use {@link #readRelatedMethodContexts()} instead
     */
    public List<MethodContext> getRelatedMethodContexts() {
        return this.relatedMethodContexts;
    }

    public Stream<MethodContext> readRelatedMethodContexts() {
        return this.relatedMethodContexts.stream();
    }

    /**
     * Used in methodDependencies.vm
     * @deprecated Use {@link #readDependsOnMethodContexts()} instead
     */
    public List<MethodContext> getDependsOnMethodContexts() {
        return this.dependsOnMethodContexts;
    }

    public Stream<MethodContext> readDependsOnMethodContexts() {
        return this.dependsOnMethodContexts.stream();
    }

    public void setRelatedMethodContexts(List<MethodContext> relatedMethodContexts) {
        this.relatedMethodContexts = relatedMethodContexts;
    }

    public void addDependsOnMethod(MethodContext methodContext) {
        this.dependsOnMethodContexts.add(methodContext);
    }

    private Stream<TestStepAction> readTestStepActions() {
        return this.readTestSteps().flatMap(testStep -> testStep.getTestStepActions().stream());
    }

    private Stream<Screenshot> readScreenshots() {
        return this.readTestStepActions()
                .flatMap(testStepAction -> testStepAction.readEntries(Screenshot.class));
    }

    /**
     * @deprecated Use {@link #readTestSteps()} instead
     * Use in methodsDashboard.vm
     */
    public Collection<Screenshot> getScreenshots() {
        return readScreenshots().collect(Collectors.toList());
    }

    public TestStepAction addLogMessage(LogMessage logMessage) {
        return testStepController.addLogMessage(logMessage);
    }

    /**
     * @deprecated Use {@link #readTestSteps()} instead
     */
    public List<TestStep> getTestSteps() {
        return this.testStepController.getTestSteps();
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

    /**
     * Used in methodDetails.vm
     */
    public TestStep getLastFailedStep() {
        return this.lastFailedStep;
    }

    public int getLastFailedTestStepIndex() {
        return this.testStepController.getTestSteps().indexOf(this.lastFailedStep);
    }

    public void setFailedStep(TestStep step) {
        this.lastFailedStep = step;
    }

    public boolean hasErrorContext() {
        return this.errorContext != null && this.errorContext.getThrowable() != null;
    }

    public ErrorContext getErrorContext() {
        if (errorContext == null) {
            errorContext = new ErrorContext();
        }
        return errorContext;
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

    @Override
    public int hashCode() {
        return hashCodeOfTestResult;
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
        this.numOptionalAssertions++;
    }

    public void addCollectedAssertion(Throwable throwable) {
        getCurrentTestStep().getCurrentTestStepAction().addAssertion(new ErrorContext(throwable, false));
        this.numAssertions++;
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
        final String extraMessageToAdd = StringUtils.prepareStringForHTML(msg + "\n");

        if (priorityMessage == null) {
            priorityMessage = "";
        }

        if (!priorityMessage.contains(extraMessageToAdd)) {
            priorityMessage += extraMessageToAdd;
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

    /**
     * @todo What is this?
     */
    public boolean isSame(MethodContext methodContext) {
        return methodContext.hashCodeOfTestResult == hashCodeOfTestResult;
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
        TestStepAction currentTestStepAction = this.testStepController.getCurrentTestStep().getCurrentTestStepAction();
        screenshots.forEach(currentTestStepAction::addScreenshot);
    }

    /**
     * @deprecated Use {@link #readSessionContexts()} instead
     */
    public Collection<Video> getVideos() {
        return this.readVideos().collect(Collectors.toList());
    }

    /**
     * @deprecated Use {@link #readSessionContexts()} instead
     */
    public Stream<Video> readVideos() {
        return this.readSessionContexts().map(SessionContext::getVideo).filter(Optional::isPresent).map(Optional::get);
    }

    /**
     * Proper parameter names are available by setting {https://stackoverflow.com/questions/6759880/getting-the-name-of-a-method-parameter}
     */
    public Parameter[] getParameters() {
        return iTestNgMethod.getConstructorOrMethod().getMethod().getParameters();
    }

    public MethodContext setParameterValues(Object[] parameters) {
        this.parameterValues = Arrays.asList(parameters);
        return this;
    }

    public List<Object> getParameterValues() {
        if (this.parameterValues == null) {
            return Collections.emptyList();
        } else {
            return this.parameterValues;
        }
    }

    public ITestResult getTestNgResult() {
        return testResult;
    }

    public MethodContext setTestNgResult(ITestResult testResult) {
        this.testResult = testResult;
        return this;
    }

    public ITestContext getTestNgContext() {
        return iTestContext;
    }

    public MethodContext setTestNgContext(ITestContext iTestContext) {
        this.iTestContext = iTestContext;
        return this;
    }

    public ITestNGMethod getTestNgMethod() {
        return iTestNgMethod;
    }

    public MethodContext setTestNgMethod(ITestNGMethod iTestNgMethod) {
        this.iTestNgMethod = iTestNgMethod;
        return this;
    }

    public Stream<Annotation> readAnnotations() {
        return Stream.of(this.iTestNgMethod.getConstructorOrMethod().getMethod().getAnnotations());
    }
}
