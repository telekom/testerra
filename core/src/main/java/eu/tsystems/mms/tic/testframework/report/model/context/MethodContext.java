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
/*
 * Created on 25.01.2011
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.report.model.context;

import com.google.common.collect.Lists;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.internal.Counters;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.AssertionInfo;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.MethodType;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepController;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds the informations of an test method.
 *
 * @author mibu
 */
public class MethodContext extends AbstractContext implements SynchronizableContext {

    public ITestResult testResult;
    public ITestContext iTestContext;
    public ITestNGMethod iTestNgMethod;

    public TestStatusController.Status status = TestStatusController.Status.NO_RUN;
    public final MethodType methodType;
    public List<Object> parameters = new LinkedList<>();
    public List<Annotation> methodTags = new LinkedList<>();
    public int retryNumber = 0;
    public int methodRunIndex = -1;
    public String threadName = "unrelated";
    private TestStep lastFailedStep;
    public FailureCorridor.Value failureCorridorValue = FailureCorridor.Value.HIGH;

    public ClassContext classContext;
    public TestContextModel testContextModel;
    public SuiteContext suiteContext;
    public final ExecutionContext executionContext;

    private int hashCodeOfTestResult = 0;

    public final List<AssertionInfo> nonFunctionalInfos = new LinkedList<>();
    public final List<AssertionInfo> collectedAssertions = new LinkedList<>();
    public final List<String> infos = new LinkedList<>();

    public final List<SessionContext> sessionContexts = new LinkedList<>();
    public String priorityMessage = null;
    private final TestStepController testStepController = new TestStepController();
    public List<MethodContext> relatedMethodContexts;
    public List<MethodContext> dependsOnMethodContexts;

    /**
     * @deprecated This should not be public
     */
    @Deprecated
    public final List<Video> videos = new LinkedList<>();
    /**
     * @deprecated This should not be public
     */
    @Deprecated
    public final List<Screenshot> screenshots = new LinkedList<>();
    public final List<CustomContext> customContexts = new LinkedList<>();

    private ErrorContext errorContext;

    /**
     * Public constructor. Creates a new <code>MethodContext</code> object.
     *  @param name        The test method name.
     * @param methodType  method type.
     * @param classContext .
     * @param suiteContext .
     * @param testContextModel .
     * @param executionContext .
     */
    public MethodContext(
        final String name,
        final MethodType methodType,
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

    public TestStepAction addLogMessage(LogMessage logMessage) {
        return testStepController.addLogMessage(logMessage);
    }

    public TestStepController steps() {
        return testStepController;
    }

    public TestStep getLastFailedStep() {
        return this.lastFailedStep;
    }

    public MethodContext setFailedStep(TestStep step) {
        this.lastFailedStep = step;
        return this;
    }

    public ErrorContext errorContext() {
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

    /**
     * Add non functional infos.
     *
     * @param throwable         .
     */
    public AssertionInfo addNonFunctionalInfo(final Throwable throwable) {
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
        final String extraMessageToAdd = StringUtils.prepareStringForHTML(msg) + "\n";

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
        return methodType == MethodType.CONFIGURATION_METHOD;
    }

    public boolean isTestMethod() {
        return !isConfigMethod();
    }

    public boolean hasNonFunctionalErrors() {
        return !nonFunctionalInfos.isEmpty();
    }

    public boolean isExpectedFailed() {
        return status == TestStatusController.Status.FAILED_EXPECTED;
    }

    public List<MethodContext> getRelatedMethodContexts() {
        return getMethodContexts(relatedMethodContexts);
    }

    private List<MethodContext> getMethodContexts(List<MethodContext> relatedMethodContexts) {
        if (relatedMethodContexts == null) {
            return null;
        }
        List<MethodContext> copy = new LinkedList<>(relatedMethodContexts);
        Collections.reverse(copy);
        return copy;
    }

    public List<MethodContext> getDependsOnMethodContexts() {
        return getMethodContexts(dependsOnMethodContexts);
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
                throw new TesterraSystemException("Method state not implemented: " + status);
        }
    }

    @Override
    public String toString() {
        return "MethodContext{" +
                "methodRunIndex=" + methodRunIndex +
                ", name='" + name + '\'' +
                '}';
    }

    public ClassContext getEffectiveClassContext() {
        if (classContext.merged) {
            return classContext.mergedIntoClassContext;
        }
        else {
            return classContext;
        }
    }

    /**
     * Publish the screenshots to the report into the current errorContext.
     */
    public void addScreenshots(List<Screenshot> screenshots) {
        TestStepAction currentTestStepAction = steps().getCurrentTestStep().getCurrentTestStepAction();
        screenshots.stream().filter(s -> !s.hasErrorContext()).forEach(screenshot -> {
            screenshot.setErrorContextId(this.id);
            currentTestStepAction.addScreenshots(null, screenshot);
        });
    }
}
