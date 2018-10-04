/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import org.testng.IClass;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.LinkedList;
import java.util.List;

public class TestContext extends Context implements SynchronizableContext {

    List<ClassContext> classContexts = new LinkedList<>();
    public final SuiteContext suiteContext;
    public final RunContext runContext;

    public TestContext(SuiteContext suiteContext, RunContext runContext) {
        this.suiteContext = suiteContext;
        this.runContext = runContext;
    }

    public ClassContext getClassContext(ITestResult testResult, ITestContext iTestContext, IInvokedMethod invokedMethod) {
        IClass testClass = TestNGHelper.getTestClass(testResult, iTestContext, invokedMethod);
        Class<?> realClass = testClass.getRealClass();

        final String name = realClass.getSimpleName();

        ClassContext classContext = getContext(ClassContext.class, classContexts, name, true, () -> new ClassContext(this, runContext));

        if (realClass.isAnnotationPresent(FennecClassContext.class)) {
            classContext.fennecClassContext = realClass.getAnnotation(FennecClassContext.class);
        }

        classContext.fullClassName = realClass.getName();
        classContext.simpleClassName = realClass.getSimpleName();

        return classContext;
    }

    public List<ClassContext> copyOfClassContexts() {
        synchronized (classContexts) {
            return new LinkedList<>(classContexts);
        }
    }


    @Override
    public TestStatusController.Status getStatus() {
        return getStatusFromContexts(classContexts.toArray(new Context[0]));
    }
}
