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

import java.util.function.Supplier;
import org.testng.IClass;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class DefaultTestNGContextGenerator {

    protected final <T> T tryAny(final Supplier<T>... getValues) {
        for (Supplier<T> getValue : getValues) {
            try {
                T value = getValue.get();
                if (value != null) {
                    return value;
                }
            } catch (Throwable e) {
                // ignore
            }
        }
        throw new RuntimeException("Could not get value");
    }

    public String getClassContextName(ITestResult testResult, ITestContext testContext, IInvokedMethod invokedMethod) {
        return this.getTestClass(testResult, testContext, invokedMethod).getName();
    }

    public String getSuiteContextName(ITestResult testResult, ITestContext testContext, IInvokedMethod invokedMethod) {
        return tryAny(
                () -> testResult.getTestContext().getSuite().getName(),
                () -> testContext.getSuite().getName()
        );

    }

    public String getTestContextName(ITestResult testResult, ITestContext testContext, IInvokedMethod invokedMethod) {
        return tryAny(
                () -> testResult.getTestContext().getCurrentXmlTest().getName(),
                () -> testContext.getCurrentXmlTest().getName()
        );
    }

    public String getMethodContextName(ITestResult testResult, ITestContext testContext, IInvokedMethod invokedMethod) {
        return getTestMethod(testResult, testContext, invokedMethod).getMethodName();
    }

    public IClass getTestClass(ITestResult testResult, ITestContext testContext, IInvokedMethod invokedMethod) {
        return tryAny(
                () -> testResult.getTestClass(),
                () -> invokedMethod.getTestMethod().getTestClass(),
                () -> invokedMethod.getTestResult().getTestClass(),
                () -> testResult.getMethod().getTestClass()
        );
    }

    public ITestNGMethod getTestMethod(ITestResult testResult, ITestContext testContext, IInvokedMethod invokedMethod) {
        return tryAny(
                () -> testResult.getMethod(),
                () -> invokedMethod.getTestMethod()
        );
    }

}
