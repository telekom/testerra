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
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import org.testng.IClass;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

/**
 * Representation of a TestNG {@link ITestContext}
 */
public class TestContext extends AbstractContext implements SynchronizableContext {
    /**
     * @deprecated Use {@link #readClassContexts()} instead
     */
    public final Queue<ClassContext> classContexts = new ConcurrentLinkedQueue<>();
    /**
     * id to context map:
     * The first occurance of a class create an entry for id=fullClassName.
     * Same class context can see an already existing id and either enhance (same testContext)
     * or create a new id with naming scheme.
     */
    private static final Map<String, ClassContext> CLASS_CONTEXT_MARKS = new ConcurrentHashMap<>();

    public TestContext(SuiteContext suiteContext) {
        this.parentContext = suiteContext;
    }

    public SuiteContext getSuiteContext() {
        return (SuiteContext)this.parentContext;
    }

    public Stream<ClassContext> readClassContexts() {
        return this.classContexts.stream();
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
        basic tree entries for the context
         */
        ClassContext newClassContext = createAndAddClassContext(realClass);

        /**
         * check if {@link TestClassContext} is present on class
         */
        if (realClass.isAnnotationPresent(TestClassContext.class)) {

            /*
            hook into executionContext mergedContexts
             */
            TestClassContext actualTestContext = realClass.getAnnotation(TestClassContext.class);
            if (actualTestContext.mode() == TestClassContext.Mode.ONE_FOR_ALL) {
                newClassContext.setTestClassContext(actualTestContext);
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
        final ClassContext newClassContext = new ClassContext(realClass,this);
        fillBasicContextValues(newClassContext, this, newClassContext.getTestClass().getSimpleName());

        // lets check if we already know about it
        if (CLASS_CONTEXT_MARKS.containsKey(newClassContext.getTestClass().getName())) {
            // a context for this class is already present

            // Does a marker entry with this swi exist?
            if (CLASS_CONTEXT_MARKS.containsKey(newClassContext.swi)) { // A'++ HIT
                // this must be our class context
                return CLASS_CONTEXT_MARKS.get(newClassContext.swi);
            }

            // now A can be the matching one, or this is a completely new context

            final ClassContext defaultStoredClassContext = CLASS_CONTEXT_MARKS.get(newClassContext.getTestClass().getName());
            if (newClassContext.swi.equals(defaultStoredClassContext.swi)) { // A HIT
                // it is the exact same class
                return defaultStoredClassContext;
            }

                /*
                 A is present but not matching.
                 creating A'' and setting A to dummy
                  */
            newClassContext.setExplicitName();
            CLASS_CONTEXT_MARKS.put(newClassContext.getSwi(), newClassContext);
            classContexts.add(newClassContext);

//            final String dummySWI = "dummy";
//            if (!dummySWI.equals(defaultStoredClassContext.getSwi())) {
//                    /*
//                    the defaultStoredClassContext is NOT a dummy already, set it to dummy
//                     */
//                defaultStoredClassContext.setExplicitName();
//                CLASS_CONTEXT_MARKS.put(defaultStoredClassContext.swi, defaultStoredClassContext);
//
//                final ClassContext dummy = new ClassContext(null, executionContext);
//                dummy.swi = "dummy";
//                CLASS_CONTEXT_MARKS.put(defaultStoredClassContext.fullClassName, dummy);
//            }
        } else {
            // Our new class is the first time coming up.
            CLASS_CONTEXT_MARKS.put(newClassContext.getTestClass().getName(), newClassContext);
            classContexts.add(newClassContext);
        }

        return newClassContext;
    }

    @Override
    public TestStatusController.Status getStatus() {
        return getStatusFromContexts(classContexts.stream());
    }
}
