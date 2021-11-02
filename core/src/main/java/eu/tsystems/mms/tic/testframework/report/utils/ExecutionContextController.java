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

import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.StatusCounter;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SuiteContext;
import eu.tsystems.mms.tic.testframework.report.model.context.TestContext;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class ExecutionContextController {

    private static ExecutionContext EXECUTION_CONTEXT;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionContextController.class);

    private static final ThreadLocal<MethodContext> CURRENT_METHOD_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<ITestResult> CURRENT_TEST_RESULT = new ThreadLocal<>();

    private static final ThreadLocal<SessionContext> CURRENT_SESSION_CONTEXT = new ThreadLocal<>();
    private static final String prefix = "*** Stats: ";

    /**
     * //TODO Make {@link Optional}
     * @return The current method context or NULL if there was no method initialized.
     */
    public static MethodContext getCurrentMethodContext() {
        return CURRENT_METHOD_CONTEXT.get();
    }

    public static ITestResult getCurrentTestResult() {
        return CURRENT_TEST_RESULT.get();
    }

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
        return getMethodContextFromTestResult(iTestResult);
    }

    /**
     * Set currently active test.
     *
     * @param methodContext Method Context.
     */
    public static void setCurrentMethodContext(final MethodContext methodContext) {
        CURRENT_METHOD_CONTEXT.set(methodContext);
    }

    public static void setCurrentSessionContext(final SessionContext sessionContext) {
        CURRENT_SESSION_CONTEXT.set(sessionContext);
    }

    public static SessionContext getCurrentSessionContext() {
        return CURRENT_SESSION_CONTEXT.get();
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

        LOGGER.info(prefix + "**********************************************");

        LOGGER.info(prefix + "ExecutionContext: " + executionContext.getName());
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
        LOGGER.info(prefix + "SuiteContexts:  " + suiteContextCount.get());
        LOGGER.info(prefix + "TestContexts:   " + testContextCount.get());
        LOGGER.info(prefix + "ClassContexts:  " + classContextCount.get());
        LOGGER.info(prefix + "MethodContexts: " + methodContextCount.get());
        LOGGER.info(prefix + "**********************************************");
        LOGGER.info(prefix + "Test Methods Count: " + testMethodContextCount.get() + " (" + relevantMethodContextCount.get() + " relevant)");

        TestStatusController testStatusController = TesterraListener.getTestStatusController();
        StatusCounter statusCounter = testStatusController.getStatusCounter();

        logStatusSet(Stream.of(Status.FAILED, Status.RETRIED), statusCounter);
        logStatusSet(Stream.of(Status.FAILED_EXPECTED), statusCounter);
        logStatusSet(Stream.of(Status.SKIPPED), statusCounter);
        logStatusSet(Stream.of(Status.PASSED, Status.RECOVERED, Status.REPAIRED), statusCounter);

        LOGGER.info(prefix + "**********************************************");
        Status overallStatus = Status.FAILED;
        if (Flags.FAILURE_CORRIDOR_ACTIVE) {
            if (FailureCorridor.isCorridorMatched()) {
                overallStatus = Status.PASSED;
            }
        } else {
            if (testStatusController.getTestsFailed() == 0) {
                overallStatus = Status.PASSED;
            }
        }

        LOGGER.info(prefix + "ExecutionContext Status: " + overallStatus.title);
        LOGGER.info(prefix + "FailureCorridor Enabled: " + Flags.FAILURE_CORRIDOR_ACTIVE);

        if (Flags.FAILURE_CORRIDOR_ACTIVE) {
            LOGGER.info(prefix + "**********************************************");
            LOGGER.info(prefix + "FailureCorridor Status : " + FailureCorridor.getStatistics() + " " + FailureCorridor.getStatusMessage());
        }

        LOGGER.info(prefix + "**********************************************");

        LOGGER.info(prefix + "Duration: " + executionContext.getDurationAsString());

        LOGGER.info(prefix + "**********************************************");
    }

    private static void logStatusSet(Stream<Status> statuses, StatusCounter statusCounter) {
        String statusSetString = createStatusSetString(statuses, statusCounter);
        if (statusSetString.length() > 0) {
            LOGGER.info(prefix + statusSetString);
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
