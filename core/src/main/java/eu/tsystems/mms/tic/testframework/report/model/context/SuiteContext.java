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
 */
 package eu.tsystems.mms.tic.testframework.report.model.context;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class SuiteContext extends AbstractContext {
    private final Queue<TestContext> testContexts = new ConcurrentLinkedQueue<>();

    public SuiteContext(ExecutionContext executionContext) {
        this.setParentContext(executionContext);
    }

    public ExecutionContext getExecutionContext() {
        return (ExecutionContext)this.getParentContext();
    }

    public Stream<TestContext> readTestContexts() {
        return testContexts.stream();
    }

    public TestContext getTestContext(ITestResult testResult) {
        return getTestContext(contextNameGenerator.getTestContextName(testResult));
    }

    public TestContext getTestContext(ITestContext testContext) {
        return getTestContext(contextNameGenerator.getTestContextName(testContext));
    }

    private synchronized TestContext getTestContext(String testContextName) {
        return getOrCreateContext(
                testContexts,
                testContextName,
                () -> new TestContext(this),
                testContextModel -> {
                    EventBus eventBus = Testerra.getEventBus();
                    eventBus.post(new ContextUpdateEvent().setContext(this));
                });
    }
}
