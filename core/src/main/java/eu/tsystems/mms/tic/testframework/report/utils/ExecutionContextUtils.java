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

public class ExecutionContextUtils {

    /**
     * gets the current method name from test results
     *
     * @return method name or nukk
     */
    public static String getMethodNameFromCurrentTestResult() {
        return getMethodNameFromCurrentTestResult(false);
    }

    public static String getMethodNameFromCurrentTestResult(boolean withClassName) {
        return ExecutionContextController.getTestResultForThread().map(testResult -> {
            String methodName = "";
            if (withClassName) {
                methodName += testResult.getTestClass().getRealClass().getSimpleName() + ".";
            }
            methodName += testResult.getMethod().getMethodName();
            return methodName;
        }).orElse("");
    }
}
