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
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.testng.IClass;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.*;

public class TestContext extends Context implements SynchronizableContext {

    public final List<ClassContext> classContexts = new LinkedList<>();
    public final SuiteContext suiteContext;
    public final RunContext runContext;

    /**
     * id to context map:
     * The first occurance of a class create an entry for id=fullClassName.
     * Same class context can see an already existing id and either enhance (same testContext)
     * or create a new id with naming scheme.
     */
    private static final Map<String, ClassContext> CLASS_CONTEXT_MARKS = new LinkedHashMap<>();

    public TestContext(SuiteContext suiteContext, RunContext runContext) {
        this.parentContext = this.suiteContext = suiteContext;
        this.runContext = runContext;
    }

    public ClassContext getClassContext(ITestResult testResult, ITestContext iTestContext, IInvokedMethod invokedMethod) {
        IClass testClass = TestNGHelper.getTestClass(testResult, iTestContext, invokedMethod);
        Class<?> realClass = testClass.getRealClass();

        /*
            tree example:

            - A
            - A' (from test1)
            - A'' (from test2)

            A=the only one A context (A'++ don't exist)
            A=dummy (A'++ exist)

            **** no FennecClassContext
            C running first time: C -> A
            C running seconds time: return A
            C' (another testcontext) running first time: C' -> A'', A -> A'

            **** with FennecClassContext
            same like above, with classContext reference to merge Context
         */

        /*
        this is the one we want to return later
         */
        final ClassContext classContext;

        /*
        basic tree entries for the context
         */
        classContext = getTreeClassContext(realClass);

        /*
        check if @FennecClassContext is present on class
         */
        if (realClass.isAnnotationPresent(FennecClassContext.class)) {
            /*
            hook into runContext mergedContexts
             */
            final FennecClassContext actualFennecClassContext = realClass.getAnnotation(FennecClassContext.class);

            if (actualFennecClassContext.mode() == FennecClassContext.Mode.ONE_FOR_ALL) {
                final ClassContext mergedClassContext;

                synchronized (runContext.mergedClassContexts) {
                    // check if this class is present
                    Optional<ClassContext> first = runContext.mergedClassContexts.stream().filter(c -> c.fennecClassContext == actualFennecClassContext).findFirst();
                    if (first.isPresent()) {
                        mergedClassContext = first.get();
                    } else {
                        // create and add to list
                        mergedClassContext = new ClassContext(this, runContext);
                        mergedClassContext.fullClassName = realClass.getName();
                        mergedClassContext.simpleClassName = realClass.getSimpleName();
                        fillBasicContextValues(mergedClassContext, this, mergedClassContext.simpleClassName);
                        mergedClassContext.fennecClassContext = actualFennecClassContext;
                        mergedClassContext.merged = true;

                        if (!StringUtils.isStringEmpty(actualFennecClassContext.value())) {
                            mergedClassContext.name = actualFennecClassContext.value();
                        }

                        runContext.mergedClassContexts.add(mergedClassContext);
                    }
                }

                // mark context reference
                classContext.merged = true;
                classContext.mergedIntoClassContext = mergedClassContext;
            }
        }

        return classContext;
    }

    private ClassContext getTreeClassContext(Class realClass) {
        /*
        create a new class context, maybe this is later thrown away
         */
        final ClassContext newClassContext = new ClassContext(this, runContext);
        newClassContext.fullClassName = realClass.getName();
        newClassContext.simpleClassName = realClass.getSimpleName();
        fillBasicContextValues(newClassContext, this, newClassContext.simpleClassName);

        synchronized (CLASS_CONTEXT_MARKS) {
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
                synchronized (classContexts) {
                    classContexts.add(newClassContext);
                }

                final String dummySWI = "dummy";
                if (!dummySWI.equals(defaultStoredClassContext.swi)) {
                    /*
                    the defaultStoredClassContext is NOT a dummy already, set it to dummy
                     */
                    defaultStoredClassContext.setExplicitName();
                    CLASS_CONTEXT_MARKS.put(defaultStoredClassContext.swi, defaultStoredClassContext);

                    final ClassContext dummy = new ClassContext(null, runContext);
                    dummy.swi = "dummy";
                    CLASS_CONTEXT_MARKS.put(defaultStoredClassContext.fullClassName, dummy);
                }
            }
            else {
                // Our new class is the first time coming up.
                CLASS_CONTEXT_MARKS.put(newClassContext.fullClassName, newClassContext);
                synchronized (classContexts) {
                    classContexts.add(newClassContext);
                }
            }

            return newClassContext;
        }
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
