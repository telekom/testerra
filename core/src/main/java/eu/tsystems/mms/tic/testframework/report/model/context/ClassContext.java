/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.report.model.context;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.SkipException;

/**
 * Holds the informations of a test class.
 *
 * @author pele
 */
public class ClassContext extends AbstractContext implements SynchronizableContext, Loggable {

    /**
     * @deprecated Use {@link #readMethodContexts()} instead
     */
    @Deprecated
    public final Queue<MethodContext> methodContexts = new ConcurrentLinkedQueue<>();
    private Class testClass;
    private TestClassContext testClassContext = null;

    public ClassContext(Class testClass, TestContext testContext) {
        this.testClass = testClass;
        this.parentContext = testContext;
    }

    @Deprecated
    public String getFullClassName() {
        return getTestClass().getName();
    }

    @Deprecated
    public String getSimpleClassName() {
        return getTestClass().getSimpleName();
    }

    /**
     * @deprecated Use {@link #readMethodContexts()} instead
     */
    public Collection<MethodContext> getMethodContexts() {
        return methodContexts;
    }

    public Stream<MethodContext> readMethodContexts() {
        return this.methodContexts.stream();
    }

    public ClassContext setTestClassContext(TestClassContext testContext) {
        this.testClassContext = testContext;
        return this;
    }

    public TestClassContext getTestClassContext() {
        return this.testClassContext;
    }

    public TestContext getTestContext() {
        return (TestContext)this.parentContext;
    }

    public Class getTestClass() {
        return testClass;
    }

    public MethodContext findTestMethodContainer(String methodName) {
        return getOrCreateContext(methodContexts, methodName, null, null);
    }

    public MethodContext getMethodContext(ITestResult testResult, ITestContext iTestContext, IInvokedMethod invokedMethod) {
        final Object[] parameters = testResult.getParameters();
        final ITestNGMethod testMethod = TestNGHelper.getTestMethod(testResult, iTestContext, invokedMethod);
        return this.getMethodContext(testResult, iTestContext, testMethod, parameters);
    }

    public MethodContext getMethodContext(ITestResult testResult, ITestContext iTestContext, ITestNGMethod iTestNGMethod, Object[] parameters) {
        final String name = iTestNGMethod.getMethodName();

        final List<Object> parametersList = Arrays.stream(parameters).collect(Collectors.toList());

        Optional<MethodContext> found;

        if (testResult != null) {
            found = methodContexts.stream()
                    .filter(mc -> testResult == mc.testResult)
                    .findFirst();
        } else {
            // TODO: (!!!!) this is not eindeutig
            found = methodContexts.stream()
                    .filter(mc -> iTestContext == mc.iTestContext)
                    .filter(mc -> iTestNGMethod == mc.iTestNgMethod)
                    .filter(mc -> mc.parameters.containsAll(parametersList))
                    .findFirst();
        }

        MethodContext methodContext;
        if (!found.isPresent()) {
            MethodContext.Type methodType;

            if (iTestNGMethod.isTest()) {
                methodType = MethodContext.Type.TEST_METHOD;
            } else {
                methodType = MethodContext.Type.CONFIGURATION_METHOD;
            }

            methodContext = new MethodContext(name, methodType, this);
            fillBasicContextValues(methodContext, this, name);

            methodContext.testResult = testResult;
            methodContext.iTestContext = iTestContext;
            methodContext.iTestNgMethod = iTestNGMethod;

                /*
                enhance swi with parameters, set parameters into context
                 */
            if (parameters.length > 0) {
                methodContext.parameters = Arrays.stream(parameters).map(o -> o == null ? "" : o.toString()).collect(Collectors.toList());
                String swiSuffix = methodContext.parameters.stream().map(Object::toString).collect(Collectors.joining("_"));
                methodContext.swi += "_" + swiSuffix;
            }

                /*
                link to merged context
                 */
//            if (isMerged()) {
//                mergedIntoClassContext.methodContexts.add(methodContext);
//            }

            // also check for annotations
            Method method = iTestNGMethod.getConstructorOrMethod().getMethod();
            if (method.isAnnotationPresent(FailureCorridor.High.class)) {
                methodContext.failureCorridorValue = FailureCorridor.Value.HIGH;
            } else if (method.isAnnotationPresent(FailureCorridor.Mid.class)) {
                methodContext.failureCorridorValue = FailureCorridor.Value.MID;
            } else if (method.isAnnotationPresent(FailureCorridor.Low.class)) {
                methodContext.failureCorridorValue = FailureCorridor.Value.LOW;
            }

            /*
            add to method contexts
             */
            methodContexts.add(methodContext);

            EventBus eventBus = TesterraListener.getEventBus();
            eventBus.post(new ContextUpdateEvent().setContext(methodContext));
            eventBus.post(new ContextUpdateEvent().setContext(this));
        } else {
            methodContext = found.get();
        }
        return methodContext;
    }

    public MethodContext safeAddSkipMethod(ITestResult testResult, IInvokedMethod invokedMethod) {
        MethodContext methodContext = getMethodContext(testResult, testResult.getTestContext(), invokedMethod);
        methodContext.getErrorContext().setThrowable(null, new SkipException("Skipped"));
        methodContext.status = TestStatusController.Status.SKIPPED;
        return methodContext;
    }

    @Override
    public TestStatusController.Status getStatus() {
        return getStatusFromContexts(getRepresentationalMethods());
    }

    public Stream<MethodContext> getRepresentationalMethods() {
        return methodContexts.stream().filter(MethodContext::isRepresentationalTestMethod);
        //        AbstractContext[] contexts = methodContexts.stream().filter(MethodContext::isRepresentationalTestMethod);
        //        return contexts;
    }

    @Deprecated
    public Map<TestStatusController.Status, Integer> getMethodStats(boolean includeTestMethods, boolean includeConfigMethods) {
        Map<TestStatusController.Status, Integer> counts = new LinkedHashMap<>();

        // initialize with 0
        Arrays.stream(TestStatusController.Status.values()).forEach(status -> counts.put(status, 0));

        methodContexts.stream().filter(mc -> (includeTestMethods && mc.isTestMethod()) || (includeConfigMethods && mc.isConfigMethod()))
                .forEach(methodContext -> {
                    TestStatusController.Status status = methodContext.getStatus();
                    int value = 0;
                    if (counts.containsKey(status)) {
                        value = counts.get(status);
                    }

                    counts.put(status, value + 1);
                });

        return counts;
    }

    /**
     * Use in methodsDashboard.vm only
     */
    @Deprecated
    public List<MethodContext> getTestMethodsWithStatus(TestStatusController.Status status) {
        List<MethodContext> methodContexts = new LinkedList<>();
        this.methodContexts.forEach(methodContext -> {
            if (methodContext.isTestMethod() && status == methodContext.status) {
                methodContexts.add(methodContext);
            }
        });
        return methodContexts;
    }

    protected void setExplicitName() {
        this.name = getTestClass().getSimpleName() + "_" + getTestContext().getSuiteContext().getName() + "_" + getTestContext().getName();
    }

}
