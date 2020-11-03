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
import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    public final Queue<MethodContext> methodContexts = new ConcurrentLinkedQueue<>();
    public String fullClassName;
    public String simpleClassName;
    public final TestContextModel testContextModel;
    public final ExecutionContext executionContext;
    public TestContext testContext = null;
    public boolean merged = false;
    public ClassContext mergedIntoClassContext = null;

    public ClassContext(TestContextModel testContextModel, ExecutionContext executionContext) {
        this.parentContext = this.testContextModel = testContextModel;
        this.executionContext = executionContext;
    }

    public MethodContext findTestMethodContainer(String methodName) {
        return getOrCreateContext(MethodContext.class, methodContexts, methodName, null, null);
    }

    public MethodContext getMethodContext(ITestResult testResult, ITestContext iTestContext, IInvokedMethod invokedMethod) {
        final Object[] parameters = testResult.getParameters();
        final ITestNGMethod testMethod = TestNGHelper.getTestMethod(testResult, iTestContext, invokedMethod);
        return this.getMethodContext(testResult, iTestContext, testMethod, parameters);
    }

    public MethodContext getMethodContext(ITestResult testResult, ITestContext iTestContext, ITestNGMethod iTestNGMethod, Object[] parameters) {
        final String name = iTestNGMethod.getMethodName();

        final List<Object> parametersList = Arrays.stream(parameters).collect(Collectors.toList());

        List<MethodContext> collect;

        if (testResult != null) {
            collect = methodContexts.stream()
                    .filter(mc -> testResult == mc.testResult)
                    .collect(Collectors.toList());
        } else {
            // TODO: (!!!!) this is not eindeutig
            collect = methodContexts.stream()
                    .filter(mc -> iTestContext == mc.iTestContext)
                    .filter(mc -> iTestNGMethod == mc.iTestNgMethod)
                    .filter(mc -> mc.parameters.containsAll(parametersList))
                    .collect(Collectors.toList());
        }

        MethodContext methodContext;
        if (collect.isEmpty()) {
                /*
                create new one
                 */
            MethodContext.Type methodType;

            final boolean isTest;
            if (iTestNGMethod != null) {
                isTest = iTestNGMethod.isTest();
            } else {
                throw new SystemException("Error getting method infos, seems like a TestNG bug.\n" + String.join("\n", iTestNGMethod.toString(), iTestContext.toString()));
            }

            if (isTest) {
                methodType = MethodContext.Type.TEST_METHOD;
            } else {
                methodType = MethodContext.Type.CONFIGURATION_METHOD;
            }

            TestContextModel correctTestContextModel = testContextModel;
            SuiteContext correctSuiteContext = testContextModel.suiteContext;
            if (merged) {
                correctSuiteContext = executionContext.getSuiteContext(iTestContext);
                correctTestContextModel = correctSuiteContext.getTestContext(iTestContext);
            }

            methodContext = new MethodContext(name, methodType, this, correctTestContextModel, correctSuiteContext, executionContext);
            fillBasicContextValues(methodContext, this, name);

            methodContext.testResult = testResult;
            methodContext.iTestContext = iTestContext;
            methodContext.iTestNgMethod = iTestNGMethod;

                /*
                enhance swi with parameters, set parameters into context
                 */
            if (parameters.length > 0) {
                methodContext.parameters = Arrays.stream(parameters).map(Object::toString).collect(Collectors.toList());
                String swiSuffix = methodContext.parameters.stream().map(Object::toString).collect(Collectors.joining("_"));
                methodContext.swi += "_" + swiSuffix;
            }

                /*
                link to merged context
                 */
            if (merged) {
                mergedIntoClassContext.methodContexts.add(methodContext);
            }

                /*
                also check for annotations
                 */
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
            if (collect.size() > 1) {
                log().error("INTERNAL ERROR: Found " + collect.size() + " " + MethodContext.class.getSimpleName() + "s with name " + name + ", picking first one");
            }
            methodContext = collect.get(0);
        }
        return methodContext;
    }

    public MethodContext safeAddSkipMethod(ITestResult testResult, IInvokedMethod invokedMethod) {
        MethodContext methodContext = getMethodContext(testResult, testResult.getTestContext(), invokedMethod);
        methodContext.errorContext().setThrowable(null, new SkipException("Skipped"));
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

    public Map<TestStatusController.Status, Integer> getMethodStats(boolean includeTestMethods, boolean includeConfigMethods) {
        Map<TestStatusController.Status, Integer> counts = new LinkedHashMap<>();

        // initialize with 0
        Arrays.stream(TestStatusController.Status.values()).forEach(status -> counts.put(status, 0));

        methodContexts.stream().filter(mc -> (includeTestMethods && mc.isTestMethod()) || (includeConfigMethods && mc.isConfigMethod())).forEach(methodContext -> {
            TestStatusController.Status status = methodContext.getStatus();
            int value = 0;
            if (counts.containsKey(status)) {
                value = counts.get(status);
            }

            counts.put(status, value + 1);
        });

        return counts;
    }

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
        name = simpleClassName + "_" + testContextModel.suiteContext.name + "_" + testContextModel.name;
    }

}
