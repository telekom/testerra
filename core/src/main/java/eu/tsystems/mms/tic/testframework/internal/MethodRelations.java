/*
 * Testerra
 *
 * (C) 2025,  Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.TestContext;
import org.testng.IDataProviderMethod;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MethodRelations {

//    private static final Map<Long, List<MethodContext>> EXECUTION_CONTEXT = Collections.synchronizedMap(new HashMap<>());
//    private static final ThreadLocal<Boolean> TEST_WAS_HERE = new ThreadLocal<>();

    private static final Map<Method, MethodContext> dataProviderMap = new ConcurrentHashMap<>();

//    private static ThreadLocal<Map<ITestResult, ITestNGMethod>> beforeAfterConfigMap = new ThreadLocal<>();

    // Needed to allocate afterMethod contexts to related test methodContext
    private static ThreadLocal<MethodContext> currentTestMethodContext = new ThreadLocal<>();
    private static ThreadLocal<List<MethodContext>> currentBeforeMethods = new ThreadLocal<>();

    private MethodRelations() {

    }

//    /**
//     * This method stores all called configuration methods to handle relations.
//     * <p>
//     * Only at beforeMethod and afterMethod 'testNGMethod' contains the related testmethod.
//     *
//     * @param testResult ITestResult object of configuration method
//     * @param testNGMethod ITestNGMethod object of the test method
//     */
//    public static void addConfigMethod(ITestResult testResult, ITestNGMethod testNGMethod) {
//        if (testResult.getMethod().isBeforeMethodConfiguration() || testResult.getMethod().isAfterMethodConfiguration()) {
//            Map<ITestResult, ITestNGMethod> map = beforeAfterConfigMap.get();
//            if (map == null) {
//                map = new HashMap<>();
//                beforeAfterConfigMap.set(map);
//            }
//            map.put(testResult, testNGMethod);
//        }
//    }

//    public static Map<ITestResult, ITestNGMethod> getBeforeConfig() {
//        return beforeAfterConfigMap.get();
//    }

//    private static boolean isBeforeXXMethod(Method method) {
//        if (method.isAnnotationPresent(BeforeSuite.class) ||
//                method.isAnnotationPresent(BeforeClass.class) ||
//                method.isAnnotationPresent(BeforeGroups.class) ||
//                method.isAnnotationPresent(BeforeMethod.class) ||
//                method.isAnnotationPresent(BeforeTest.class)
//        ) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    private static long getThreadId() {
//        return Thread.currentThread().getId();
//    }

//    private static void createNewList() {
//        EXECUTION_CONTEXT.put(getThreadId(), new LinkedList<>());
//    }

    /**
     * This method is called from finalization of a MethodContext.
     * MethodContext can belong to a config or test method.
     */
    public static void announceRun(MethodContext methodContext) {
        if (methodContext.getTestNgResult().isEmpty()) {
            return;
        }
        Method method = methodContext.getTestNgResult().get().getMethod().getConstructorOrMethod().getMethod();
        ITestNGMethod testNGMethod = methodContext.getTestNgResult().get().getMethod();

        // store it for later processing
        addBeforeMethod(methodContext);
        if (methodContext.getMethodType() == MethodContext.Type.DATA_PROVIDER) {
            dataProviderMap.put(testNGMethod.getConstructorOrMethod().getMethod(), methodContext);
        }

        // all beforeXY methods
        checkForBeforeDependencies(methodContext);

        // all afterXY methods
        checkForAfterDependencies(methodContext);

        // dependsOnMethod, dependsOnGroup
        checkForMethodDependencies(testNGMethod, methodContext);
    }

    private static void addBeforeMethod(MethodContext methodContext) {
        if (methodContext.getMethodType() == MethodContext.Type.CONFIGURATION_BEFORE_METHOD) {
            List<MethodContext> list = currentBeforeMethods.get();
            if (list == null) {
                list = new ArrayList<>();
                currentBeforeMethods.set(list);
            }
            list.add(methodContext);
        }
    }

    private static void checkForBeforeDependencies(MethodContext methodContext) {
        // beforeMethod
        if (methodContext.getMethodType() == MethodContext.Type.TEST_METHOD && currentBeforeMethods.get() != null) {
            // Allocate all before methods of current thread to the MethodContext of current test method.
            // TestNG always executes beforeMethods and afterMethods in the same thread.
            handleConfigurationMethods(
                    methodContext,
                    null,
                    () -> currentBeforeMethods.get().stream()
            );
            currentBeforeMethods.remove();
        }

        // beforeClass
        handleConfigurationMethods(
                methodContext,
                null,
                () -> methodContext.getClassContext().readMethodContexts()
                        .filter(context -> context.getMethodType() == MethodContext.Type.CONFIGURATION_BEFORE_CLASS)
        );

        // beforeTest
        handleConfigurationMethods(
                methodContext,
                null,
                () -> methodContext.getClassContext().getTestContext().readClassContexts()
                        .flatMap(ClassContext::readMethodContexts)
                        .filter(context -> context.getMethodType() == MethodContext.Type.CONFIGURATION_BEFORE_TEST)
        );

        // beforeSuite
        handleConfigurationMethods(
                methodContext,
                null,
                () -> methodContext.getClassContext().getTestContext().getSuiteContext().readTestContexts()
                        .flatMap(TestContext::readClassContexts)
                        .flatMap(ClassContext::readMethodContexts)
                        .filter(context -> context.getMethodType() == MethodContext.Type.CONFIGURATION_BEFORE_SUITE)
        );
    }

    private static void checkForAfterDependencies(MethodContext methodContext) {
        // afterMethod
        if (currentTestMethodContext.get() != null) {
            handleConfigurationMethods(
                    methodContext,
                    MethodContext.Type.CONFIGURATION_AFTER_METHOD,
                    () -> Stream.of(currentTestMethodContext.get())
            );
        }

        // afterClass
        handleConfigurationMethods(
                methodContext,
                MethodContext.Type.CONFIGURATION_AFTER_CLASS,
                () -> methodContext.getClassContext().readMethodContexts()
        );

        // afterTest
        handleConfigurationMethods(
                methodContext,
                MethodContext.Type.CONFIGURATION_AFTER_TEST,
                () -> methodContext.getClassContext().getTestContext().readClassContexts()
                        .flatMap(ClassContext::readMethodContexts)
        );

        // afterSuite
        handleConfigurationMethods(
                methodContext,
                MethodContext.Type.CONFIGURATION_AFTER_SUITE,
                () -> methodContext.getClassContext().getTestContext().getSuiteContext().readTestContexts()
                        .flatMap(TestContext::readClassContexts)
                        .flatMap(ClassContext::readMethodContexts)
        );
    }

    private static void checkForMethodDependencies(ITestNGMethod testNGMethod, MethodContext methodContext) {
        if (methodContext.isTestMethod()) {
            Test test = testNGMethod.getConstructorOrMethod().getMethod().getAnnotation(Test.class);
            currentTestMethodContext.set(methodContext);

            // dependsOnMethods
            String[] dependsOnMethods = test.dependsOnMethods();
            for (String dependsOnMethod : dependsOnMethods) {
                // In case of retried dependsOn methods the correct dependsOn-context was the last retry
                methodContext.getClassContext().readMethodContexts()
                        .filter(context -> context.getName().equals(dependsOnMethod) && context.getStatus() != Status.RETRIED)
                        .forEach(context -> {
                            methodContext.addDependsOnMethod(context);
                            context.addRelatedMethodContext(methodContext);
                        });
            }

            // dependsOnGroups
            for (String dependsOnGroup : test.dependsOnGroups()) {
                // Methods for group dependency can be placed in other classes
                methodContext.getClassContext().getTestContext().readClassContexts()
                        .flatMap(ClassContext::readMethodContexts)
                        .filter(MethodContext::isTestMethod)
                        .filter(context -> !context.getClassContext().getName().concat(context.getName()).equals(methodContext.getClassContext().getName().concat(methodContext.getName())))
                        .filter(context -> {
                            Optional<ITestResult> testNgResult = methodContext.getTestNgResult();
//                            Optional<Test> internalTest = context.getAnnotation(Test.class);
                            if (testNgResult.isPresent()) {
                                String[] groups = testNgResult.get().getMethod().getGroupsDependedUpon();
                                return Arrays.asList(groups).contains(dependsOnGroup);
                            }
                            return false;
                        })
                        .forEach(methodContext::addDependsOnMethod);
            }

            // dataprovider
            if (testNGMethod.isDataDriven()) {
                IDataProviderMethod dataProviderMethod = testNGMethod.getDataProviderMethod();
                // Can be null if test methods points to a non-existent data provider
                if (dataProviderMethod != null) {
                    MethodContext dpContext = dataProviderMap.get(dataProviderMethod.getMethod());
                    if (dpContext != null) {
                        addRelation(dpContext, methodContext);
                    }
                }
            }
        }
    }

    /**
     * Handling before methods
     * In case the given type is null the given MethodContext is combined with all other MethodContexts
     * of the stream. The stream should pre-filtered by the expected before method of a specific context.
     * <p>
     * Handling after methods:
     * In case the given type is one of afterXY (specified by type), all other MethodContexts
     * of the stream are combined with exactly the given MethodContext.
     */
    private static void handleConfigurationMethods(
            MethodContext methodContext,
            MethodContext.Type type,
            Supplier<Stream<MethodContext>> streamSupplier) {
        if ((type == null || methodContext.getMethodType() == type) && streamSupplier.get() != null) {
            streamSupplier.get().forEach(context -> addRelation(context, methodContext));
        }
    }

    private static void addRelation(MethodContext methodContext1, MethodContext methodContext2) {
        if (methodContext1 != methodContext2) {
            methodContext1.addRelatedMethodContext(methodContext2);
            methodContext2.addRelatedMethodContext(methodContext1);
        }
    }

//    private static void handleCurrentContext(Method method, MethodContext methodContext) {
//        boolean addToList = true;
//        boolean isTestMethod = !methodContext.isConfigMethod();
//
//        if (method.getAnnotation(DataProvider.class) != null) {
//            dataProviderMap.put(method.toString(), methodContext);
//            return;
//        }
//
//        if (TEST_WAS_HERE.get() != null) {
//            if (isTestMethod) {
//                // test method means new context
//                addToList = false;
//            } else {
//                // config method: beforeXX methods will announce a new context
//                if (isBeforeXXMethod(method)) {
//                    addToList = false;
//                }
//            }
//        }
//
//        if (!addToList) {
//            // flush first
//            flush();
//        }
//        EXECUTION_CONTEXT.get(getThreadId()).add(methodContext);
//
//        // mark as test if it is one
//        if (isTestMethod) {
//            TEST_WAS_HERE.set(true);
//        }
//    }

//    public static void flushAll() {
//        synchronized (EXECUTION_CONTEXT) {
//            for (Long key : EXECUTION_CONTEXT.keySet()) {
//                List<MethodContext> methodContexts = EXECUTION_CONTEXT.get(key);
//                flushContainers(methodContexts);
//            }
//        }
//    }

//    public static void flush() {
//        List<MethodContext> containerList = EXECUTION_CONTEXT.get(getThreadId());
//
//        flushContainers(containerList);
//
//         clean up
//        EXECUTION_CONTEXT.remove(getThreadId());
//        TEST_WAS_HERE.remove();
//
//         create new list
//        createNewList();
//    }

//    private static void flushContainers(List<MethodContext> containerList) {
//        if (containerList != null && containerList.size() > 0) {
//        /*
//        new context: populate the previous context to previous main containers
//         */
//            containerList.forEach(methodContext -> {
//                containerList.forEach(relatedMethodContext -> {
//                    if (methodContext != relatedMethodContext) {
//                        methodContext.addRelatedMethodContext(relatedMethodContext);
//                    }
//                });
//            });
////            List<MethodContext> relatedContainers = new LinkedList<>(containerList);
////
////            synchronized (containerList) {
////                for (MethodContext methodContainer : containerList) {
////                    methodContainer.setRelatedMethodContexts(relatedContainers);
////                }
////            }
//        }
//    }
}
