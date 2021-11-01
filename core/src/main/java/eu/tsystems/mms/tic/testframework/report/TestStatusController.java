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

import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.internal.MethodRelations;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.SkipException;
import static eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController.getCurrentExecutionContext;

public class TestStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestStatusController.class);
    private static int testsSuccessful = 0;
    private static int testsSkipped = 0;
    private static int testsFailed = 0;

    /*
    Failure corridor values
     */
    private static int testsFailedHIGH = 0;
    private static int testsFailedMID = 0;
    private static int testsFailedLOW = 0;

    private static int testsFailedRetried = 0;
    private static int testsExpectedFailed = 0;
    private static final String SEPARATOR = ", ";

    private TestStatusController() {

    }
//
//    public static JSONObject createStatusJSON() {
//        Map<String, Object> statusMap = new HashMap<>();
//
//        statusMap.put("TestsSuccessful", testsSuccessful);
//        statusMap.put("TestsSkipped", testsSkipped);
//        statusMap.put("TestsFailed", testsFailed);
//
//        statusMap.put("FailureCorridorActive", Flags.FAILURE_CORRIDOR_ACTIVE);
//        statusMap.put("DryRun", Flags.DRY_RUN);
//
//        statusMap.put("Status", getCurrentExecutionContext().getStatus());
//        statusMap.put("StatusBool", getCurrentExecutionContext().getStatus() == Status.PASSED);
//
//        statusMap.put("RunCfg", getCurrentExecutionContext().runConfig.RUNCFG);
//        statusMap.put("Date", getCurrentExecutionContext().startTime.toString());
//
//        return new JSONObject(statusMap);
//    }

    public static void setMethodStatus(MethodContext methodContext, Status status, Method method) {
        /*
        check for additional marker annotations
         */
        //Annotation[] annotations = method.getAnnotations();
        //methodContext.methodTags = Arrays.stream(annotations).collect(Collectors.toList());

        /*
        set status
         */
        if (methodContext.getTestNgResult().isPresent()) {
            ITestResult testResult = methodContext.getTestNgResult().get();
            Throwable throwable = testResult.getThrowable();

            if (testResult.getStatus() == ITestResult.CREATED && status == Status.FAILED) {
                LOGGER.warn("TestNG bug - result status is CREATED, which is wrong. Method status is " + Status.FAILED +
                        ", which is also wrong. Assuming SKIPPED.");
                status = Status.SKIPPED;
            } else if (throwable instanceof SkipException) {
                LOGGER.info("Found SkipException");
                status = Status.SKIPPED;
            }
        }

        methodContext.setStatus(status);
        methodContext.updateEndTimeRecursive(new Date());

        // announce to run context
        MethodRelations.announceRun(method, methodContext);

        if (methodContext.isConfigMethod()) {
            return;
            // stop here
        }

        // raise counters
        switch (status) {
            case NO_RUN:
            case INFO:
                break;

            case PASSED_RETRY:
            case MINOR:
            case MINOR_RETRY:
            case PASSED:
                testsSuccessful++;

                break;

            case FAILED_EXPECTED:
                testsExpectedFailed++;
                break;

            case FAILED_MINOR:
            case FAILED:
                testsFailed++;
                levelFC(methodContext, true);
                break;

            case SKIPPED:
                testsSkipped++;
                break;

            case FAILED_RETRIED:
                testsFailedRetried++;
                testsFailed--;

                levelFC(methodContext, false);
                break;

            default:
                throw new SystemException("Not implemented: " + status);
        }

        // print out current test execution state
        writeCounterToLog();
    }

    private static void levelFC(MethodContext methodContext, boolean raise) {
        Class failureCorridorClass = methodContext.getFailureCorridorClass();
        if (failureCorridorClass.equals(FailureCorridor.High.class)) {
            if (raise) {
                testsFailedHIGH++;
            } else {
                testsFailedHIGH--;
            }
        } else if (failureCorridorClass.equals(FailureCorridor.Mid.class)) {
            if (raise) {
                testsFailedMID++;
            } else {
                testsFailedMID--;
            }
        } else {
            if (raise) {
                testsFailedLOW++;
            } else {
                testsFailedLOW--;
            }
        }
    }

    public static String getFinalCountersMessage() {
        // V-X-S: 3-2-1  H-M-L: 0-0-0 (1-1-1)
        // 3 Passed, 2 Failed, 1 ExpFailed, 1 Skipped
        List<String> out = new ArrayList<>();

        if (testsSuccessful > 0) {
            out.add(testsSuccessful + " Passed");
        }
        if (testsFailed > 0) {
            out.add(testsFailed + " Failed");
        }
        if (testsSkipped > 0) {
            out.add(testsSkipped + " Skipped");
        }
        if (testsExpectedFailed > 0) {
            out.add(testsExpectedFailed + " ExpFailed");
        }

        return String.join(SEPARATOR, out);
    }

    public static String getCounterInfoMessage() {
        String out = getFinalCountersMessage();
        if (testsFailedRetried > 0) {
            out += SEPARATOR + testsFailedRetried + " Retried";
        }
        return out;
    }

    public static void writeCounterToLog() {
        String counterInfoMessage = getCounterInfoMessage();
        String logMessage = ExecutionContextController.getCurrentExecutionContext().runConfig.getReportName() + " " +
                getCurrentExecutionContext().runConfig.RUNCFG + ": " + counterInfoMessage;

        LOGGER.info(logMessage);
    }

    public static int getTestsFailed() {
        return testsFailed;
    }

    public static int getTestsFailedHIGH() {
        return testsFailedHIGH;
    }

    public static int getTestsFailedMID() {
        return testsFailedMID;
    }

    public static int getTestsFailedLOW() {
        return testsFailedLOW;
    }

    public static int getTestsSuccessful() {
        return testsSuccessful;
    }

    public static int getTestsSkipped() {
        return testsSkipped;
    }

    public static int getTestsFailedRetried() {
        return testsFailedRetried;
    }

    public static int getTestsExpectedFailed() {
        return testsExpectedFailed;
    }

    public static int getAllFailed() {
        return testsFailed + testsExpectedFailed;
    }

    public static boolean areAllTestsPassedYet() {
        return (getAllFailed() + getTestsSkipped()) == 0;
    }

    public enum Status {
        PASSED("Passed", true, true),
        /**
         * @deprecated Remove this after discontinuing 'report' module
         */
        MINOR("Minor", true, true),
        PASSED_RETRY("Passed after Retry", true, true),
        /**
         * @deprecated Remove this after discontinuing 'report' module
         */
        MINOR_RETRY("Minor after Retry", false, true),
        /**
         * @deprecated Remove this after discontinuing 'report' module
         */
        INFO("Info", true, false),

        FAILED("Failed", true, true),
        /**
         * @deprecated Remove this after discontinuing 'report' module
         */
        FAILED_MINOR("Failed + Minor", true, true),
        FAILED_RETRIED("Retried", true, false),
        FAILED_EXPECTED("Expected Failed", true, false),

        SKIPPED("Skipped", true, true),
        NO_RUN("No run", false, true); // this is basically an illegal state

        public final String title;
        public final boolean active;
        public final boolean relevant;

        public transient Map<Status, Integer> counts = new LinkedHashMap<>();

        Status(String title, boolean active, boolean relevant) {
            this.title = title;
            this.active = active;
            this.relevant = relevant;
        }

        public boolean isPassed() {
            switch (this) {
                case PASSED:
                case PASSED_RETRY:
                case MINOR_RETRY:
                case MINOR:
                case INFO:
                    return true;

                case SKIPPED:
                case NO_RUN:
                case FAILED:
                case FAILED_MINOR:
                case FAILED_EXPECTED:
                case FAILED_RETRIED:
                    return false;

                default:
                    throw new SystemException("Unhandled state: " + this);
            }
        }

        public boolean isFailed(boolean orSkipped, boolean withFailedExpected, boolean withRetried) {
            switch (this) {
                case SKIPPED:
                    if (orSkipped) {
                        return true;
                    }
                case NO_RUN:
                case PASSED:
                case PASSED_RETRY:
                case MINOR:
                case MINOR_RETRY:
                case INFO:
                    return false;

                case FAILED_RETRIED:
                    if (!withRetried) {
                        return false;
                    }
                case FAILED_EXPECTED:
                    if (!withFailedExpected) {
                        return false;
                    }
                case FAILED:
                case FAILED_MINOR:
                    return true;


                default:
                    throw new SystemException("Unhandled state: " + this);
            }
        }

        public boolean isSkipped() {
            switch (this) {
                case PASSED:
                case MINOR:
                case MINOR_RETRY:
                case PASSED_RETRY:
                case INFO:
                case FAILED:
                case FAILED_MINOR:
                case FAILED_RETRIED:
                case FAILED_EXPECTED:
                    return false;

                case SKIPPED:
                case NO_RUN:
                    return true;

                default:
                    throw new SystemException("Unhandled state: " + this);
            }
        }

    }
}
