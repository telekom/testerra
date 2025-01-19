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

package eu.tsystems.mms.tic.testframework.report;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionAbortEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.internal.MethodRelations;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Listener for the end of the test execution.
 * It finalizes the {@link ExecutionContext} and posts the {@link FinalizeExecutionEvent}.
 * NOTE: It is package private to prevent miss usage.
 */
public final class ExecutionEndListener implements
        ExecutionFinishEvent.Listener,
        ExecutionAbortEvent.Listener,
        Loggable {

    IExecutionContextController contextController = Testerra.getInjector().getInstance(IExecutionContextController.class);

    private static final String statsPrefix = "*** Stats: ";

    @Override
    @Subscribe
    public void onExecutionAbort(ExecutionAbortEvent event) {
        log().warn("Test execution was aborted. Test results may be incomplete.", Loggable.prompt);
        contextController.getExecutionContext().setCrashed();
        finalizeExecutionContext();
        log().info("Report generated after unexpected abortion of test execution.");
    }

    @Override
    @Subscribe
    public void onExecutionFinish(ExecutionFinishEvent event) {
        // set the testRunFinished flag
        finalizeExecutionContext();
        log().info("Report generated after successful test execution.");
        // set the reportGenerated flag
        contextController.getExecutionContext().setReportModelGenerated();
    }

    private void finalizeExecutionContext() {
//        MethodRelations.flushAll();

        ExecutionContext currentExecutionContext = contextController.getExecutionContext();
        currentExecutionContext.updateEndTimeRecursive(new Date());

        printExecutionStatistics();

        EventBus eventBus = Testerra.getEventBus();

        eventBus.post(new ContextUpdateEvent().setContext(currentExecutionContext));
        eventBus.post(new FinalizeExecutionEvent().setExecutionContext(currentExecutionContext));
    }

    private void printExecutionStatistics() {
        final ExecutionContext executionContext = contextController.getExecutionContext();

        log().info(statsPrefix + "**********************************************");

        log().info(statsPrefix + "ExecutionContext: " + executionContext.getName());
        AtomicInteger suiteContextCount = new AtomicInteger();
        AtomicInteger classContextCount = new AtomicInteger();
        AtomicInteger testContextCount = new AtomicInteger();
        AtomicInteger methodContextCount = new AtomicInteger();
        AtomicInteger testMethodContextCount = new AtomicInteger();
        AtomicInteger relevantMethodContextCount = new AtomicInteger();

        executionContext.readSuiteContexts().forEach(suiteContext -> {
            suiteContextCount.incrementAndGet();
            suiteContext.readTestContexts().forEach(testContext -> {
                testContextCount.incrementAndGet();
                testContext.readClassContexts().forEach(classContext -> {
                    classContextCount.incrementAndGet();
                    classContext.readMethodContexts().forEach(methodContext -> {
                        methodContextCount.incrementAndGet();

                        if (methodContext.isTestMethod()) {
                            testMethodContextCount.incrementAndGet();

                            if (methodContext.getStatus().isStatisticallyRelevant()) {
                                relevantMethodContextCount.incrementAndGet();
                            }
                        }
                    });
                });
            });
        });
        log().info(statsPrefix + "SuiteContexts:  " + suiteContextCount.get());
        log().info(statsPrefix + "TestContexts:   " + testContextCount.get());
        log().info(statsPrefix + "ClassContexts:  " + classContextCount.get());
        log().info(statsPrefix + "MethodContexts: " + methodContextCount.get());
        log().info(statsPrefix + "**********************************************");
        log().info(statsPrefix + "Test Methods Count: " + testMethodContextCount.get() + " (" + relevantMethodContextCount.get() + " relevant)");

        ITestStatusController testStatusController = TesterraListener.getTestStatusController();
        StatusCounter statusCounter = testStatusController.getStatusCounter();

        logStatusSet(Stream.of(Status.FAILED), statusCounter);
        logStatusSet(Stream.of(Status.RETRIED), statusCounter);
        logStatusSet(Stream.of(Status.FAILED_EXPECTED), statusCounter);
        logStatusSet(Stream.of(Status.SKIPPED), statusCounter);
        logStatusSet(Stream.of(Status.PASSED, Status.RECOVERED, Status.REPAIRED), statusCounter);

        log().info(statsPrefix + "**********************************************");
        Status overallStatus = Status.FAILED;
        if (Testerra.Properties.FAILURE_CORRIDOR_ACTIVE.asBool()) {
            if (FailureCorridor.isCorridorMatched()) {
                overallStatus = Status.PASSED;
            }
        } else {
            if (testStatusController.getTestsFailed() == 0) {
                overallStatus = Status.PASSED;
            }
        }

        log().info(statsPrefix + "ExecutionContext Status: " + overallStatus.title);
        log().info(statsPrefix + "FailureCorridor Enabled: " + Testerra.Properties.FAILURE_CORRIDOR_ACTIVE.asBool());

        if (Testerra.Properties.FAILURE_CORRIDOR_ACTIVE.asBool()) {
            log().info(statsPrefix + "**********************************************");
            log().info(statsPrefix + "FailureCorridor Status : " + FailureCorridor.getStatistics() + " " + FailureCorridor.getStatusMessage());
        }

        log().info(statsPrefix + "**********************************************");

        log().info(statsPrefix + "Duration: " + executionContext.getDurationAsString());

        log().info(statsPrefix + "**********************************************");
    }

    private void logStatusSet(Stream<Status> statuses, StatusCounter statusCounter) {
        String statusSetString = createStatusSetString(statuses, statusCounter);
        if (statusSetString.length() > 0) {
            log().info(statsPrefix + statusSetString);
        }
    }

    private String createStatusSetString(Stream<Status> statuses, StatusCounter statusCounter) {
        return statuses
                .map(status -> {
                    int summarizedTestStatusCount = statusCounter.getSum(Status.getStatusGroup(status));
                    if (summarizedTestStatusCount > 0) {
                        return status.title + ": " + summarizedTestStatusCount;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" ⊃ "));
    }
}
