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
    private final Queue<ClassContext> classContexts = new ConcurrentLinkedQueue<>();

    public TestContext(SuiteContext suiteContext) {
        this.parentContext = suiteContext;
    }

    public SuiteContext getSuiteContext() {
        return (SuiteContext) this.parentContext;
    }

    public Stream<ClassContext> readClassContexts() {
        return this.classContexts.stream();
    }

    public ClassContext getClassContext(ITestResult testResult) {
        DefaultTestNGContextGenerator contextGenerator = TesterraListener.getContextGenerator();
        return this.pGetClassContext(testResult.getTestClass(), contextGenerator.getClassContextName(testResult));
    }

    /**
     * Used in platform-connector only
     * May be @deprecated
     */
    public ClassContext getClassContext(final ITestNGMethod iTestNgMethod) {
        final IClass testClass = iTestNgMethod.getTestClass();
        return this.pGetClassContext(testClass, testClass.getRealClass().getSimpleName());
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
}
