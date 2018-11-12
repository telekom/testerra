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
/*
 * Created on 25.01.2011
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.internal.Counters;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.AssertionInfo;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.MethodType;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepController;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
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
public class MethodContext extends ErrorContext implements SynchronizableContext {

    public ITestResult testResult;
    public TestStatusController.Status status = TestStatusController.Status.NO_RUN;
    public final MethodType methodType;
    public List<Object> parameters = new LinkedList<>();
    public List<Annotation> methodTags = new LinkedList<>();
    public int retryNumber = 0;
    public int methodRunIndex = -1;
    public String threadName;
    public TestStep failedStep;
    public FailureCorridor.Value failureCorridorValue = FailureCorridor.Value.High;

    public ClassContext classContext;
    public TestContext testContext;
    public SuiteContext suiteContext;
    public final RunContext runContext;

    private int hashCodeOfTestResult = 0;

    public List<AssertionInfo> nonFunctionalInfos = new LinkedList<>();
    public List<AssertionInfo> collectedAssertions = new LinkedList<>();
    public List<String> infos = new LinkedList<>();

    /**
     * Flag for putting all information into the report. Defaults to true. You can set this to false, may be for passed
     * configuration methods.
     */
    public boolean intoReport = true;

    public String priorityMessage = null;

    public TestStepController testStepController = new TestStepController();

    /**
     * Stores additional Info for test method
     * Like Dataprovider.. or suitename ...
     */
    public List<String> dashboardInfos = new LinkedList<>();

    public List<MethodContext> relatedMethodContexts;
    public List<MethodContext> dependsOnMethodContexts;

    /**
     * Public constructor. Creates a new <code>MethodContext</code> object.
     *  @param name        The test method name.
     * @param methodType  method type.
     * @param classContext .
     * @param suiteContext .
     * @param testContext .
     * @param runContext .
     */
    public MethodContext(final String name, final MethodType methodType, final ClassContext classContext, final TestContext testContext, final SuiteContext suiteContext, final RunContext runContext) {
        this.testContext = testContext;
        this.suiteContext = suiteContext;
        this.runContext = runContext;

        this.name = name;
        this.parentContext = this.classContext = classContext;
        this.methodRunIndex = Counters.increaseMethodExecutionCounter();
        this.methodType = methodType;
    }

    public void addLogMessage(LogMessage logMessage) {
        testStepController.addLogMessage(logMessage);
    }

    public TestStepController steps() {
        return testStepController;
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
     * Gets whether the test method execution was exception.
     *
     * @return The value of <code>exception</code>.
     */
    @Deprecated
    public boolean isException() {
        return status == TestStatusController.Status.FAILED;
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
        if (nonFunctionalInfos == null) {
            nonFunctionalInfos = new LinkedList<>();
        }

        AssertionInfo assertionInfo = new AssertionInfo(throwable);

        this.nonFunctionalInfos.add(assertionInfo);

        return assertionInfo;
    }

    public void addCollectedAssertions(final List<AssertionInfo> collectedAssertions) {
        if (this.collectedAssertions == null) {
            this.collectedAssertions = new LinkedList<>();
        }
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

            case NO_RUN:
            case PASSED:
            case MINOR:
            case SKIPPED:
            default:
                return false;
        }
    }

    public boolean isPassed() {
        switch (status) {
            case FAILED_EXPECTED:
            case FAILED:
            case FAILED_MINOR:
            case FAILED_RETRIED:
            case SKIPPED:
            case NO_RUN:
                return false;

            case PASSED:
            case MINOR:
            default:
                return true;
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
        this.threadName = Thread.currentThread().getName();
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
        if (relatedMethodContexts == null) {
            return null;
        }
        List<MethodContext> copy = new LinkedList<>(relatedMethodContexts);
        Collections.reverse(copy);
        return copy;
    }

    public List<MethodContext> getDependsOnMethodContexts() {
        if (dependsOnMethodContexts == null) {
            return null;
        }
        List<MethodContext> copy = new LinkedList<>(dependsOnMethodContexts);
        Collections.reverse(copy);
        return copy;
    }

    public boolean hasBeenRetried() {
        return status == TestStatusController.Status.FAILED_RETRIED || status == TestStatusController.Status.PASSED_RETRY;
    }

    @Override
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
            case FAILED:
            case FAILED_MINOR:
            case SKIPPED:
                return true;

            default:
                throw new FennecSystemException("Method state not implemented: " + status);
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
}
