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

package eu.tsystems.mms.tic.testframework.report;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.events.TestStatusUpdateEvent;
import eu.tsystems.mms.tic.testframework.internal.MethodRelations;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.RunConfig;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import org.testng.ITestResult;
import org.testng.SkipException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestStatusController implements TestStatusUpdateEvent.Listener, Loggable, ITestStatusController {
    private static final String SEPARATOR = ", ";
    private final StatusCounter statusCounter = new StatusCounter();
    private final Map<Class, Integer> failureCorridorCounts = new ConcurrentHashMap<>();
    private final IExecutionContextController executionContextController = Testerra.getInjector().getInstance(IExecutionContextController.class);

    private void finalizeMethod(MethodContext methodContext) {
        Status status = methodContext.getStatus();

        /*
        set status
         */
        if (methodContext.getTestNgResult().isPresent()) {
            ITestResult testResult = methodContext.getTestNgResult().get();
            Method method = testResult.getMethod().getConstructorOrMethod().getMethod();
            Throwable throwable = testResult.getThrowable();

            if (testResult.getStatus() == ITestResult.CREATED && status == Status.FAILED) {
                log().warn("TestNG bug - result status is CREATED, which is wrong. Method status is " + Status.FAILED +
                        ", which is also wrong. Assuming SKIPPED.");
                methodContext.setStatus(Status.SKIPPED);
            } else if (throwable instanceof SkipException) {
                log().info("Found SkipException");
                methodContext.setStatus(Status.SKIPPED);
            }

            // announce to run context
            MethodRelations.announceRun(method, methodContext);
        }
        methodContext.updateEndTimeRecursive(new Date());

        // Only add status count for tests, not config methds
        if (methodContext.isTestMethod()) {
            statusCounter.increment(methodContext.getStatus());

            if (methodContext.getStatus() == Status.FAILED) {
                incrementFailureCorridor(methodContext.getFailureCorridorClass());
            }

            writeCounterToLog();
        }
    }

    @Override
    public String getCounterInfoMessage() {
        return Stream.of(Status.RETRIED, Status.FAILED, Status.FAILED_EXPECTED, Status.SKIPPED, Status.PASSED)
                .map(status -> {
                    int summarizedTestStatusCount = statusCounter.getSum(Status.getStatusGroup(status));
                    if (summarizedTestStatusCount > 0) {
                        return summarizedTestStatusCount + " " + status.title;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(SEPARATOR));
    }

    private void writeCounterToLog() {
        RunConfig runConfig = executionContextController.getExecutionContext().getRunConfig();
        String logMessage = runConfig.getReportName() + " " + runConfig.RUNCFG + ": " + getCounterInfoMessage();
        log().info(logMessage);
    }
    @Override
    public int getTestsFailed() {
        return statusCounter.get(Status.FAILED);
    }

    @Override
    public int getTestsFailedHIGH() {
        return getFailureCorridorCount(FailureCorridor.High.class);
    }

    @Override
    public int getTestsFailedMID() {
        return getFailureCorridorCount(FailureCorridor.Mid.class);
    }

    @Override
    public int getTestsFailedLOW() {
        return getFailureCorridorCount(FailureCorridor.Low.class);
    }

    @Override
    public int getTestsSuccessful() {
        return statusCounter.get(Status.PASSED);
    }

    @Override
    public int getTestsSkipped() {
        return statusCounter.get(Status.SKIPPED);
    }

    @Subscribe
    @Override
    public void onTestStatusUpdate(TestStatusUpdateEvent event) {
        MethodContext methodContext = event.getMethodContext();
        finalizeMethod(methodContext);
        TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(methodContext));
    }

    @Override
    public int getFailureCorridorCount(Class failureCorridorClass) {
        return failureCorridorCounts.getOrDefault(failureCorridorClass, 0);
    }

    private void incrementFailureCorridor(Class failureCorridorClass) {
        int failureCorridorCount = getFailureCorridorCount(failureCorridorClass);
        failureCorridorCount++;
        failureCorridorCounts.put(failureCorridorClass, failureCorridorCount);
    }

    @Override
    public StatusCounter getStatusCounter() {
        return statusCounter;
    }
}
