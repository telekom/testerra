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
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class ExecutionContext extends AbstractContext implements SynchronizableContext {
    private final Queue<SuiteContext> suiteContexts = new ConcurrentLinkedQueue<>();
    public final RunConfig runConfig = new RunConfig();
    public boolean crashed = false;
    private Queue<SessionContext> exclusiveSessionContexts;

    public int estimatedTestMethodCount;

    private final ConcurrentLinkedQueue<LogMessage> methodContextLessLogs = new ConcurrentLinkedQueue<>();

    public ExecutionContext() {
        name = runConfig.RUNCFG;
        TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(this));
    }

    public Stream<SuiteContext> readSuiteContexts() {
        return this.suiteContexts.stream();
    }

    public Stream<SessionContext> readExclusiveSessionContexts() {
        if (this.exclusiveSessionContexts == null) {
            return Stream.empty();
        } else {
            return exclusiveSessionContexts.stream();
        }
    }

    public ExecutionContext addExclusiveSessionContext(SessionContext sessionContext) {
        if (this.exclusiveSessionContexts == null) {
            this.exclusiveSessionContexts = new ConcurrentLinkedQueue<>();
        }
        if (!this.exclusiveSessionContexts.contains(sessionContext)) {
            this.exclusiveSessionContexts.add(sessionContext);
            sessionContext.parentContext = this;
        }
        return this;
    }

    public ExecutionContext addLogMessage(LogMessage logMessage) {
        this.methodContextLessLogs.add(logMessage);
        return this;
    }

    public Stream<LogMessage> readMethodContextLessLogs() {
        return this.methodContextLessLogs.stream();
    }

    public SuiteContext getSuiteContext(ITestResult testResult) {
        return getSuiteContext(TesterraListener.getContextGenerator().getSuiteContextName(testResult));
    }

    private synchronized SuiteContext getSuiteContext(String suiteContextName) {
        return getOrCreateContext(
                suiteContexts,
                suiteContextName,
                () -> new SuiteContext(this),
                suiteContext -> {
                    EventBus eventBus = TesterraListener.getEventBus();
                    eventBus.post(new ContextUpdateEvent().setContext(this));
                });
    }

    public SuiteContext getSuiteContext(ITestContext testContext) {
        return getSuiteContext(TesterraListener.getContextGenerator().getSuiteContextName(testContext));
    }

    @Override
    public TestStatusController.Status getStatus() {
        if (Flags.FAILURE_CORRIDOR_ACTIVE) {
            if (FailureCorridor.isCorridorMatched()) {
                return TestStatusController.Status.PASSED;
            } else {
                return TestStatusController.Status.FAILED;
            }
        } else {
            return getStatusFromContexts(suiteContexts.stream());
        }
    }

    public TestStatusController.Status[] getAvailableStatuses() {
        return TestStatusController.Status.values();
    }

    public TestStatusController.Status[] getAvailableStatus() {
        return TestStatusController.Status.values();
    }
}
