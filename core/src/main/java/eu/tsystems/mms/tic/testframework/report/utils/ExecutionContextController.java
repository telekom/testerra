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
 package eu.tsystems.mms.tic.testframework.report.utils;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.ITestStatusController;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.StatusCounter;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SuiteContext;
import eu.tsystems.mms.tic.testframework.report.model.context.TestContext;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @deprecated Use {@link IExecutionContextController} instead
 */
public class ExecutionContextController {

    private static ExecutionContext EXECUTION_CONTEXT;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionContextController.class);

    private static final ThreadLocal<MethodContext> CURRENT_METHOD_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<ITestResult> CURRENT_TEST_RESULT = new ThreadLocal<>();
    private static final ThreadLocal<SessionContext> CURRENT_SESSION_CONTEXT = new ThreadLocal<>();
    private static final String statsPrefix = "*** Stats: ";

    /**
     * @return The current method context or NULL if there was no method initialized.
     * @deprecated Use {@link IExecutionContextController#getCurrentMethodContext()} instead
     */
    public static MethodContext getCurrentMethodContext() {
        return CURRENT_METHOD_CONTEXT.get();
    }

    /**
     * Returns the method context of the current thread
     */
    public static Optional<MethodContext> getMethodContextForThread() {
        return Optional.ofNullable(CURRENT_METHOD_CONTEXT.get());
    }

    /**
     * @deprecated Use {@link #getTestResultForThread()} instead
     */
    public static ITestResult getCurrentTestResult() {
        return getTestResultForThread().orElse(null);
    }

    /**
     * Returns the test result of the current thread
     */
    public static Optional<ITestResult> getTestResultForThread() {
        return Optional.ofNullable(CURRENT_TEST_RESULT.get());
    }

    /**
     * @deprecated Use {@link IExecutionContextController#getExecutionContext()} instead
     */
    public static synchronized ExecutionContext getCurrentExecutionContext() {
        if (EXECUTION_CONTEXT == null) {
            EXECUTION_CONTEXT = new ExecutionContext();
        }
        return EXECUTION_CONTEXT;
    }

    /**
     * Gets the ClassContext for TestNG ITestResult. If no ClassContext for result exists, it will set.
     *
     * @param testResult The ITestResult to set.
     * @return the ClassContext for the result.
     */
    public static ClassContext getClassContextFromTestResult(ITestResult testResult) {
        SuiteContext suiteContext = getCurrentExecutionContext().getSuiteContext(testResult);
        TestContext testContext = suiteContext.getTestContext(testResult);
        ClassContext classContext = testContext.getClassContext(testResult);
        return classContext;
    }

    /**
     * Used in platform-connector only
     * May be @deprecated
     */
    public static ClassContext getClassContextFromTestContextAndMethod(ITestContext testNGContext, ITestNGMethod testNGMethod) {
        SuiteContext suiteContext = getCurrentExecutionContext().getSuiteContext(testNGContext);
        TestContext testContext = suiteContext.getTestContext(testNGContext);
        ClassContext classContext = testContext.getClassContext(testNGMethod);
        return classContext;
    }

    /**
     * Gets the MethodContext for TestNG ITestResult. If no MethodContext for test result exists, it will
     * created.
     *
     * @param iTestResult The ITestResult to set.
     * @return the MethodContext for the result.
     */
    public static MethodContext getMethodContextFromTestResult(ITestResult iTestResult) {
        //final Object[] parameters = iTestResult.getParameters();
        final ClassContext classContext = getClassContextFromTestResult(iTestResult);
        return classContext.getMethodContext(iTestResult);
    }

    /**
     * Used by ReTestFilterInterceptor on platform-connector
     */
    public static MethodContext getMethodContextFromTestContextAndMethod(ITestContext testContext, ITestNGMethod testNGMethod, Object[] parameters) {
        ClassContext classContext = getClassContextFromTestContextAndMethod(testContext, testNGMethod);
        return classContext.getMethodContext(testContext, testNGMethod, parameters);
    }

    /**
     * Set currently active test.
     *
     * @param iTestResult TestNg testResult representing current test.
     */
    public static MethodContext setCurrentTestResult(ITestResult iTestResult) {
        CURRENT_TEST_RESULT.set(iTestResult);
        MethodContext methodContext = getMethodContextFromTestResult(iTestResult);
        CURRENT_METHOD_CONTEXT.set(methodContext);
        return methodContext;
    }

    public static void setCurrentSessionContext(final SessionContext sessionContext) {
        CURRENT_SESSION_CONTEXT.set(sessionContext);
    }

    /**
     * Returns the current active session context, set by WebdriverProxy
     */
    public static Optional<SessionContext> getSessionContextForThread() {
        return Optional.ofNullable(CURRENT_SESSION_CONTEXT.get());
    }

    /**
     * @deprecated Use {@link #getSessionContextForThread()} instead
     */
    public static SessionContext getCurrentSessionContext() {
        return getSessionContextForThread().orElse(null);
    }

    public static void clearCurrentSessionContext() {
        CURRENT_SESSION_CONTEXT.remove();
    }

    /**
     * Clear the -current testresult-. Use with care!
     */
    public static void clearCurrentTestResult() {
        CURRENT_TEST_RESULT.remove();
        CURRENT_METHOD_CONTEXT.remove();
    }

    public static void printExecutionStatistics() {
        final ExecutionContext executionContext = getCurrentExecutionContext();

        LOGGER.info(statsPrefix + "**********************************************");

        LOGGER.info(statsPrefix + "ExecutionContext: " + executionContext.getName());
        AtomicInteger suiteContextCount = new AtomicInteger();
        AtomicInteger classContextCount = new AtomicInteger();
        AtomicInteger testContextCount = new AtomicInteger();
        AtomicInteger methodContextCount = new AtomicInteger();
        AtomicInteger testMethodContextCount = new AtomicInteger();
        AtomicInteger relevantMethodContextCount = new AtomicInteger();

        executionContext.readSuiteContexts().forEach(suiteContext -> {
            suiteContextCount.incrementAndGet();
            suiteContext.readTestContexts().forEach(testContext -> {
                testContextCount.incrementAndGet();
                testContext.readClassContexts().forEach(classContext -> {
                    classContextCount.incrementAndGet();
                    classContext.readMethodContexts().forEach(methodContext -> {
                        methodContextCount.incrementAndGet();

                        if (methodContext.isTestMethod()) {
                            testMethodContextCount.incrementAndGet();

                            if (methodContext.getStatus().isStatisticallyRelevant()) {
                                relevantMethodContextCount.incrementAndGet();
                            }
                        }
                    });
                });
            });
        });
        LOGGER.info(statsPrefix + "SuiteContexts:  " + suiteContextCount.get());
        LOGGER.info(statsPrefix + "TestContexts:   " + testContextCount.get());
        LOGGER.info(statsPrefix + "ClassContexts:  " + classContextCount.get());
        LOGGER.info(statsPrefix + "MethodContexts: " + methodContextCount.get());
        LOGGER.info(statsPrefix + "**********************************************");
        LOGGER.info(statsPrefix + "Test Methods Count: " + testMethodContextCount.get() + " (" + relevantMethodContextCount.get() + " relevant)");

        ITestStatusController testStatusController = TesterraListener.getTestStatusController();
        StatusCounter statusCounter = testStatusController.getStatusCounter();

        logStatusSet(Stream.of(Status.FAILED), statusCounter);
        logStatusSet(Stream.of(Status.RETRIED), statusCounter);
        logStatusSet(Stream.of(Status.FAILED_EXPECTED), statusCounter);
        logStatusSet(Stream.of(Status.SKIPPED), statusCounter);
        logStatusSet(Stream.of(Status.PASSED, Status.RECOVERED, Status.REPAIRED), statusCounter);

        LOGGER.info(statsPrefix + "**********************************************");
        Status overallStatus = Status.FAILED;
        if (Testerra.Properties.FAILURE_CORRIDOR_ACTIVE.asBool()) {
            if (FailureCorridor.isCorridorMatched()) {
                overallStatus = Status.PASSED;
            }
        } else {
            if (testStatusController.getTestsFailed() == 0) {
                overallStatus = Status.PASSED;
            }
        }

        LOGGER.info(statsPrefix + "ExecutionContext Status: " + overallStatus.title);
        LOGGER.info(statsPrefix + "FailureCorridor Enabled: " + Testerra.Properties.FAILURE_CORRIDOR_ACTIVE.asBool());

        if (Testerra.Properties.FAILURE_CORRIDOR_ACTIVE.asBool()) {
            LOGGER.info(statsPrefix + "**********************************************");
            LOGGER.info(statsPrefix + "FailureCorridor Status : " + FailureCorridor.getStatistics() + " " + FailureCorridor.getStatusMessage());
        }

        LOGGER.info(statsPrefix + "**********************************************");

        LOGGER.info(statsPrefix + "Duration: " + executionContext.getDurationAsString());

        LOGGER.info(statsPrefix + "**********************************************");
    }

    private static void logStatusSet(Stream<Status> statuses, StatusCounter statusCounter) {
        String statusSetString = createStatusSetString(statuses, statusCounter);
        if (statusSetString.length() > 0) {
            LOGGER.info(statsPrefix + statusSetString);
        }
    }

    private static String createStatusSetString(Stream<Status> statuses, StatusCounter statusCounter) {
        return statuses
                .map(status -> {
                    int summarizedTestStatusCount = statusCounter.getSum(Status.getStatusGroup(status));
                    if (summarizedTestStatusCount > 0) {
                        return status.title + ": " + summarizedTestStatusCount;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" ⊃ "));
    }
}
