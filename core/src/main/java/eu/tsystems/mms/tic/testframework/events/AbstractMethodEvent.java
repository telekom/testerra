/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.events;

import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import java.lang.reflect.Method;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public abstract class AbstractMethodEvent {
    private ITestResult testResult;
    private String methodName;
    private MethodContext methodContext;
    private ITestContext testContext;
    private IInvokedMethod invokedMethod;

    public ITestResult getTestResult() {
        return testResult;
    }

    public AbstractMethodEvent setTestResult(ITestResult testResult) {
        this.testResult = testResult;
        return this;
    }

    public ITestNGMethod getTestMethod() {
        return TesterraListener.getContextGenerator().getTestMethod(testResult, testContext, invokedMethod);
    }

    public Method getMethod() {
        return getTestMethod().getConstructorOrMethod().getMethod();
    }

    public String getMethodName() {
        return methodName;
    }

    public AbstractMethodEvent setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public MethodContext getMethodContext() {
        return methodContext;
    }

    public AbstractMethodEvent setMethodContext(MethodContext methodContext) {
        this.methodContext = methodContext;
        return this;
    }

    public ITestContext getTestContext() {
        return testContext;
    }

    public AbstractMethodEvent setTestContext(ITestContext context) {
        this.testContext = context;
        return this;
    }

    public IInvokedMethod getInvokedMethod() {
        return invokedMethod;
    }

    public AbstractMethodEvent setInvokedMethod(IInvokedMethod invokedMethod) {
        this.invokedMethod = invokedMethod;
        return this;
    }
    public boolean isSkipped() {
        return testResult.getStatus() == ITestResult.SKIP;
    }

    public boolean isFailed() {
        return (!isPassed() && !isSkipped());
    }

    public boolean isPassed() {
        return testResult.isSuccess();
    }
}
