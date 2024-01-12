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

import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SuiteContext;
import eu.tsystems.mms.tic.testframework.report.model.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.util.Optional;

/**
 * @deprecated Use {@link IExecutionContextController} instead
 */
public class ExecutionContextController {

    private static ExecutionContext EXECUTION_CONTEXT;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionContextController.class);

    private static final ThreadLocal<MethodContext> CURRENT_METHOD_CONTEXT = new ThreadLocal<>();
//    private static final ThreadLocal<ITestResult> CURRENT_TEST_RESULT = new ThreadLocal<>();
    private static final ThreadLocal<SessionContext> CURRENT_SESSION_CONTEXT = new ThreadLocal<>();

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

//    /**
//     * @deprecated Use {@link #getTestResultForThread()} instead
//     */
//    public static ITestResult getCurrentTestResult() {
//        return getTestResultForThread().orElse(null);
//    }

//    /**
//     * Returns the test result of the current thread
//     */
//    public static Optional<ITestResult> getTestResultForThread() {
//        return Optional.ofNullable(CURRENT_TEST_RESULT.get());
//    }

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
    public static MethodContext setCurrentMethodContext(ITestResult iTestResult) {
//        CURRENT_TEST_RESULT.set(iTestResult);
        MethodContext methodContext = getMethodContextFromTestResult(iTestResult);
        CURRENT_METHOD_CONTEXT.set(methodContext);
        return methodContext;
    }

    public static void setCurrentSessionContext(final SessionContext sessionContext) {
        CURRENT_SESSION_CONTEXT.set(sessionContext);
    }

    /**
     * Returns the current active session context
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
//        CURRENT_TEST_RESULT.remove();
        CURRENT_METHOD_CONTEXT.remove();
    }

}
