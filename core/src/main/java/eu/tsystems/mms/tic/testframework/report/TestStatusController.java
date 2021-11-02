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

import eu.tsystems.mms.tic.testframework.internal.MethodRelations;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.SkipException;
import static eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController.getCurrentExecutionContext;

public class TestStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestStatusController.class);
    private static final ExecutionContext executionContext = ExecutionContextController.getCurrentExecutionContext();
    private static final String SEPARATOR = ", ";
    private static final StatusCounter statusCounter = new StatusCounter();

    private TestStatusController() {

    }

    public static void finalizeMethod(MethodContext methodContext, Method method) {
        /*
        check for additional marker annotations
         */
        //Annotation[] annotations = method.getAnnotations();
        //methodContext.methodTags = Arrays.stream(annotations).collect(Collectors.toList());

        Status status = methodContext.getStatus();

        /*
        set status
         */
        if (methodContext.getTestNgResult().isPresent()) {
            ITestResult testResult = methodContext.getTestNgResult().get();
            Throwable throwable = testResult.getThrowable();

            if (testResult.getStatus() == ITestResult.CREATED && status == Status.FAILED) {
                LOGGER.warn("TestNG bug - result status is CREATED, which is wrong. Method status is " + Status.FAILED +
                        ", which is also wrong. Assuming SKIPPED.");
                methodContext.setStatus(Status.SKIPPED);
            } else if (throwable instanceof SkipException) {
                LOGGER.info("Found SkipException");
                methodContext.setStatus(Status.SKIPPED);
            }
        }
        methodContext.updateEndTimeRecursive(new Date());

        // announce to run context
        MethodRelations.announceRun(method, methodContext);

        if (!methodContext.isConfigMethod()) {
            statusCounter.increment(methodContext.getStatus());
        }
    }

    public static void writeCounterToLog() {
        String counterInfoMessage = Stream.of(Status.RETRIED, Status.FAILED, Status.FAILED_EXPECTED, Status.SKIPPED, Status.PASSED)
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

        String logMessage = ExecutionContextController.getCurrentExecutionContext().runConfig.getReportName() + " " +
                getCurrentExecutionContext().runConfig.RUNCFG + ": " + counterInfoMessage;

        LOGGER.info(logMessage);
    }
    public static int getTestsFailed() {
        return statusCounter.get(Status.FAILED);
    }

    /**
     * @deprecated Use {@link ExecutionContext#getFailureCorridorCount(Class)} instead
     */
    public static int getTestsFailedHIGH() {
        return executionContext.getFailureCorridorCount(FailureCorridor.High.class);
    }

    /**
     * @deprecated Use {@link ExecutionContext#getFailureCorridorCount(Class)} instead
     */
    public static int getTestsFailedMID() {
        return executionContext.getFailureCorridorCount(FailureCorridor.Mid.class);
    }

    /**
     * @deprecated Use {@link ExecutionContext#getFailureCorridorCount(Class)} instead
     */
    public static int getTestsFailedLOW() {
        return executionContext.getFailureCorridorCount(FailureCorridor.Low.class);
    }

    public static int getTestsSuccessful() {
        return statusCounter.get(Status.PASSED);
    }
    public static int getTestsSkipped() {
        return statusCounter.get(Status.SKIPPED);
    }

    public enum Status {
        // Regular test passed
        PASSED("Passed"),
        // Regular test failed
        FAILED("Failed"),
        // Regular test skipped
        SKIPPED("Skipped"),
        // Test has no status
        NO_RUN("No run"),
        /**
         * Test {@link #FAILED} with {@link MethodContext#getFailsAnnotation()}
         */
        FAILED_EXPECTED("Expected Failed"),
        /**
         * Test {@link #PASSED} with {@link MethodContext#getFailsAnnotation()}
         */
        REPAIRED("Repaired"),
        /**
         * Test {@link #FAILED} with {@link MethodContext#hasBeenRetried()}
         */
        RETRIED("Retried"),
        /**
         * Test {@link #PASSED} with {@link MethodContext#hasBeenRetried()}
         */
        RECOVERED("Recovered"),
        ;

        public final String title;

        private static final Status[] PASSED_STATUS_GROUP = new Status[]{Status.PASSED, Status.REPAIRED, Status.RECOVERED};

        Status(String title) {
            this.title = title;
        }

        public static Status[] getStatusGroup(Status status) {
            if (status == Status.PASSED) {
                return PASSED_STATUS_GROUP;
            } else {
                return new Status[]{status};
            }
        }

        public boolean isStatisticallyRelevant() {
            return this != Status.RETRIED;
        }
    }
}
