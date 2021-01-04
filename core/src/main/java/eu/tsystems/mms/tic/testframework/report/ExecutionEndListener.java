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

package eu.tsystems.mms.tic.testframework.report;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionAbortEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.internal.MethodRelations;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.util.Date;

/**
 * Listener for the end of the test execution.
 * It finalizes the {@link ExecutionContext} and posts the {@link FinalizeExecutionEvent}.
 * NOTE: It is package private to prevent miss usage.
 */
final class ExecutionEndListener implements
        ExecutionFinishEvent.Listener,
        ExecutionAbortEvent.Listener,
        Loggable
{
    @Override
    @Subscribe
    public void onExecutionAbort(ExecutionAbortEvent event) {
        ExecutionContextController.getCurrentExecutionContext().crashed = true;
        finalizeExecutionContext();
    }

    @Override
    @Subscribe
    public void onExecutionFinish(ExecutionFinishEvent event) {
        // set the testRunFinished flag
        finalizeExecutionContext();
    }

    private void finalizeExecutionContext() {
        MethodRelations.flushAll();

        ExecutionContext currentExecutionContext = ExecutionContextController.getCurrentExecutionContext();
        currentExecutionContext.updateEndTimeRecursive(new Date());

        EventBus eventBus = TesterraListener.getEventBus();

        eventBus.post(new ContextUpdateEvent().setContext(currentExecutionContext));
        eventBus.post(new FinalizeExecutionEvent().setExecutionContext(currentExecutionContext));
    }
}
