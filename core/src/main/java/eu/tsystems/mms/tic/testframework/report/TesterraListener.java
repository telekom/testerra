/*
 * Testerra
 *
 * (C) 2020,  Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.AbstractMethodEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.events.InterceptMethodsEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.events.MethodStartEvent;
import eu.tsystems.mms.tic.testframework.events.TestStatusUpdateEvent;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodEndWorker;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.hooks.ConfigMethodHook;
import eu.tsystems.mms.tic.testframework.report.hooks.TestMethodHook;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.apache.logging.log4j.core.LoggerContext;
import org.testng.IConfigurable;
import org.testng.IConfigurationListener;
import org.testng.IConfigureCallBack;
import org.testng.IDataProviderListener;
import org.testng.IDataProviderMethod;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.Test;
import org.testng.internal.ConfigurationMethod;
import org.testng.internal.ConstructorOrMethod;
import org.testng.internal.NoOpTestClass;
import org.testng.internal.TestResult;
import org.testng.internal.annotations.DefaultAnnotationTransformer;
import org.testng.internal.annotations.IAnnotationFinder;
import org.testng.internal.invokers.InvokedMethod;
import org.testng.internal.objects.DefaultTestObjectFactory;
import org.testng.xml.XmlSuite;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Testerra listener based on TestNG for whole test execution.
 */
public class TesterraListener implements
        IInvokedMethodListener,
        IReporter,
        IHookable,
        IConfigurable,
        IMethodInterceptor,
        ITestListener,
        ISuiteListener,
        Loggable,
        IDataProviderListener,
        IConfigurationListener {
    /**
     * Default package namespace used in ClassFinder and for initializing of modules.
     * <p>
     * Additional modules have to use
     */
    public static final String[] DEFAULT_PACKAGES = {"eu.tsystems.mms.tic", "io.testerra"};

    /**
     * Skip test methods control.
     */
    private static boolean skipAllMethods = false;

    /**
     * Instance counter for this reporter. *
     */
    private static int instances = 0;
    private static final Object LOCK = new Object();
    private static final ConcurrentHashMap<ITestNGMethod, Boolean> dataProviderSemaphore = new ConcurrentHashMap<>();

    /**
     * @deprecated Use {@link Testerra#getEventBus()}
     */
    public static EventBus getEventBus() {
        return Testerra.getEventBus();
    }

    /**
     * @deprecated Use {@link Testerra#getLoggerContext()}
     */
    public static LoggerContext getLoggerContext() {
        return Testerra.getLoggerContext();
    }

    /**
     * @deprecated Use {@link Testerra#getInjector()}
     */
    public static Report getReport() {
        return Testerra.getInjector().getInstance(Report.class);
    }

    /**
     * @deprecated Use {@link Testerra#getInjector()}
     */
    public static ITestStatusController getTestStatusController() {
        return Testerra.getInjector().getInstance(ITestStatusController.class);
    }

    static {
        /*
         * Add monitoring event listeners
         */
        JVMMonitor.label("Start");

        // start memory monitor
        JVMMonitor.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            JVMMonitor.stop();
        }));
    }

    /**
     * Default constructor. *
     */
    public TesterraListener() {
        synchronized (LOCK) {
            // increment instance counter
            instances++;

            if (instances == 1) {
                EventBus eventBus = Testerra.getEventBus();
                // this is the last worker to be called
                eventBus.register(new MethodEndWorker());
                // The finalize listener has to be registered AFTER all modules ONCE
                eventBus.register(new FinalizeListener());
            }
        }
    }

    /*
     * TESTNG
     */

    /**
     * This method INTERCEPTs THE XML_TEST not the methods
     * It is possible to filter methods an remove them completely from execution
     * Or you do a dependency analysis for execution filter
     *
     * @param list All methods that should be run due to current XML-Test
     * @param iTestContext .
     * @return All methods that should be executed
     */
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> list, final ITestContext iTestContext) {
        InterceptMethodsEvent event = new InterceptMethodsEvent()
                .setMethodInstances(list)
                .setTestContext(iTestContext);
        Testerra.getEventBus().post(event);
        return event.getMethodInstances();
    }

    /**
     * @param method .
     * @param testResult .
     */
    @Override
    public void beforeInvocation(final IInvokedMethod method, final ITestResult testResult) {
        // we don't need that-
    }

    /**
     * @param method .
     * @param method .
     * @param testResult .
     */
    @Override
    public void afterInvocation(final IInvokedMethod method, final ITestResult testResult) {
        // we don't need that-
    }

    /**
     * Override before invocation, to visualize threads.
     *
     * @param method invoked method.
     * @param testResult result of invoked method.
     * @param context steps of test.
     */
    @Override
    public void beforeInvocation(
            IInvokedMethod method,
            ITestResult testResult,
            ITestContext context
    ) {
        try {
            pBeforeInvocation(method, testResult, context);
        } catch (Throwable t) {
            log().error("FATAL INTERNAL ERROR in beforeInvocation for " + method + ", " + testResult + ", " + context, t);
            //ReportInfo.getDashboardWarning().addInfo(1, "FATAL INTERNAL ERROR during execution! Please analyze the build logs for this error!");
        }
    }

    /**
     * Override before invocation, to visualize threads.
     *
     * @param invokedMethod invoked method.
     * @param testResult result of invoked method.
     * @param testContext
     */
    private MethodContext pBeforeInvocation(
            IInvokedMethod invokedMethod,
            ITestResult testResult,
            ITestContext testContext
    ) {
        final String methodName = getMethodName(testResult);

        // stores the actual testresult, auto-creates the method context
        MethodContext methodContext = ExecutionContextController.setCurrentMethodContext(testResult);

        methodContext.getTestStep(TestStep.SETUP);

//        final String infoText = "beforeInvocation: " + invokedMethod.getTestMethod().getTestClass().getName() + "." +
//                methodName +
//                " - " + Thread.currentThread().getName();
//
//        log().trace(infoText);

        AbstractMethodEvent event = new MethodStartEvent()
                .setTestResult(testResult)
                .setInvokedMethod(invokedMethod)
                .setMethodName(methodName)
                .setTestContext(testContext)
                .setMethodContext(methodContext);

        Testerra.getEventBus().post(event);

        // We don't close teardown steps, because we want to collect further actions there
        //step.close();
        return methodContext;
    }

    /**
     * Override after invocation, to visualize threads and finish reporting.
     *
     * @param method invoked method.
     * @param testResult result of invoked method.
     * @param context steps of test.
     */
    @Override
    public void afterInvocation(
            IInvokedMethod method,
            ITestResult testResult,
            ITestContext context
    ) {
        pAfterInvocation(method, testResult, context);
    }

    private static String getMethodName(ITestResult testResult) {
        ITestNGMethod testMethod = testResult.getMethod();
        String methodName = testMethod.getMethodName();
        Object[] parameters = testResult.getParameters();
        if (parameters != null) {
            methodName += "(";
            for (Object parameter : parameters) {
                methodName += parameter + ", ";
            }
            if (parameters.length > 0) {
                methodName = methodName.substring(0, methodName.length() - 2);
            }
            methodName += ")";
        }
        return methodName;
    }

    /**
     * Override after invocation, to visualize threads and finish reporting.
     *
     * @param invokedMethod invoked method.
     * @param testResult result of invoked method.
     * @param testContext steps of test.
     */
    private void pAfterInvocation(
            IInvokedMethod invokedMethod,
            ITestResult testResult,
            ITestContext testContext
    ) {

        final String methodName = getMethodName(testResult);

        Optional<MethodContext> optionalMethodContext = ExecutionContextController.getMethodContextForThread();
        MethodContext methodContext;

        if (!optionalMethodContext.isPresent()) {
            if (testResult.getStatus() == ITestResult.CREATED || testResult.getStatus() == ITestResult.SKIP) {
                /*
                 * TestNG bug or whatever ?!?!
                 */
                ClassContext classContext = ExecutionContextController.getClassContextFromTestResult(testResult);
                methodContext = classContext.safeAddSkipMethod(testResult);
            }
//            else {
//                throw new SystemException("INTERNAL ERROR. Could not create methodContext for " + methodName + " with result: " + testResult);
//            }
        } else {
            methodContext = optionalMethodContext.get();
        }

        AbstractMethodEvent event = new MethodEndEvent()
                .setTestResult(testResult)
                .setInvokedMethod(invokedMethod)
                .setMethodName(methodName)
                .setTestContext(testContext)
                .setMethodContext(methodContext);

        Testerra.getEventBus().post(event);

        methodContext.getTestStep(TestStep.TEARDOWN);
    }

    @Override
    public void generateReport(
            List<XmlSuite> xmlSuites,
            List<ISuite> suites,
            String outputDirectory
    ) {
        ExecutionFinishEvent event = new ExecutionFinishEvent()
                .setSuites(suites)
                .setXmlSuites(xmlSuites);
        Testerra.getEventBus().post(event);
    }

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        TestMethodHook.runHook(callBack, testResult);
    }

    @Override
    public void run(IConfigureCallBack callBack, ITestResult testResult) {
        ConfigMethodHook.runHook(callBack, testResult);
    }

    public static boolean isSkipAllMethods() {
        return skipAllMethods;
    }

    public static void skipAllTests() {
        skipAllMethods = true;
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {

    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {

    }

    /**
     * This is only a fallback when 'afterInvocation was not called.' This could happen when TestNG runs into an exception, e.g.
     * the Test method points to a non-existing dataprovider.
     * Therefor the events will only post if the corresponding MethodContext has no status.
     * <p>
     * This method is also called when data provider failed. This is excluded, see {@link #onDataProviderFailure(ITestNGMethod, ITestContext, RuntimeException)}
     */
    @Override
    public void onTestFailure(ITestResult iTestResult) {
        if (dataProviderSemaphore.containsKey(iTestResult.getMethod())) {
            return;
        }

        InvokedMethod invokedMethod = new InvokedMethod(new Date().getTime(), iTestResult);
        MethodContext methodContext = ExecutionContextController.getMethodContextFromTestResult(iTestResult);

        if (methodContext != null && methodContext.getStatus() == Status.NO_RUN) {
            AbstractMethodEvent event = new MethodEndEvent()
                    .setTestResult(iTestResult)
                    .setInvokedMethod(invokedMethod)
                    .setMethodName(getMethodName(iTestResult))
                    .setTestContext(iTestResult.getTestContext())
                    .setMethodContext(methodContext);
            Testerra.getEventBus().post(event);
            Testerra.getEventBus().post(new TestStatusUpdateEvent(methodContext));
        }
    }

    /**
     * This method gets not only called when
     * - a test was skipped using {@link Test#dependsOnMethods()} or by throwing a {@link SkipException},
     * - a data provider failed
     * - a failed test should not be retried by {@link RetryAnalyzer#retry(ITestResult)}.
     */
    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        if (dataProviderSemaphore.containsKey(iTestResult.getMethod())) {
            return;
        }

        if (!iTestResult.wasRetried()) {
            MethodContext methodContext = ExecutionContextController.getMethodContextFromTestResult(iTestResult);
            methodContext.setStatus(Status.SKIPPED);
            Testerra.getEventBus().post(new TestStatusUpdateEvent(methodContext));
        }
    }

    @Override
    public void onConfigurationSkip(ITestResult testResult) {
        MethodContext methodContext = ExecutionContextController.getMethodContextFromTestResult(testResult);
        methodContext.setStatus(Status.SKIPPED);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    @Override
    public void onStart(ITestContext iTestContext) {

    }

    @Override
    public void onFinish(ITestContext iTestContext) {

    }

    /**
     * This method runs, when SUITE starts!
     *
     * @param iSuite
     */
    @Override
    public void onStart(ISuite iSuite) {
        Testerra.getEventBus().post(iSuite);
    }

    /**
     * This method runs, when SUITE ends!
     *
     * @param iSuite
     */
    @Override
    public void onFinish(ISuite iSuite) {
        Testerra.getEventBus().post(iSuite);
    }

    public static boolean isActive() {
        return instances > 0;
    }

    /**
     * Generated data provider objects are stored for use in 'afterDataProviderExecutionÄ
     */
    private static final Map<String, IInvokedMethod> DP_INVOKED_METHODS = new ConcurrentHashMap<>();
    private static final Map<String, ITestResult> DP_TEST_RESULT = new ConcurrentHashMap<>();

    @Override
    public void beforeDataProviderExecution(IDataProviderMethod dataProviderMethod, ITestNGMethod testNGMethod, ITestContext testContext) {
        /**
         * TestNG calls the data provider initialization for every thread.
         * Added a semaphore to prevent adding multiple method contexts.
         */
        if (dataProviderSemaphore.containsKey(testNGMethod)) {
            return;
        }
        dataProviderSemaphore.put(testNGMethod, true);

        // Manually create a TestNG ConfigurationMethod and set the 'BeforeMethod' flavour
        IAnnotationFinder annoFinder = new DataProvAnnotationFinder(new DefaultAnnotationTransformer());
        ITestNGMethod dpConfigMethod = new ConfigurationMethod(
                new DefaultTestObjectFactory(),
                new ConstructorOrMethod(dataProviderMethod.getMethod()),
                annoFinder,
                false,
                false,
                false,
                false,
                false,
                false,
                true,
                false,
                false,
                new String[]{},
                new String[]{},
                testNGMethod.getXmlTest(),
                dataProviderMethod
        );

        // A data provider could also defined in a specific data provider class
        NoOpTestClass dpTestClass = new NoOpTestClass(testNGMethod.getTestClass());
        dpTestClass.setTestClass(dataProviderMethod.getMethod().getDeclaringClass());
        dpConfigMethod.setTestClass(dpTestClass);

        // Need full qualified TestResult
        TestResult testResult = TestResult.newContextAwareTestResult(dpConfigMethod, testContext);
        InvokedMethod invokedMethod = new InvokedMethod(new Date().getTime(), testResult);
        DP_TEST_RESULT.put(dataProviderMethod.getMethod().toString(), testResult);
        DP_INVOKED_METHODS.put(dataProviderMethod.getMethod().toString(), invokedMethod);

        pBeforeInvocation(invokedMethod, testResult, testContext);
    }

    @Override
    public void afterDataProviderExecution(IDataProviderMethod dataProviderMethod, ITestNGMethod testNGMethod, ITestContext testContext) {
        log().info("After dataprovider execution");

        IInvokedMethod invokedMethod = DP_INVOKED_METHODS.get(dataProviderMethod.getMethod().toString());
        ITestResult testResult = DP_TEST_RESULT.get(dataProviderMethod.getMethod().toString());
        testResult.setStatus(ITestResult.SUCCESS);
        pAfterInvocation(invokedMethod, testResult, testContext);
    }

    @Override
    public void onDataProviderFailure(ITestNGMethod testNGMethod, ITestContext testContext, RuntimeException exception) {

        // Finalize method context for data provider
        IInvokedMethod dpIinvokedMethod = DP_INVOKED_METHODS.get(testNGMethod.getDataProviderMethod().getMethod().toString());
        ITestResult dpTestResult = DP_TEST_RESULT.get(testNGMethod.getDataProviderMethod().getMethod().toString());
        dpTestResult.setStatus(ITestResult.FAILURE);
        dpTestResult.setThrowable(exception);
        pAfterInvocation(dpIinvokedMethod, dpTestResult, testContext);

//        if (!dataProviderSemaphore.containsKey(testNGMethod)) {

        // For the called test method a new method context is created
        ITestResult testResult = TestResult.newContextAwareTestResult(testNGMethod, testContext);
        IInvokedMethod invokedMethod = new InvokedMethod(new Date().getTime(), testResult);
        MethodContext methodContext = ExecutionContextController.getMethodContextFromTestResult(testResult);

        Throwable nestedThrowable = exception.getCause() != null ? exception.getCause() : exception;
        methodContext.addError(new SkipException("Method skipped because of failed data provider", nestedThrowable));

        // Data provider methods are a kind of setup methods. If they crash the test method will get the status SKIPPED
        methodContext.setStatus(Status.SKIPPED);
        testResult.setStatus(ITestResult.SKIP);
        pAfterInvocation(invokedMethod, testResult, testContext);

        // To prevent double method context, this map is needed
        dataProviderSemaphore.put(testNGMethod, true);
//        }
    }
}
