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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.execution.testng.worker;

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.lang.reflect.Method;

/**
 * Created by pele on 19.01.2017.
 */
public abstract class MethodWorker implements Worker {

    protected static final Logger LOGGER = LoggerFactory.getLogger("ResultHandler");

    protected ITestResult testResult;
    protected ITestNGMethod testMethod;
    protected Method method;
    protected String methodName;
    protected MethodContext methodContext;
    protected ITestContext context;
    protected IInvokedMethod invokedMethod;

    public MethodWorker() {
    }

    public static class SharedTestResultAttributes {
        public static final String failsFromCollectedAssertsOnly = "failsFromCollectedAssertsOnly";
        public static final String expectedFailed = "expectedFailed";
    }

    public void init(ITestResult testResult, String methodName, MethodContext methodContext, ITestContext context, IInvokedMethod invokedMethod) {
        this.testResult = testResult;
        this.testMethod = TestNGHelper.getTestMethod(testResult, context, invokedMethod);
        this.method = testMethod.getConstructorOrMethod().getMethod();
        this.methodName = methodName;
        this.methodContext = methodContext;

        this.context = context;
        this.invokedMethod = invokedMethod;
    }

    protected boolean isTest() {
        return testMethod.isTest();
    }

    protected boolean isSuccess() {
        return testResult.isSuccess();
    }

    protected boolean isSkipped() {
        return testResult.getStatus() == ITestResult.SKIP;
    }

    protected boolean isFailed() {
        return (!isSuccess()&& !isSkipped());
    }

    public abstract void run();

    public boolean wasMethodInvoked() {
        return invokedMethod != null;
    }

}
