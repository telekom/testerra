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
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class ExecutionContext extends AbstractContext implements SynchronizableContext, HasStatus {
    private final Queue<SuiteContext> suiteContexts = new ConcurrentLinkedQueue<>();
    public final RunConfig runConfig = new RunConfig();
    public boolean crashed = false;
    private Queue<SessionContext> exclusiveSessionContexts;
    public int estimatedTestMethodCount;
    private final ConcurrentLinkedQueue<LogMessage> methodContextLessLogs = new ConcurrentLinkedQueue<>();
    private final Map<TestStatusController.Status, Integer> statusCounts = new ConcurrentHashMap<>();
    private final Map<Class, Integer> failureCorridorCounts = new ConcurrentHashMap<>();

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
            if (statusCounts.containsKey(TestStatusController.Status.FAILED)) {
                return TestStatusController.Status.FAILED;
            } else {
                return TestStatusController.Status.PASSED;
            }
        }
    }

    public void addStatusCount(MethodContext methodContext) {

        if (methodContext.isConfigMethod()) {
            return;
        }

        TestStatusController.Status methodStatus = methodContext.getStatus();

        incrementStatus(methodStatus);

//        if (methodContext.hasBeenRetried()) {
//            if (methodStatus == TestStatusController.Status.PASSED) {
//                incrementStatus(TestStatusController.Status.RECOVERED);
//            } else {
//                incrementStatus(TestStatusController.Status.RETRIED);
//            }
//        }

        methodContext.getFailsAnnotation().ifPresent(fails -> {
            if (methodStatus == TestStatusController.Status.PASSED) {
                incrementStatus(TestStatusController.Status.REPAIRED);
            }
        });

        if (methodStatus == TestStatusController.Status.FAILED) {
            incrementFailureCorridor(methodContext.getFailureCorridorClass());
        }
    }

    public int getStatusCount(TestStatusController.Status status) {
        return statusCounts.getOrDefault(status, 0);
    }

    public void incrementStatus(TestStatusController.Status status) {
        int statusCount = getStatusCount(status);
        statusCount++;
        statusCounts.put(status, statusCount);
    }

    public int getFailureCorridorCount(Class failureCorridorClass) {
        return failureCorridorCounts.getOrDefault(failureCorridorClass, 0);
    }

    public void incrementFailureCorridor(Class failureCorridorClass) {
        int failureCorridorCount = getFailureCorridorCount(failureCorridorClass);
        failureCorridorCount++;
        failureCorridorCounts.put(failureCorridorClass, failureCorridorCount);
    }

    public Stream<Map.Entry<TestStatusController.Status, Integer>> readStatusCounts() {
        return statusCounts.entrySet().stream();
    }
}
