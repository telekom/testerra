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
package eu.tsystems.mms.tic.testframework.report.utils;

import eu.tsystems.mms.tic.testframework.report.model.context.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestResult;

/**
 * Created by piet on 08.12.16.
 */
public class ExecutionContextController {

    public static final RunContext RUN_CONTEXT = new RunContext();

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionContextController.class);

    private static final ThreadLocal<MethodContext> CURRENT_METHOD_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<ITestResult> CURRENT_TEST_RESULT = new ThreadLocal<>();

    public static MethodContext getCurrentMethodContext() {
        return CURRENT_METHOD_CONTEXT.get();
    }

    public static ITestResult getCurrentTestResult() {
        return CURRENT_TEST_RESULT.get();
    }

    /**
     * Gets the ClassContext for TestNG ITestResult. If no ClassContext for result exists, it will set.
     *
     * @param testResult The ITestResult to set.
     * @return the ClassContext for the result.
     */
    public static ClassContext getClassContextFromTestResult(ITestResult testResult, ITestContext iTestContext, IInvokedMethod invokedMethod) {
        SuiteContext suiteContext = RUN_CONTEXT.getSuiteContext(testResult, iTestContext);
        TestContext testContext = suiteContext.getTestContext(testResult, iTestContext);
        ClassContext classContext = testContext.getClassContext(testResult, iTestContext, invokedMethod);
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
        ClassContext classContext = getClassContextFromTestResult(iTestResult, testContext, null);
        return classContext.getMethodContext(iTestResult, testContext, null);
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

    /**
     * Clear the -current testresult-. Use with care!
     */
    public static void clearCurrentTestResult() {
        CURRENT_TEST_RESULT.remove();
        CURRENT_METHOD_CONTEXT.remove();
    }

}
