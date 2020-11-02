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
 package eu.tsystems.mms.tic.testframework.report.model.context;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class SuiteContext extends AbstractContext implements SynchronizableContext {

    public final Queue<TestContextModel> testContextModels = new ConcurrentLinkedQueue<>();
    public final ExecutionContext executionContext;

    public SuiteContext(ExecutionContext executionContext) {
        this.parentContext = this.executionContext = executionContext;
    }

    public TestContextModel getTestContext(ITestResult testResult, ITestContext iTestContext) {
        final String testName = TestNGHelper.getTestName(testResult, iTestContext);
        return getOrCreateContext(
                testContextModels,
                testName,
                () -> new TestContextModel(this, executionContext),
                testContextModel -> {
                    EventBus eventBus = TesterraListener.getEventBus();
                    eventBus.post(new ContextUpdateEvent().setContext(this));
                });
    }

    public TestContextModel getTestContext(final ITestContext iTestContext) {
        return this.getTestContext(null, iTestContext);
    }

    @Override
    public TestStatusController.Status getStatus() {
        return getStatusFromContexts(testContextModels.stream());
    }
}
