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

package eu.tsystems.mms.tic.testframework.ioc;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.HandleCollectedAssertsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodAnnotationCheckerWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodContextUpdateWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodEndWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.RemoveTestMethodIfRetryPassedWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodParametersWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodStartWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.OmitInDevelopmentMethodInterceptor;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.SortMethodsByPriorityMethodInterceptor;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.report.ExecutionEndListener;

public class CoreHook implements ModuleHook {
    @Override
    public void init() {
        EventBus eventBus = Testerra.getEventBus();

        eventBus.register(new MethodStartWorker());
        eventBus.register(new MethodParametersWorker());
        eventBus.register(new HandleCollectedAssertsWorker());// !! must be invoked before MethodAnnotationCheckerWorker
        eventBus.register(new MethodAnnotationCheckerWorker()); // !! must be invoked before Container Update
        eventBus.register(new MethodContextUpdateWorker());
        eventBus.register(new RemoveTestMethodIfRetryPassedWorker());
        eventBus.register(new MethodEndWorker());
        eventBus.register(new OmitInDevelopmentMethodInterceptor());
        eventBus.register(new SortMethodsByPriorityMethodInterceptor());
        eventBus.register(new ExecutionEndListener());
    }

    @Override
    public void terminate() {

    }
}
