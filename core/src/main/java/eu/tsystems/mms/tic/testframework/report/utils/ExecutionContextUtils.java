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

import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.lang.reflect.Method;

/**
 * Created by piet on 08.12.16.
 */
public class ExecutionContextUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionContextUtils.class);

    /**
     * Checks if MethodContainer for the give result exists.
     *
     * @param testResult result to check.
     */
    public static void checkForInjectedMethod(ITestResult testResult, final ITestContext testContext) {
        boolean foundMethodObject = false;
        Object[] parameters = testResult.getParameters();
        if (parameters != null) {
            for (Object parameter : parameters) {
                if ((parameter != null) && (parameter instanceof Method)) {
                    Method method = (Method) parameter;

                    MethodContext methodContext = ExecutionContextController.getMethodContextFromTestResult(testResult, testContext);
                    String testMethodName = methodContext.name;

                    final String info = "for " + method.getName();
                    if (!testMethodName.contains(info)) {
                        LOGGER.info("Config method -" + testResult.getMethod().getMethodName() + "- called for "
                                + method.getName());
                        methodContext.infos.add(info);
                    }

                    foundMethodObject = true;
                }
            }
        }

        if (!foundMethodObject) {
            final String msg = "Please use @BeforeMethod before(Method method) and @AfterMethod after(Method method)!" +
                    "\nThis will be mandatory in a future release.";

            LOGGER.info(msg);
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
