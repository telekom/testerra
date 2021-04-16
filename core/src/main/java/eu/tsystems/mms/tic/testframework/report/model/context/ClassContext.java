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
 */

package eu.tsystems.mms.tic.testframework.report.model.context;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
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
        this.name = testClass.getSimpleName();
    }

    @Override
    public String getName() {
        return getTestClassContext().map(TestClassContext::name).orElseGet(super::getName);
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
        if (testContext.name().trim().length() > 0) {
            this.testClassContext = testContext;
        }
        return this;
    }

    public Optional<TestClassContext> getTestClassContext() {
        return Optional.ofNullable(this.testClassContext);
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

    public MethodContext getMethodContext(ITestResult testResult) {
        return getMethodContext(testResult, testResult.getTestContext(), testResult.getMethod(), testResult.getParameters());
    }

    public MethodContext getMethodContext(ITestContext testContext,
                                          ITestNGMethod testNGMethod,
                                          Object[] parameters) {
        return getMethodContext(null, testContext, testNGMethod, parameters);
    }

    private MethodContext getMethodContext(
            ITestResult testResult,
            ITestContext testContext,
            ITestNGMethod testNGMethod,
            Object[] parameters
    ) {
        final List<Object> parametersList = Arrays.stream(parameters).collect(Collectors.toList());

        Optional<MethodContext> found;
        String methodContextName;


        if (testResult != null) {
            found = methodContexts.stream()
                    .filter(methodContext -> methodContext.getTestNgResult().isPresent())
                    .filter(methodContext -> testResult == methodContext.getTestNgResult().get())
                    .findFirst();
            methodContextName = contextNameGenerator.getMethodContextName(testResult);
        } else {
            // TODO: (!!!!) this is not eindeutig
            found = methodContexts.stream()
                    .filter(methodContext -> methodContext.getTestNgResult().isPresent())
                    .filter(methodContext -> {
                        ITestResult iTestResult = methodContext.getTestNgResult().get();
                        return testContext == iTestResult.getTestContext() && testNGMethod == iTestResult.getMethod();
                    })
                    .filter(methodContext -> methodContext.getParameterValues().containsAll(parametersList))
                    .findFirst();

            methodContextName = contextNameGenerator.getMethodContextName(testContext, testNGMethod, parameters);
        }

        MethodContext methodContext;
        if (!found.isPresent()) {
            MethodContext.Type methodType;

            if (testNGMethod.isTest()) {
                methodType = MethodContext.Type.TEST_METHOD;
            } else {
                methodType = MethodContext.Type.CONFIGURATION_METHOD;
            }

            methodContext = new MethodContext(methodContextName, methodType, this);
            //methodContext.name = name;
            //fillBasicContextValues(methodContext, this, name);

            methodContext.setTestNgResult(testResult);
            methodContext.setParameterValues(testResult.getParameters());
//
//            if (parameters.length > 0) {
//                methodContext.parameters = Arrays.stream(parameters).map(o -> o == null ? "" : o.toString()).collect(Collectors.toList());
//            }

                /*
                link to merged context
                 */
//            if (isMerged()) {
//                mergedIntoClassContext.methodContexts.add(methodContext);
//            }

            // also check for annotations
            Method method = testNGMethod.getConstructorOrMethod().getMethod();
            if (method.isAnnotationPresent(FailureCorridor.High.class)) {
                methodContext.setFailureCorridorClass(FailureCorridor.High.class);
            } else if (method.isAnnotationPresent(FailureCorridor.Mid.class)) {
                methodContext.setFailureCorridorClass(FailureCorridor.Mid.class);
            } else if (method.isAnnotationPresent(FailureCorridor.Low.class)) {
                methodContext.setFailureCorridorClass(FailureCorridor.Low.class);
            }

            /*
            add to method contexts
             */
            methodContexts.add(methodContext);

            EventBus eventBus = Testerra.getEventBus();
            eventBus.post(new ContextUpdateEvent().setContext(methodContext));
            eventBus.post(new ContextUpdateEvent().setContext(this));
        } else {
            methodContext = found.get();
        }
        return methodContext;
    }

    public MethodContext safeAddSkipMethod(ITestResult testResult) {
        MethodContext methodContext = getMethodContext(testResult);
        methodContext.getErrorContext().setThrowable(new SkipException("Skipped"));
        methodContext.setStatus(TestStatusController.Status.SKIPPED);
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

    public Map<TestStatusController.Status, Integer> createStats() {
        Map<TestStatusController.Status, Integer> counts = new LinkedHashMap<>();
        Arrays.stream(TestStatusController.Status.values()).forEach(status -> counts.put(status, 0));
        return counts;
    }

    public void addToStats(Map<TestStatusController.Status, Integer> stats, MethodContext methodContext) {
        TestStatusController.Status status = methodContext.getStatus();
        int value = stats.getOrDefault(status,0);
        stats.put(status, value + 1);
    }

    /**
     * Used in dashboard.vm and methodsDashboard.vm
     */
    @Deprecated
    public Map<TestStatusController.Status, Integer> getMethodStats(boolean includeTestMethods, boolean includeConfigMethods) {
        Map<TestStatusController.Status, Integer> counts = createStats();

        methodContexts.stream()
                .filter(mc -> (includeTestMethods && mc.isTestMethod()) || (includeConfigMethods && mc.isConfigMethod()))
                .forEach(methodContext -> {
                    addToStats(counts, methodContext);
                });

        return counts;
    }

    /**
     * Used in methodsDashboard.vm only
     */
    @Deprecated
    public List<MethodContext> getTestMethodsWithStatus(TestStatusController.Status status) {
        List<MethodContext> methodContexts = new LinkedList<>();
        this.methodContexts.forEach(methodContext -> {
            if (methodContext.isTestMethod() && status == methodContext.getStatus()) {
                methodContexts.add(methodContext);
            }
        });
        return methodContexts;
    }

    @Deprecated
    public void updateMultiContextualName() {
        TestContext testContext = getTestContext();
        this.name = this.getName() + "_" + testContext.getSuiteContext().getName() + "_" + testContext.getName();
    }
}
