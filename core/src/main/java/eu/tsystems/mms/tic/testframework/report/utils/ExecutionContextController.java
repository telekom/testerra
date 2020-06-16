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
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SuiteContext;
import eu.tsystems.mms.tic.testframework.report.model.context.TestContextModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.util.List;
import java.util.stream.Collectors;

public class ExecutionContextController {

    private static ExecutionContext EXECUTION_CONTEXT;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionContextController.class);

    private static final ThreadLocal<MethodContext> CURRENT_METHOD_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<ITestResult> CURRENT_TEST_RESULT = new ThreadLocal<>();

    private static final ThreadLocal<SessionContext> CURRENT_SESSION_CONTEXT = new ThreadLocal<>();

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

    public static boolean testRunFinished = false;

    /**
     * Gets the ClassContext for TestNG ITestResult. If no ClassContext for result exists, it will set.
     *
     * @param testResult The ITestResult to set.
     * @return the ClassContext for the result.
     */
    public static ClassContext getClassContextFromTestResult(ITestResult testResult, ITestContext iTestContext, IInvokedMethod invokedMethod) {
        SuiteContext suiteContext = getCurrentExecutionContext().getSuiteContext(testResult, iTestContext);
        TestContextModel testContextModel = suiteContext.getTestContext(testResult, iTestContext);
        ClassContext classContext = testContextModel.getClassContext(testResult, iTestContext, invokedMethod);
        return classContext;
    }

    public static ClassContext getClassContextFromTestContextAndMethod(final ITestContext iTestContext, final ITestNGMethod iTestNgMethod) {
        SuiteContext suiteContext = getCurrentExecutionContext().getSuiteContext(iTestContext);
        TestContextModel testContextModel = suiteContext.getTestContext(iTestContext);
        ClassContext classContext = testContextModel.getClassContext(iTestNgMethod);
        return classContext;
    }

    /**
     * Gets the MethodContext for TestNG ITestResult. If no MethodContext for test result exists, it will
     * created.
     *
     * @param iTestResult The ITestResult to set.
     * @return the MethodContext for the result.
     */
    public static MethodContext getMethodContextFromTestResult(final ITestResult iTestResult, final ITestContext testContext) {
        final Object[] parameters = iTestResult.getParameters();
        final ClassContext classContext = getClassContextFromTestResult(iTestResult, testContext, null);
        return classContext.getMethodContext(iTestResult, testContext, null);
    }

    public static MethodContext getMethodContextFromTestContextAndMethod(final ITestContext iTestContext, final ITestNGMethod iTestNgMethod, final Object[] parameters) {
        ClassContext classContext = getClassContextFromTestContextAndMethod(iTestContext, iTestNgMethod);
        return classContext.getMethodContext(null, iTestContext, iTestNgMethod, parameters);
    }

    /**
     * Set currently active test.
     *
     * @param iTestResult TestNg testResult representing current test.
     */
    public static MethodContext setCurrentTestResult(final ITestResult iTestResult, final ITestContext testContext) {
        CURRENT_TEST_RESULT.set(iTestResult);
        /*
        auto-create method context
         */
        return getMethodContextFromTestResult(iTestResult, testContext);
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
        final String prefix = "*** Stats: ";

        LOGGER.info(prefix + "**********************************************");

        LOGGER.info(prefix + "ExecutionContext: " + executionContext.name);
        LOGGER.info(prefix + "SuiteContexts:  " + executionContext.suiteContexts.size());
        LOGGER.info(prefix + "TestContexts:   " + executionContext.suiteContexts.stream().mapToInt(s -> s.testContextModels.size()).sum());
        LOGGER.info(prefix + "ClassContexts:  " + executionContext.suiteContexts.stream().flatMap(s -> s.testContextModels.stream()).mapToInt(t -> t.classContexts.size()).sum());

        List<MethodContext> allMethodContexts = executionContext.suiteContexts.stream().flatMap(s -> s.testContextModels.stream()).flatMap(t -> t.classContexts.stream()).flatMap(c -> c.methodContexts.stream()).collect(Collectors.toList());

        LOGGER.info(prefix + "MethodContexts: " + allMethodContexts.size());

        LOGGER.info(prefix + "**********************************************");

        List<MethodContext> allTestMethods = allMethodContexts.stream().filter(MethodContext::isTestMethod).collect(Collectors.toList());

        LOGGER.info(prefix + "Test Methods Count: " + allTestMethods.size() + " (" + allTestMethods.stream().filter(m -> m.status.relevant).count() + " relevant)");

        for (TestStatusController.Status status : TestStatusController.Status.values()) {
            LOGGER.info(prefix + status.name() + ": " + allTestMethods.stream().filter(m -> m.status == status).count());
        }

        LOGGER.info(prefix + "**********************************************");

        LOGGER.info(prefix + "ExecutionContext Status: " + executionContext.getStatus());
        LOGGER.info(prefix + "FailureCorridor Enabled: " + Flags.FAILURE_CORRIDOR_ACTIVE);

        if (Flags.FAILURE_CORRIDOR_ACTIVE) {
            LOGGER.info(prefix + "**********************************************");
            LOGGER.info(prefix + "FailureCorridor Status : " + FailureCorridor.getStatistics() + " " + FailureCorridor.getStatusMessage());
        }

        LOGGER.info(prefix + "**********************************************");

        LOGGER.info(prefix + "Duration: " + executionContext.getDuration(executionContext.startTime, executionContext.endTime));

        LOGGER.info(prefix + "**********************************************");
    }

    public static void setEstimatedTestMethodCount(final int estimatedTestMethodCount) {

        getCurrentExecutionContext().estimatedTestMethodCount = estimatedTestMethodCount;
    }

}
