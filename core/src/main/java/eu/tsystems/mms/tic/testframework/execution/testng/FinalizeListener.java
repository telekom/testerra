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

package eu.tsystems.mms.tic.testframework.execution.testng;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.boot.Booter;
import eu.tsystems.mms.tic.testframework.events.ExecutionAbortEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;

/**
 * Listener for the very end of the test execution.
 */
public class FinalizeListener implements
        ExecutionAbortEvent.Listener,
        ExecutionFinishEvent.Listener
{
    @Override
    @Subscribe
    public void onExecutionAbort(ExecutionAbortEvent event) {
        finalizeTestExecution();
    }

    @Override
    @Subscribe
    public void onExecutionFinish(ExecutionFinishEvent event) {
        finalizeTestExecution();
    }

    private void finalizeTestExecution() {
        /*
         * Shutdown local services and hooks
         */
        JVMMonitor.stop();
        Booter.shutdown();
        /*
        print stats
         */
        ExecutionContextController.printExecutionStatistics();

        /*
         * Check failure corridor and set exit code and state
         */
        if (Flags.FAILURE_CORRIDOR_ACTIVE) {
            FailureCorridor.printStatusToStdOut();
        }

        Report report = new Report();
        report.finalizeReport();
    }
}
