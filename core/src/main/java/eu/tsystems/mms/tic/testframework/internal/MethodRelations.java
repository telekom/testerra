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
package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MethodRelations {

    private static final Map<Long, List<MethodContext>> EXECUTION_CONTEXT = Collections.synchronizedMap(new HashMap<>());
    private static final ThreadLocal<Boolean> TEST_WAS_HERE = new ThreadLocal<>();

    private MethodRelations() {

    }

    private static boolean isBeforeXXMethod(Method method) {
        if (method.isAnnotationPresent(BeforeSuite.class) ||
                method.isAnnotationPresent(BeforeClass.class) ||
                method.isAnnotationPresent(BeforeGroups.class) ||
                method.isAnnotationPresent(BeforeMethod.class) ||
                method.isAnnotationPresent(BeforeTest.class)
        ) {
            return true;
        } else {
            return false;
        }
    }

    private static long getThreadId() {
        return Thread.currentThread().getId();
    }

    private static void createNewList() {
        EXECUTION_CONTEXT.put(getThreadId(), new LinkedList<>());
    }

    public static void announceRun(Method method, MethodContext methodContext) {
        if (EXECUTION_CONTEXT.get(getThreadId()) == null) {
            createNewList();
        }

        // handle current context
        handleCurrentContext(method, methodContext);

        // check for dependencies
        checkForDependencies(method, methodContext);
    }

    private static void checkForDependencies(Method method, MethodContext methodContext) {
        if (method.isAnnotationPresent(Test.class)) {
            /*
            dependsOnMethods
             */
            Test test = method.getAnnotation(Test.class);
            // TODO: dependsOnGroups is not checked at the moment and it's also missing in dependency graph
            String[] dependsOnMethods = test.dependsOnMethods();
            for (String dependsOnMethod : dependsOnMethods) {
                // In case of retried dependsOn methods the correct dependsOn-context was not retried
                Optional<MethodContext> foundContext = methodContext.getClassContext().readMethodContexts()
                        .filter(context -> context.getName().equals(dependsOnMethod) && !context.hasBeenRetried())
                        .findFirst();
                foundContext.ifPresent(methodContext::addDependsOnMethod);
            }
        }
    }

    private static void handleCurrentContext(Method method, MethodContext methodContext) {
        boolean addToList = true;
        boolean isTestMethod = !methodContext.isConfigMethod();

        if (TEST_WAS_HERE.get() != null) {
            if (isTestMethod) {
                /*
                 test method means new context
                */
                addToList = false;
            } else {
                /*
                config method: beforeXX methods will announce a new context
                 */
                if (isBeforeXXMethod(method)) {
                    addToList = false;
                }
            }
        }

        if (!addToList) {
            /*
            flush first
             */
            flush();
        }
        EXECUTION_CONTEXT.get(getThreadId()).add(methodContext);

        // mark as test if it is one
        if (isTestMethod) {
            TEST_WAS_HERE.set(true);
        }
    }

    public static void flushAll() {
        synchronized (EXECUTION_CONTEXT) {
            for (Long key : EXECUTION_CONTEXT.keySet()) {
                List<MethodContext> methodContexts = EXECUTION_CONTEXT.get(key);
                flushContainers(methodContexts);
            }
        }
    }

    public static void flush() {
        List<MethodContext> containerList = EXECUTION_CONTEXT.get(getThreadId());

        flushContainers(containerList);

        // clean up
        EXECUTION_CONTEXT.remove(getThreadId());
        TEST_WAS_HERE.remove();

        // create new list
        createNewList();
    }

    private static void flushContainers(List<MethodContext> containerList) {
        if (containerList != null && containerList.size() > 0) {
        /*
        new context: populate the previous context to previous main containers
         */
            containerList.forEach(methodContext -> {
                containerList.forEach(relatedMethodContext -> {
                    if (methodContext != relatedMethodContext) {
                        methodContext.addRelatedMethodContext(relatedMethodContext);
                    }
                });
            });
//            List<MethodContext> relatedContainers = new LinkedList<>(containerList);
//
//            synchronized (containerList) {
//                for (MethodContext methodContainer : containerList) {
//                    methodContainer.setRelatedMethodContexts(relatedContainers);
//                }
//            }
        }
    }
}
