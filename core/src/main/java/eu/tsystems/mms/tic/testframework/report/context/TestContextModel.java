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
package eu.tsystems.mms.tic.testframework.report.context;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.testng.IClass;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

/**
 * Representation of a TestNG {@link ITestContext}
 */
public class TestContextModel extends AbstractContext implements SynchronizableContext {

    public final Queue<ClassContext> classContexts = new ConcurrentLinkedQueue<>();
    public final SuiteContext suiteContext;
    public final ExecutionContext executionContext;

    /**
     * id to context map:
     * The first occurance of a class create an entry for id=fullClassName.
     * Same class context can see an already existing id and either enhance (same testContext)
     * or create a new id with naming scheme.
     */
    private static final Map<String, ClassContext> CLASS_CONTEXT_MARKS = new ConcurrentHashMap<>();

    public TestContextModel(SuiteContext suiteContext, ExecutionContext executionContext) {
        this.parentContext = this.suiteContext = suiteContext;
        this.executionContext = executionContext;
    }

    public ClassContext getClassContext(ITestResult testResult, ITestContext iTestContext, IInvokedMethod invokedMethod) {
        final IClass testClass = TestNGHelper.getTestClass(testResult, iTestContext, invokedMethod);
        return this.pGetClassContext(testClass);
    }

    public ClassContext getClassContext(final ITestNGMethod iTestNgMethod) {
        final IClass testClass = iTestNgMethod.getTestClass();
        return this.pGetClassContext(testClass);
    }

    private ClassContext pGetClassContext(IClass testClass) {
        final Class<?> realClass = testClass.getRealClass();

        /*
            tree example:

            - A
            - A' (from test1)
            - A'' (from test2)

            A=the only one A context (A'++ don't exist)
            A=dummy (A'++ exist)

            **** no TesterraClassContext
            C running first time: C -> A
            C running seconds time: return A
            C' (another testcontext) running first time: C' -> A'', A -> A'

            **** with TesterraClassContext
            same like above, with classContext reference to merge Context
         */

        /*
        basic tree entries for the context
         */
        final ClassContext newClassContext = createAndAddClassContext(realClass);

        /**
         * check if {@link TestContext} is present on class
         */
        if (realClass.isAnnotationPresent(TestContext.class)) {

            /*
            hook into executionContext mergedContexts
             */
            final TestContext actualTestContext = realClass.getAnnotation(TestContext.class);

            if (actualTestContext.mode() == TestContext.Mode.ONE_FOR_ALL) {
                synchronized (executionContext.mergedClassContexts) {
                    final ClassContext mergedClassContext;

                    // check if this class is present
                    Optional<ClassContext> first = executionContext.mergedClassContexts.stream()
                            .filter(c -> c.testContext == actualTestContext)
                            .findFirst();

                    if (first.isPresent()) {
                        mergedClassContext = first.get();
                    } else {
                        // create and add to list
                        mergedClassContext = new ClassContext(this, executionContext);
                        mergedClassContext.fullClassName = realClass.getName();
                        mergedClassContext.simpleClassName = realClass.getSimpleName();
                        fillBasicContextValues(mergedClassContext, this, mergedClassContext.simpleClassName);
                        mergedClassContext.testContext = actualTestContext;
                        mergedClassContext.merged = true;

                        if (!StringUtils.isStringEmpty(actualTestContext.name())) {
                            mergedClassContext.name = actualTestContext.name();
                        }

                        executionContext.mergedClassContexts.add(mergedClassContext);
                    }

                    // mark context reference
                    newClassContext.merged = true;
                    newClassContext.mergedIntoClassContext = mergedClassContext;
                }
            }
        }

        EventBus eventBus = TesterraListener.getEventBus();
        eventBus.post(new ContextUpdateEvent().setContext(this));

        return newClassContext;

    }

    private synchronized ClassContext createAndAddClassContext(Class realClass) {
        /*
        create a new class context, maybe this is later thrown away
         */
        final ClassContext newClassContext = new ClassContext(this, executionContext);
        newClassContext.fullClassName = realClass.getName();
        newClassContext.simpleClassName = realClass.getSimpleName();
        fillBasicContextValues(newClassContext, this, newClassContext.simpleClassName);

        // lets check if we already know about it
        if (CLASS_CONTEXT_MARKS.containsKey(newClassContext.fullClassName)) {
            // a context for this class is already present

            // Does a marker entry with this swi exist?
            if (CLASS_CONTEXT_MARKS.containsKey(newClassContext.swi)) { // A'++ HIT
                // this must be our class context
                return CLASS_CONTEXT_MARKS.get(newClassContext.swi);
            }

            // now A can be the matching one, or this is a completely new context

            final ClassContext defaultStoredClassContext = CLASS_CONTEXT_MARKS.get(newClassContext.fullClassName);
            if (newClassContext.swi.equals(defaultStoredClassContext.swi)) { // A HIT
                // it is the exact same class
                return defaultStoredClassContext;
            }

                /*
                 A is present but not matching.
                 creating A'' and setting A to dummy
                  */
            newClassContext.setExplicitName();
            CLASS_CONTEXT_MARKS.put(newClassContext.swi, newClassContext);
            classContexts.add(newClassContext);

            final String dummySWI = "dummy";
            if (!dummySWI.equals(defaultStoredClassContext.swi)) {
                    /*
                    the defaultStoredClassContext is NOT a dummy already, set it to dummy
                     */
                defaultStoredClassContext.setExplicitName();
                CLASS_CONTEXT_MARKS.put(defaultStoredClassContext.swi, defaultStoredClassContext);

                final ClassContext dummy = new ClassContext(null, executionContext);
                dummy.swi = "dummy";
                CLASS_CONTEXT_MARKS.put(defaultStoredClassContext.fullClassName, dummy);
            }
        } else {
            // Our new class is the first time coming up.
            CLASS_CONTEXT_MARKS.put(newClassContext.fullClassName, newClassContext);
            classContexts.add(newClassContext);
        }

        return newClassContext;
    }

    @Override
    public TestStatusController.Status getStatus() {
        return getStatusFromContexts(classContexts.stream());
    }
}
