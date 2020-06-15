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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.execution.testng.worker.finish;

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.ReportUtils;

/**
 * Updates the TeamCity Agents
 * Original extracted from {@link TestStatusController}
 * @author pele
 */
public class TeamCityMethodContextUpdateWorker extends MethodWorker implements Loggable {

    @Override
    public void run() {
        if (
            methodContext.isConfigMethod()
            || methodContext.status == TestStatusController.Status.INFO
        ) {
            return;
        }

        String counterInfoMessage = TestStatusController.getCounterInfoMessage();
        String teamCityMessage = ReportUtils.getReportName() + " " + ExecutionContextController.getCurrentExecutionContext().runConfig.RUNCFG + ": " + counterInfoMessage;

        log().info(teamCityMessage);
    }
}
