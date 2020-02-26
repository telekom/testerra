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
 *     erku
 *     pele
 */
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.LinkedList;
import java.util.List;

public class SuiteContext extends AbstractContext implements SynchronizableContext {

    public final List<TestContextModel> testContextModels = new LinkedList<>();
    public final ExecutionContext executionContext;

    public SuiteContext(ExecutionContext executionContext) {
        this.parentContext = this.executionContext = executionContext;
    }

    public TestContextModel getTestContext(ITestResult testResult, ITestContext iTestContext) {
        final String testName = TestNGHelper.getTestName(testResult, iTestContext);
        return getContext(TestContextModel.class, testContextModels, testName, true, () -> new TestContextModel(this, executionContext));
    }

    public TestContextModel getTestContext(final ITestContext iTestContext) {
        return this.getTestContext(null, iTestContext);
    }

    public List<TestContextModel> copyOfTestContexts() {
        synchronized (testContextModels) {
            return new LinkedList<>(testContextModels);
        }
    }

    @Override
    public TestStatusController.Status getStatus() {
        return getStatusFromContexts(testContextModels.toArray(new AbstractContext[0]));
    }
}
