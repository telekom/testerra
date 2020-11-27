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
import eu.tsystems.mms.tic.testframework.report.model.AssertionInfo;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepController;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
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

    public ITestResult testResult;
    public ITestContext iTestContext;
    public ITestNGMethod iTestNgMethod;

    public TestStatusController.Status status = TestStatusController.Status.NO_RUN;
    public final Type methodType;
    public List<Object> parameters = new LinkedList<>();
    public List<Annotation> methodTags = new LinkedList<>();
    public int retryNumber = 0;
    public int methodRunIndex = -1;
    public String threadName = "unrelated";
    private TestStep lastFailedStep;
    public FailureCorridor.Value failureCorridorValue = FailureCorridor.Value.HIGH;
    /**
     * @deprecated Use {@link #getClassContext()} instead
     */
    public final ClassContext classContext;
    public TestContextModel testContextModel;
    public SuiteContext suiteContext;
    public final ExecutionContext executionContext;

    private int hashCodeOfTestResult = 0;

    /**
     * @deprecated Use {@link #getOptionalAssertions()} instead
     */
    public final List<AssertionInfo> nonFunctionalInfos = new LinkedList<>();
    /**
     * @deprecated Use {@link #getCollectedAssertions()} instead
     */
    public final List<AssertionInfo> collectedAssertions = new LinkedList<>();
    public final List<String> infos = new LinkedList<>();
    private List<SessionContext> sessionContexts = new LinkedList<>();
    public String priorityMessage = null;
    private final TestStepController testStepController = new TestStepController();
    /**
     * @deprecated Use {@link #getRelatedMethodContexts()} instead
     */
    public List<MethodContext> relatedMethodContexts;

    /**
     * @deprecated Us {@link #getDependsOnMethodContexts()} instead
     */
    public List<MethodContext> dependsOnMethodContexts;

    private List<Video> videos;

    /**
     * @deprecated Use {@link #getAllScreenshotsFromTestSteps()} instead
     */
    public final List<Screenshot> screenshots = new LinkedList<>();

    public final List<CustomContext> customContexts = new LinkedList<>();

    private ErrorContext errorContext;

    /**
     * Public constructor. Creates a new <code>MethodContext</code> object.
     *
     * @param name             The test method name.
     * @param methodType       method type.
     * @param classContext     .
     * @param suiteContext     .
     * @param testContextModel .
     * @param executionContext .
     */
    public MethodContext(
            final String name,
            final Type methodType,
            final ClassContext classContext,
            final TestContextModel testContextModel,
            final SuiteContext suiteContext,
            final ExecutionContext executionContext
    ) {
        this.testContextModel = testContextModel;
        this.suiteContext = suiteContext;
        this.executionContext = executionContext;

        this.name = name;
        this.parentContext = this.classContext = classContext;
        this.methodRunIndex = Counters.increaseMethodExecutionCounter();
        this.methodType = methodType;
    }

    public Stream<SessionContext> getSessionContexts() {
        return sessionContexts.stream();
    }

    public MethodContext addSessionContext(SessionContext sessionContext) {
        if (!this.sessionContexts.contains(sessionContext)) {
            this.sessionContexts.add(sessionContext);
            TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(this));
        }
        return this;
    }

    public ClassContext getClassContext() {
        return classContext;
    }

    public List<AssertionInfo> getOptionalAssertions() {
        return this.nonFunctionalInfos;
    }

    public List<AssertionInfo> getCollectedAssertions() {
        return this.collectedAssertions;
    }

    // Used in methodDependencies.vm
    public List<MethodContext> getRelatedMethodContexts() {
        return this.relatedMethodContexts;
    }

    // Used in methodDependencies.vm
    public List<MethodContext> getDependsOnMethodContexts() {
        return this.dependsOnMethodContexts;
    }

    public MethodContext setRelatedMethodContexts(List<MethodContext> relatedMethodContexts) {
        this.relatedMethodContexts = relatedMethodContexts;
        return this;
    }

    public MethodContext setDependsOnMethodContexts(List<MethodContext> dependsOnMethodContexts) {
        this.dependsOnMethodContexts = dependsOnMethodContexts;
        return this;
    }

    public Stream<Screenshot> getAllScreenshotsFromTestSteps() {
        return this.testStepController.getTestSteps().stream()
                .flatMap(testStep -> testStep.getTestStepActions().stream())
                .flatMap(testStepAction -> testStepAction.getTestStepActionEntries().stream())
                .map(testStepActionEntry -> testStepActionEntry.screenshot)
                .filter(Objects::nonNull);

    }

    public TestStepAction addLogMessage(LogMessage logMessage) {
        return testStepController.addLogMessage(logMessage);
    }

    /**
     * @deprecated Use {@link #getTestStepController()} instead
     */
    public TestStepController steps() {
        return testStepController;
    }

    public TestStepController getTestStepController() {
        return testStepController;
    }

    public List<TestStep> getTestSteps() {
        return this.testStepController.getTestSteps();
    }

    public TestStep getCurrentTestStep() {
        return this.testStepController.getCurrentTestStep();
    }

    public TestStep getLastFailedStep() {
        return this.lastFailedStep;
    }

    public MethodContext setFailedStep(TestStep step) {
        this.lastFailedStep = step;
        return this;
    }

    public boolean hasErrorContext() {
        return this.errorContext != null;
    }

    public ErrorContext getErrorContext() {
        if (errorContext == null) {
            errorContext = new ErrorContext();
        }
        return errorContext;
    }

    /**
     * @deprecated Use {@link #getErrorContext()} instead
     */
    public ErrorContext errorContext() {
       return this.getErrorContext();
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

    /**
     * @deprecated Use {@link #addOptionalAssertion(Throwable)} instead
     */
    public AssertionInfo addNonFunctionalInfo(final Throwable throwable) {
        return this.addOptionalAssertion(throwable);
    }

    public AssertionInfo addOptionalAssertion(Throwable throwable) {
        AssertionInfo assertionInfo = new AssertionInfo(throwable);
        this.nonFunctionalInfos.add(assertionInfo);
        return assertionInfo;
    }

    public void addCollectedAssertions(final List<AssertionInfo> collectedAssertions) {
        this.collectedAssertions.addAll(collectedAssertions);
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

    public String getName() {
        return name;
    }

    public boolean isSame(MethodContext methodContext) {
        return methodContext.hashCodeOfTestResult == hashCodeOfTestResult;
    }

    @Override
    public TestStatusController.Status getStatus() {
        return status;
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
     * @deprecated Use {@link #getClassContext()} instead
     * @return
     */
    @Deprecated
    public ClassContext getEffectiveClassContext() {
        return classContext;
    }

    /**
     * Publish the screenshots to the report into the current errorContext.
     */
    public MethodContext addScreenshots(Stream<Screenshot> screenshots) {
        TestStepAction currentTestStepAction = getTestStepController().getCurrentTestStep().getCurrentTestStepAction();
        screenshots.forEach(screenshot -> {
            if (screenshot.errorContextId == null) {
                screenshot.errorContextId = this.getErrorContext().getId();
            }
            this.screenshots.add(screenshot);
            currentTestStepAction.addScreenshot(screenshot);
        });
        return this;
    }

    public Stream<Video> getVideos() {
        if (this.videos == null) {
            return Stream.empty();
        } else {
            return this.videos.stream();
        }
    }

    public MethodContext addVideos(Collection<Video> videos) {
        if (this.videos == null) {
             this.videos = new LinkedList<>();
        }
        this.videos.addAll(videos);
        return this;
    }
}
