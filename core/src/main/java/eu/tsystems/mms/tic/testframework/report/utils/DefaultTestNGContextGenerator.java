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

import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class DefaultTestNGContextGenerator implements TestNGContextNameGenerator {

    @Override
    public String getClassContextName(ITestResult testResult) {
        return testResult.getTestClass().getRealClass().getSimpleName();
    }

    @Override
    public String getSuiteContextName(ITestResult testResult) {
        return testResult.getTestContext().getSuite().getName();
    }

    @Override
    public String getSuiteContextName(ITestContext testContext) {
        return testContext.getSuite().getName();
    }

    @Override
    public String getTestContextName(ITestResult testResult) {
        return testResult.getTestContext().getCurrentXmlTest().getName();
    }

    @Override
    public String getTestContextName(ITestContext testContext) {
        return testContext.getCurrentXmlTest().getName();
    }

    @Override
    public String getMethodContextName(ITestResult testResult) {
        return getMethodContextName(testResult.getTestContext(), testResult.getMethod(), testResult.getParameters());
    }

    @Override
    public String getMethodContextName(ITestContext testContext, ITestNGMethod testNGMethod, Object[] parameters) {
        return testNGMethod.getMethodName();
    }
}
