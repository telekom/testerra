/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.general;

import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;

public enum MethodDependency {

    FAILED_ALWAYS("test_FailedAlways", TestResultHelper.TestResult.FAILED, ""),
    FAILED_DEPENDSON_PASSED("test_FailedDependsOnPassedMethod", TestResultHelper.TestResult.FAILED, ""),
    FAILED_DEPENDSON_FAILED_BUT_ALWAYS_RUN_WITH_DATAPROVIDER_RUN2("test_dependsOnFailedMethodButAlwaysRunWithDP", TestResultHelper.TestResult.FAILED, "(1) Run2"),
    FAILED_DEPENDSON_FAILED_BUT_ALWAYS_RUN_WITH_DATAPROVIDER_RUN3("test_dependsOnFailedMethodButAlwaysRunWithDP", TestResultHelper.TestResult.FAILED, "(1) Run3"),
    PASSED_ALWAYS("test_PassedAlways", TestResultHelper.TestResult.PASSED, ""),
    PASSED_DEPENDSON_FAILED_BUT_ALWAYS_RUN("test_PassedDependsOnFailedMethodButAlwaysRun", TestResultHelper.TestResult.PASSED, ""),
    PASSED_DEPENDSON_FAILED_BUT_ALWAYS_RUN_WITH_DATAPROVIDER_RUN1("test_dependsOnFailedMethodButAlwaysRunWithDP", TestResultHelper.TestResult.PASSED, "(1) Run1"),
    SKIPPED_DEPENDSON_FAILED_WITH_DATAPROVIDER("test_dependsOnFailedMethodWithDP", TestResultHelper.TestResult.SKIPPED, ""),
    SKIPPED_DEPENDSON_FAILED("test_SkippedDependsOnFailedMethod", TestResultHelper.TestResult.SKIPPED, ""),
    SKIPPED_DEPENDSON_FAILED_WITH_SECOND_DATAPROVIDER("test_dependsOnFailedMethodWithDP2", TestResultHelper.TestResult.SKIPPED, "");

    private String methodName;
    private TestResultHelper.TestResult testResult;
    private String tagName;

    MethodDependency(String methodName, TestResultHelper.TestResult testResult, String tagName) {
        this.methodName = methodName;
        this.testResult = testResult;
        this.tagName = tagName;
    }

    public String getMethodName() {
        return methodName;
    }

    public TestResultHelper.TestResult getTestResult() {
        return testResult;
    }

    public String getTagName() {
        return tagName;
    }


}
