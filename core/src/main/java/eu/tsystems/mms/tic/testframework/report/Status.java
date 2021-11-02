/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;

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
