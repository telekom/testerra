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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public final class FailureCorridor implements Loggable {

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

    @Deprecated
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

    private FailureCorridor() {

    }

    private static int allowedTestFailuresHIGH = Testerra.Properties.FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_HIGH.asLong().intValue();
    private static int allowedTestFailuresMID = Testerra.Properties.FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_MID.asLong().intValue();
    private static int allowedTestFailuresLOW = Testerra.Properties.FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_LOW.asLong().intValue();

    public static void setFailureCorridorActive(boolean active) {
        System.setProperty(Testerra.Properties.FAILURE_CORRIDOR_ACTIVE.toString(), Boolean.toString(active));
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
        } else if (testsSuccessful + testsFailed + testsSkipped == 0) {
            return false;
        }

        /*
        2) check the exact failure corridor
         */
        if (allowedTestFailuresHIGH > -1 && allowedTestFailuresMID > -1 && allowedTestFailuresLOW > -1) {
            if (testsFailedHIGH > allowedTestFailuresHIGH) {
                return false;
            } else {
                if (testsFailedMID > allowedTestFailuresMID) {
                    return false;
                } else {
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
        } else {
            return "-NOT OK-";
        }
    }

    public static String getStatistics() {
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

        if (allowedTestFailuresHIGH > -1 && allowedTestFailuresMID > -1 && allowedTestFailuresLOW > -1) {
            out += "  H-M-L: " + testsFailedHIGH;
            if (testsFailedHIGH > allowedTestFailuresHIGH) {
                out += badMarker;
            }
            out += "-" + testsFailedMID;
            if (testsFailedMID > allowedTestFailuresMID) {
                out += badMarker;
            }
            out += "-" + testsFailedLOW;
            if (testsFailedLOW > allowedTestFailuresLOW) {
                out += badMarker;
            }
            out += " (" + allowedTestFailuresHIGH + "-" + allowedTestFailuresMID + "-" + allowedTestFailuresLOW + ")";
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
}
