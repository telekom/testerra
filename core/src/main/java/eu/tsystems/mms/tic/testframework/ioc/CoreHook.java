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
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultCollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultOptionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.ThrowingAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.HandleCollectedAssertsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodContextUpdateWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodEndWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.RemoveTestMethodIfRetryPassedWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodParametersWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodStartWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.OmitInDevelopmentMethodInterceptor;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.SortMethodsByPriorityMethodInterceptor;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.internal.IdGenerator;
import eu.tsystems.mms.tic.testframework.internal.SequenceIdGenerator;
import eu.tsystems.mms.tic.testframework.report.DefaultReport;
import eu.tsystems.mms.tic.testframework.report.ExecutionEndListener;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.utils.DefaultExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.DefaultTestNGContextGenerator;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGContextNameGenerator;
import eu.tsystems.mms.tic.testframework.testing.DefaultTestController;
import eu.tsystems.mms.tic.testframework.testing.DefaultTestControllerOverrides;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.utils.DefaultFormatter;
import eu.tsystems.mms.tic.testframework.utils.Formatter;

public class CoreHook extends AbstractModule implements ModuleHook, PropertyManagerProvider {

    @Override
    protected void configure() {
        // Singletons
        bind(Report.class).to(DefaultReport.class).in(Scopes.SINGLETON);
        bind(Formatter.class).to(DefaultFormatter.class).in(Scopes.SINGLETON);
        bind(CollectedAssertion.class).to(DefaultCollectedAssertion.class).in(Scopes.SINGLETON);
        bind(OptionalAssertion.class).to(DefaultOptionalAssertion.class).in(Scopes.SINGLETON);
        bind(InstantAssertion.class).to(ThrowingAssertion.class).in(Scopes.SINGLETON);
        bind(TestController.Overrides.class).to(DefaultTestControllerOverrides.class).in(Scopes.SINGLETON);
        bind(TestController.class).to(DefaultTestController.class).in(Scopes.SINGLETON);
        bind(TestNGContextNameGenerator.class).to(DefaultTestNGContextGenerator.class).in(Scopes.SINGLETON);
        bind(IdGenerator.class).to(SequenceIdGenerator.class).in(Scopes.SINGLETON);
        bind(IExecutionContextController.class).to(DefaultExecutionContextController.class).in(Scopes.SINGLETON);
    }

    @Override
    public void init() {
        EventBus eventBus = Testerra.getEventBus();

        eventBus.register(new MethodStartWorker());
        eventBus.register(new MethodParametersWorker());
        eventBus.register(new HandleCollectedAssertsWorker());// !! must be invoked before MethodAnnotationCheckerWorker
        eventBus.register(new MethodContextUpdateWorker());
        eventBus.register(new RemoveTestMethodIfRetryPassedWorker());
        eventBus.register(new MethodEndWorker());
        eventBus.register(new OmitInDevelopmentMethodInterceptor());
        eventBus.register(new SortMethodsByPriorityMethodInterceptor());
        eventBus.register(new ExecutionEndListener());
        eventBus.register(PROPERTY_MANAGER);
    }

    @Override
    public void terminate() {

    }
}
