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
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.ReportUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by pele on 31.08.2016.
 */
public final class FailureCorridor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailureCorridor.class);

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface High {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Mid {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Low {
    }

    public enum Value {
        HIGH(false, "grey"),
        MID(true, "grey"),
        LOW(true, "lightgrey");

        public final boolean show;
        public final String color;

        Value(boolean show, String color) {
            this.show = show;
            this.color = color;
        }
    }

    private static int allowedTestFailures = PropertyManager.getIntProperty(TesterraProperties.FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS, -1);
    private static int allowedTestFailuresHIGH = PropertyManager.getIntProperty(TesterraProperties.FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_HIGH, -1);
    private static int allowedTestFailuresMID = PropertyManager.getIntProperty(TesterraProperties.FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_MID, -1);
    private static int allowedTestFailuresLOW = PropertyManager.getIntProperty(TesterraProperties.FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_LOW, -1);

    public static void setFailureCorridorActive(boolean active) {
        Flags.FAILURE_CORRIDOR_ACTIVE = active;
    }

    public static void setAllowedTestFailures(int allowedTestFailures) {
        FailureCorridor.allowedTestFailures = allowedTestFailures;
    }

    public static void setFailureCorridor(int high, int mid, int low) {
        allowedTestFailuresHIGH = high;
        allowedTestFailuresMID = mid;
        allowedTestFailuresLOW = low;
    }

    public static boolean isCorridorMatched() {
        int testsSuccessful = TestStatusController.getTestsSuccessful();
        int testsFailed = TestStatusController.getTestsFailed();
        int testsSkipped = TestStatusController.getTestsSkipped();

        int testsFailedHIGH = TestStatusController.getTestsFailedHIGH();
        int testsFailedMID = TestStatusController.getTestsFailedMID();
        int testsFailedLOW = TestStatusController.getTestsFailedLOW();

        /*
        check for invalid state
         */
        if (testsSkipped > 0) {
            return false;
        }
        else if (testsSuccessful + testsFailed + testsSkipped == 0) {
            return false;
        }

        /*
        1) check if ANY test failed
         */
        if (allowedTestFailures > -1) {
            if (testsFailed > allowedTestFailures) {
                return false;
            }
        }

        /*
        2) check the exact failure corridor
         */
        if (allowedTestFailuresHIGH > -1 && allowedTestFailuresMID > -1 && allowedTestFailuresLOW > -1) {
            if (testsFailedHIGH > allowedTestFailuresHIGH) {
                return false;
            }
            else {
                if (testsFailedMID > allowedTestFailuresMID) {
                    return false;
                }
                else {
                    if (testsFailedLOW > allowedTestFailuresLOW) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static String getStatusMessage() {
        // -OK-
        // -NOT OK-
        if (isCorridorMatched()) {
            return "-OK-";
        }
        else {
            return "-NOT OK-";
        }
    }

    private static String getStatistics() {
        //  V-X-S: 3-2-1  H-M-L: 0-0-0 (1-1-1)

        int testsSuccessful = TestStatusController.getTestsSuccessful();
        int testsFailed = TestStatusController.getTestsFailed();
        int testsSkipped = TestStatusController.getTestsSkipped();

        int testsFailedHIGH = TestStatusController.getTestsFailedHIGH();
        int testsFailedMID = TestStatusController.getTestsFailedMID();
        int testsFailedLOW = TestStatusController.getTestsFailedLOW();

        String out = "";
        String badMarker = "*";

        /*
        check for invalid state
         */
        if (testsSuccessful == 0 && testsFailed == 0 && testsSkipped > 0) {
            out += "Invalid state: skipped tests only! ";
        }

        if (allowedTestFailures > -1) {
//            int testsFailed = TestStatusController.getTestsFailed();
//            out += "  X: " + testsFailed;
//            if (testsFailed > allowedTestFailures) {
//                out += badMarker;
//            }
//            out += " (" + allowedTestFailures + ") ";

            // failed allowed is not needed atm - pele 05.09.2016
        }

        if (allowedTestFailuresHIGH > -1 && allowedTestFailuresMID > -1 && allowedTestFailuresLOW > -1) {
            out += "  H-M-L: " + testsFailedHIGH;
            if (testsFailedHIGH > allowedTestFailuresHIGH) {
                out += badMarker;
            }
            out+= "-" + testsFailedMID;
            if (testsFailedMID > allowedTestFailuresMID) {
                out += badMarker;
            }
            out += "-" + testsFailedLOW;
            if (testsFailedLOW > allowedTestFailuresLOW) {
                out += badMarker;
            }
            out += " (" + allowedTestFailuresHIGH + "-" + allowedTestFailuresMID + "-" + allowedTestFailuresLOW + ")" ;
        }

        if (testsSkipped > 0) {
            out += " Skipped: " + testsSkipped;
        }

        return out.trim();
    }

    public static int getAllowedTestFailuresHIGH() {
        return allowedTestFailuresHIGH;
    }

    public static int getAllowedTestFailuresMID() {
        return allowedTestFailuresMID;
    }

    public static int getAllowedTestFailuresLOW() {
        return allowedTestFailuresLOW;
    }

    public static void printStatusToStdOut() {
        String statusMessage = ReportUtils.getReportName() + " " + ExecutionContextController.EXECUTION_CONTEXT.runConfig + ": ";
        if (!FailureCorridor.isCorridorMatched()) {
            // show error first
            statusMessage += TestStatusController.getFinalCountersMessage() + " ";
            statusMessage += FailureCorridor.getStatusMessage();
        } else {
            // show passed first
            statusMessage += TestStatusController.getFinalCountersMessage() + " ";
            statusMessage += FailureCorridor.getStatusMessage();
        }


        String statistics = getStatistics();
        System.out.println(statistics);

        String stdOutMessage = "\n\ntesterra: " + statusMessage + "\n\n";
        System.out.println(stdOutMessage);
    }
}
