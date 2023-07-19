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
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultAssertionWrapper;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultCollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultOptionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.ThrowingAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodContextUpdateWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodParametersWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodStartWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.SortMethodsByPriorityMethodInterceptor;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.internal.IdGenerator;
import eu.tsystems.mms.tic.testframework.internal.SequenceIdGenerator;
import eu.tsystems.mms.tic.testframework.report.DefaultReport;
import eu.tsystems.mms.tic.testframework.report.ExecutionEndListener;
import eu.tsystems.mms.tic.testframework.report.FailsAnnotationConverter;
import eu.tsystems.mms.tic.testframework.report.ITestStatusController;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.TestAnnotationConverter;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.utils.DefaultExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.DefaultTestNGContextGenerator;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGContextNameGenerator;
import eu.tsystems.mms.tic.testframework.testing.DefaultTestController;
import eu.tsystems.mms.tic.testframework.testing.DefaultTestControllerOverrides;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.utils.DefaultExecutionUtils;
import eu.tsystems.mms.tic.testframework.utils.DefaultFormatter;
import eu.tsystems.mms.tic.testframework.utils.ExecutionUtils;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import org.testng.annotations.Test;

public class CoreHook extends AbstractModule implements ModuleHook, PropertyManagerProvider {

    @Override
    protected void configure() {
        // Singletons
        bind(Report.class).to(DefaultReport.class).in(Scopes.SINGLETON);
        bind(Formatter.class).to(DefaultFormatter.class).in(Scopes.SINGLETON);
        bind(CollectedAssertion.class).to(DefaultCollectedAssertion.class).in(Scopes.SINGLETON);
        bind(OptionalAssertion.class).to(DefaultOptionalAssertion.class).in(Scopes.SINGLETON);
        bind(InstantAssertion.class).to(ThrowingAssertion.class).in(Scopes.SINGLETON);
        bind(Assertion.class).to(DefaultAssertionWrapper.class).in(Scopes.SINGLETON);
        bind(TestController.Overrides.class).to(DefaultTestControllerOverrides.class).in(Scopes.SINGLETON);
        bind(TestController.class).to(DefaultTestController.class).in(Scopes.SINGLETON);
        bind(TestNGContextNameGenerator.class).to(DefaultTestNGContextGenerator.class).in(Scopes.SINGLETON);
        bind(IdGenerator.class).to(SequenceIdGenerator.class).in(Scopes.SINGLETON);
        bind(IExecutionContextController.class).to(DefaultExecutionContextController.class).in(Scopes.SINGLETON);
        bind(ExecutionUtils.class).to(DefaultExecutionUtils.class).in(Scopes.SINGLETON);
        bind(ITestStatusController.class).to(TestStatusController.class).in(Scopes.SINGLETON);
    }

    @Override
    public void init() {

        Report report = Testerra.getInjector().getInstance(Report.class);
        report.registerAnnotationConverter(Fails.class, new FailsAnnotationConverter());
        report.registerAnnotationConverter(Test.class, new TestAnnotationConverter());

        EventBus eventBus = Testerra.getEventBus();

        eventBus.register(new MethodStartWorker());
        eventBus.register(new MethodParametersWorker());
        eventBus.register(new MethodContextUpdateWorker());
        eventBus.register(new SortMethodsByPriorityMethodInterceptor());
        eventBus.register(new ExecutionEndListener());
        eventBus.register(PROPERTY_MANAGER);

        ITestStatusController testStatusController = Testerra.getInjector().getInstance(ITestStatusController.class);
        eventBus.register(testStatusController);
    }

    @Override
    public void terminate() {

    }
}
