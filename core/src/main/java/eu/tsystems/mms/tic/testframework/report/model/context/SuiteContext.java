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
 *     erku <Eric.Kubenka@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.LinkedList;
import java.util.List;

public class SuiteContext extends Context implements SynchronizableContext {

    public final List<TestContext> testContexts = new LinkedList<>();
    public final ExecutionContext executionContext;

    public SuiteContext(ExecutionContext executionContext) {
        this.parentContext = this.executionContext = executionContext;
    }

    public TestContext getTestContext(ITestResult testResult, ITestContext iTestContext) {
        final String testName = TestNGHelper.getTestName(testResult, iTestContext);
        return getContext(TestContext.class, testContexts, testName, true, () -> new TestContext(this, executionContext));
    }

    public TestContext getTestContext(final ITestContext iTestContext) {

        final String testName = iTestContext.getCurrentXmlTest().getName();
        return getContext(TestContext.class, testContexts, testName, true, () -> new TestContext(this, executionContext));
    }

    public List<TestContext> copyOfTestContexts() {
        synchronized (testContexts) {
            return new LinkedList<>(testContexts);
        }
    }

    @Override
    public TestStatusController.Status getStatus() {
        return getStatusFromContexts(testContexts.toArray(new Context[0]));
    }

}
