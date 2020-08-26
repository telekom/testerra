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

package eu.tsystems.mms.tic.testframework.worker;

import eu.tsystems.mms.tic.testframework.execution.testng.worker.GenerateReportsWorker;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.DateUtils;
import eu.tsystems.mms.tic.testframework.utils.ReportUtils;

public class GenerateReportWorker extends GenerateReportsWorker implements Loggable {

    @Override
    public void run() {
        /*
         * Generate Report
         */
        long start = System.currentTimeMillis();
        ReportUtils.generateReportEssentials();
        long stop = System.currentTimeMillis();
        String formattedDuration = DateUtils.getFormattedDuration(stop - start, false);
        log().debug("Took " + formattedDuration + " to create the report.");
    }
}
