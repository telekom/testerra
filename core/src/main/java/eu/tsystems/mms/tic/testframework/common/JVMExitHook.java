/*
 * Testerra
 *
 * (C) 2024, Clemens Große, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.common;

import eu.tsystems.mms.tic.testframework.events.ExecutionAbortEvent;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

/**
 * triggers ExecutionAbortEvent in case of none existing report
 * missing report is indicator of unexpected system.exit and reason to trigger report generation
 */
public class JVMExitHook extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(JVMExitHook.class);

    public void run(){

        final IExecutionContextController contextController = Testerra.getInjector().getInstance(IExecutionContextController.class);
        final ExecutionContext executionContext = contextController.getExecutionContext();

        // trigger report generation only when no report exists, as shutdown hook is always executed no matter if normal finish or abortion
        if (!executionContext.getReportModelGenerated()) {
            LOGGER.info("Triggering report generation after unexpected abortion of test execution.");
            final EventBus eventBus = Testerra.getEventBus();
            eventBus.post(new ExecutionAbortEvent());
        }
    }
}
