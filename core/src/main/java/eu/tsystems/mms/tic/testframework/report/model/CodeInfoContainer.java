/*
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
package eu.tsystems.mms.tic.testframework.report.model;

import org.testng.ITestClass;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class CodeInfoContainer {

    private CodeInfoContainer() {
    }

    private static final Map<String, List<String>> UNDESCRIPTED_ASSERT_CALLERS_BY_METHOD = Collections.synchronizedMap(
            new HashMap<String, List<String>>());
    private static final List<String> NO_ASSERTS_BY_METHOD = Collections.synchronizedList(new ArrayList<String>());

    /**
     *
     *
     * @param iTestResult .
     * @param undescriptedAssertCallers .
     */
    public static void storeUndescriptedAssertCallersForMethod(ITestResult iTestResult, List<String> undescriptedAssertCallers) {
        ITestNGMethod method = iTestResult.getMethod();
        String methodName = method.getTestClass().getName() + "#" + method.getMethodName();
        List<String> copy = new ArrayList<String>(undescriptedAssertCallers.size());
        copy.addAll(undescriptedAssertCallers);
        UNDESCRIPTED_ASSERT_CALLERS_BY_METHOD.put(methodName, copy);
    }

    /**
     *
     * @param testResult .
     */
    public static void storeNoAssertMethod(ITestResult testResult) {
        if (testResult.getStatus() == ITestResult.CREATED || testResult.getStatus() == ITestResult.SKIP) {
            return;
        }
        ITestNGMethod testMethod = testResult.getMethod();
        ITestClass testClass = testMethod.getTestClass();

        String methodName = testClass.getName() + "#" + testMethod.getMethodName();
        NO_ASSERTS_BY_METHOD.add(methodName);
    }

    /*
    Getters.
     */

    /**
     *
     * @param methodName .
     * @return .
     */
    public static boolean hasMethodNoAsserts(String methodName) {
        return NO_ASSERTS_BY_METHOD.contains(methodName);
    }

    /**
     *
     *
     * @param methodName .
     * @return .
     */
    public static List<String> getUndescriptedAssertCallersForMethod(String methodName) {
        if (!UNDESCRIPTED_ASSERT_CALLERS_BY_METHOD.containsKey(methodName)) {
            return new ArrayList<String>(0);
        }
        return UNDESCRIPTED_ASSERT_CALLERS_BY_METHOD.get(methodName);
    }

    public static Map<String, List<String>> getUndescriptedAssertCallersByMethod() {
        return UNDESCRIPTED_ASSERT_CALLERS_BY_METHOD;
    }

    public static List<String> getNoAssertsByMethod() {
        return NO_ASSERTS_BY_METHOD;
    }
}
