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
import eu.tsystems.mms.tic.testframework.events.ExecutionAbortEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.internal.MethodRelations;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.util.Date;

/**
 * Called when the execution ends for any reason.
 */
public class ExecutionEndListener implements
        ExecutionFinishEvent.Listener,
        ExecutionAbortEvent.Listener
{
    @Override
    @Subscribe
    public void onExecutionFinish(ExecutionFinishEvent event) {
        finalizeExecution();
    }

    @Override
    @Subscribe
    public void onExecutionAbort(ExecutionAbortEvent event) {
        finalizeExecution();
    }

    public void finalizeExecution() {
        MethodRelations.flushAll();

        ExecutionContextController.getCurrentExecutionContext().endTime = new Date();

        // set the testRunFinished flag
        ExecutionContextController.testRunFinished = true;
    }
}
