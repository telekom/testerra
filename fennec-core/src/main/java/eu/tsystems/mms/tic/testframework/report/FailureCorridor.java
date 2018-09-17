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
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
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

    public static enum Value {
        High(false, "grey"),
        Mid(true, "grey"),
        Low(true, "lightgrey");

        public final boolean show;
        public final String color;

        Value(boolean show, String color) {
            this.show = show;
            this.color = color;
        }
    }

    private static int allowedTestFailures = PropertyManager.getIntProperty(FennecProperties.Fennec_FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS, -1);
    private static int allowedTestFailuresHIGH = PropertyManager.getIntProperty(FennecProperties.Fennec_FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_HIGH, -1);
    private static int allowedTestFailuresMID = PropertyManager.getIntProperty(FennecProperties.Fennec_FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_MID, -1);
    private static int allowedTestFailuresLOW = PropertyManager.getIntProperty(FennecProperties.Fennec_FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_LOW, -1);

    public static void setFailureCorridorActive(boolean active) {
        Flags.Fennec_FAILURE_CORRIDOR_ACTIVE = active;
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

    public static void printStatusAndJumpOut() {
        String statusMessage = ReportUtils.getReportName() + " " + ExecutionContextController.RUN_CONTEXT.runConfig + ": ";
        if (!FailureCorridor.isCorridorMatched()) {
            // show error first
            statusMessage += TestStatusController.getFinalCountersMessage() + " ";
            statusMessage += FailureCorridor.getStatusMessage();
        } else {
            // show passed first
            statusMessage += TestStatusController.getFinalCountersMessage() + " ";
            statusMessage += FailureCorridor.getStatusMessage();
        }

        String stdOutMessage = "\n\nfennec: " + statusMessage + "\n\n";

        // exit
        if (!FailureCorridor.isCorridorMatched()) {
            LOGGER.info("Exiting due to failure corridor status (not matched)");
            String statistics = getStatistics();
            System.err.println(statistics);
            System.err.println(stdOutMessage);
            System.err.println("(fennec will endTime the build here. Don't mind about messages like 'The forked VM terminated without properly saying goodbye. VM crash or System.exit called?'.)");
            System.exit(99);
        } else {
            System.out.println(stdOutMessage);
            System.out.println("(Make sure '<testFailureIgnore>true</testFailureIgnore>' is set to see the tests GREEN, because they should be GREEN.)");
//            System.exit(0); go through, only works when testFailureIgnore is set to true in surefire
        }
    }
}
