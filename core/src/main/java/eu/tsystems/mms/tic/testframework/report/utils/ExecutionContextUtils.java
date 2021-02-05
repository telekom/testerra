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

import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.Optional;

public class ExecutionContextUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionContextUtils.class);

    /**
     * Get an injected test method of an ITestResult.
     * <p>
     * Eg. setup methods (@BeforeMethod) can contain an injected method. For this verification the setup method should have 'Method method' as a parameter.
     *
     * @param testResult
     * @return
     */
    public static Optional<Method> getInjectedMethod(ITestResult testResult) {
        if (testResult != null) {
            Object[] parameters = testResult.getParameters();
            if (parameters != null) {
                for (Object parameter : parameters) {
                    if (parameter != null && parameter instanceof Method) {
                        return Optional.of((Method) parameter);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if MethodContainer for the give result exists.
     *
     * @param testResult result to check.
     */
    public static void checkForInjectedMethod(ITestResult testResult) {
        Optional<Method> optional = getInjectedMethod(testResult);
        if (optional.isPresent()) {
            MethodContext methodContext = ExecutionContextController.getMethodContextFromTestResult(testResult);
            String testMethodName = methodContext.getName();

            final String info = "for " + optional.get().getName();
            if (!testMethodName.contains(info)) {
                methodContext.infos.add(info);
            }
        } else {
            final String msg = "Please use @BeforeMethod before(Method method) and @AfterMethod after(Method method)!" +
                    "\nThis will be mandatory in a future release.";

            LOGGER.warn(msg);
            ReportInfo.getDashboardWarning().addInfo(10, StringUtils.prepareStringForHTML(msg));
        }
    }

    /**
     * gets the current method name from test results
     *
     * @return method name or nukk
     */
    public static String getMethodNameFromCurrentTestResult() {
        return getMethodNameFromCurrentTestResult(false);
    }

    public static String getMethodNameFromCurrentTestResult(boolean withClassName) {
        ITestResult currentTestResult = ExecutionContextController.getCurrentTestResult();
        String methodName = "";
        if (withClassName) {
            methodName += currentTestResult.getTestClass().getRealClass().getSimpleName() + ".";
        }
        methodName += currentTestResult.getMethod().getMethodName();
        return methodName;
    }
}
