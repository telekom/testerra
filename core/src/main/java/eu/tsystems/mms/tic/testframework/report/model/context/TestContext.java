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
import eu.tsystems.mms.tic.testframework.report.utils.DefaultTestNGContextGenerator;
import java.util.Queue;
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

    public TestContext(SuiteContext suiteContext) {
        this.parentContext = suiteContext;
    }

    public SuiteContext getSuiteContext() {
        return (SuiteContext) this.parentContext;
    }

    public Stream<ClassContext> readClassContexts() {
        return this.classContexts.stream();
    }

    public ClassContext getClassContext(ITestResult testResult, ITestContext iTestContext, IInvokedMethod invokedMethod) {
        DefaultTestNGContextGenerator contextGenerator = TesterraListener.getContextGenerator();
        final IClass testClass = contextGenerator.getTestClass(testResult, iTestContext, invokedMethod);
        return this.pGetClassContext(testClass, contextGenerator.getClassContextName(testResult, iTestContext, invokedMethod));
    }

    /**
     * Used in platform-connector only
     * May be @deprecated
     */
    public ClassContext getClassContext(final ITestNGMethod iTestNgMethod) {
        final IClass testClass = iTestNgMethod.getTestClass();
        return this.pGetClassContext(testClass, testClass.getName());
    }

    private synchronized ClassContext pGetClassContext(IClass testClass, String classContextName) {

        final Class<?> realClass = testClass.getRealClass();

        return getOrCreateContext(
                this.classContexts,
                classContextName,
                () -> {
                    ClassContext newClassContext = new ClassContext(realClass, this);
                    /*
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
                    return newClassContext;
                },
                classContext -> {
                    EventBus eventBus = TesterraListener.getEventBus();
                    eventBus.post(new ContextUpdateEvent().setContext(this));
                });

    }

    //    private synchronized ClassContext createAndAddClassContext(Class realClass) {
    //
    //        /*
    //        create a new class context, maybe this is later thrown away
    //         */
    //        final ClassContext newClassContext = new ClassContext(realClass, this);
    //
    //        //fillBasicContextValues(newClassContext, this, newClassContext.getTestClass().getSimpleName());
    //
    //        // lets check if we already know about it
    //        if (readClassContexts().anyMatch(classContext -> classContext.getTestClass().getName().equals(realClass.getName()))) {
    //
    //            // a context for this class is already present
    ////            newClassContext.setName(realClass.getSimpleName() + "_" + getSuiteContext().getName() + "_" + this.getName());
    //
    //            // Does a marker entry with this swi exist?
    //            Optional<ClassContext> optionalFoundClassContext = readClassContexts().filter(classContext -> {
    //                if (classContext.getTestClass().getName().equals(realClass.getName())) {
    //                    AbstractContext context = newClassContext;
    //                    AbstractContext cContext = classContext;
    //                    while (context.getParentContext() != null) {
    //                        if (!context.getParentContext().equals(cContext.getParentContext())) {
    //                            return false;
    //                        }
    //                        context = context.getParentContext();
    //                        cContext = cContext.getParentContext();
    //                    }
    //                }
    //                return true;
    //            }).findFirst();
    //
    //            if (optionalFoundClassContext.isPresent()) {
    //                // this must be our class context
    //                ClassContext classContext = optionalFoundClassContext.get();
    //                /**
    //                 * @todo erku Setting the name doesn't behave like before (#setExplicitName())
    //                 */
    //                classContext.setName(realClass.getSimpleName() + "_" + getSuiteContext().getName() + "_" + this.getName());
    //                return classContext;
    //            }
    //        } else {
    //            newClassContext.setName(realClass.getSimpleName());
    //        }
    //
    //        // Our new class is the first time coming up.
    //        classContexts.add(newClassContext);
    //        return newClassContext;
    //    }

    @Override
    public TestStatusController.Status getStatus() {
        return getStatusFromContexts(classContexts.stream());
    }
}
