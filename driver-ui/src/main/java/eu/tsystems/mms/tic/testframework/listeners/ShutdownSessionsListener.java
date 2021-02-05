/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.listeners;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.ExecutionAbortEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.hooks.DriverUiHook;
import eu.tsystems.mms.tic.testframework.logging.Loggable;

public class ShutdownSessionsListener implements
        ExecutionFinishEvent.Listener,
        ExecutionAbortEvent.Listener,
        Loggable
{
    @Override
    @Subscribe
    public void onExecutionAbort(ExecutionAbortEvent event) {
        DriverUiHook.shutdownModule();
    }

    @Override
    @Subscribe
    public void onExecutionFinish(ExecutionFinishEvent event) {
        DriverUiHook.shutdownModule();
    }
}
