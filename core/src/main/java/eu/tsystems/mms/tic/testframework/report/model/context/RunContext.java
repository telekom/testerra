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

import eu.tsystems.mms.tic.testframework.events.FennecEvent;
import eu.tsystems.mms.tic.testframework.events.FennecEventDataType;
import eu.tsystems.mms.tic.testframework.events.FennecEventService;
import eu.tsystems.mms.tic.testframework.events.FennecEventType;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.utils.TestNGHelper;
import eu.tsystems.mms.tic.testframework.utils.reference.IntRef;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.*;
import java.util.stream.Collectors;

public class RunContext extends Context implements SynchronizableContext {

    public final List<SuiteContext> suiteContexts = new LinkedList<>();
    public final List<ClassContext> mergedClassContexts = new LinkedList<>();
    public Map<String, List<MethodContext>> failureAspects = new LinkedHashMap<>();
    public Map<String, List<MethodContext>> exitPoints = new LinkedHashMap<>();
    public final RunConfig runConfig = new RunConfig();

    public final Map<String, String> metaData = new LinkedHashMap<>();

    public RunContext() {
        name = runConfig.RUNCFG;
        swi = name;

        // fire context update event: create context
        FennecEventService.getInstance().fireEvent(new FennecEvent(FennecEventType.CONTEXT_UPDATE).addData(FennecEventDataType.CONTEXT, this));
    }

    public SuiteContext getSuiteContext(ITestResult testResult, ITestContext iTestContext) {
        final String suiteName = TestNGHelper.getSuiteName(testResult, iTestContext);
        return getContext(SuiteContext.class, suiteContexts, suiteName, true, () -> new SuiteContext(this));
    }

    public List<SuiteContext> copyOfSuiteContexts() {
        synchronized (suiteContexts) {
            return new LinkedList<>(suiteContexts);
        }
    }

    @Override
    public TestStatusController.Status getStatus() {
        return getStatusFromContexts(suiteContexts.toArray(new Context[0]));
    }

    public int getNumberOfRepresentationalTests() {
        IntRef i = new IntRef();
        List<SuiteContext> suiteContexts = copyOfSuiteContexts();
        suiteContexts.forEach(suiteContext -> {
            suiteContext.copyOfTestContexts().forEach(testContext -> {
                testContext.copyOfClassContexts().forEach(classContext -> {
                    i.increaseBy(classContext.getRepresentationalMethods().length);
                });
            });
        });
        return i.getI();
    }

    public Map<TestStatusController.Status, Integer> getMethodStats(boolean includeTestMethods, boolean includeConfigMethods) {
        Map<TestStatusController.Status, Integer> counts = new LinkedHashMap<>();

        // initialize with 0
        Arrays.stream(TestStatusController.Status.values()).forEach(status -> counts.put(status, 0));

        List<SuiteContext> suiteContexts = copyOfSuiteContexts();
        suiteContexts.forEach(suiteContext -> {
            suiteContext.copyOfTestContexts().forEach(testContext -> {
                testContext.copyOfClassContexts().forEach(classContext -> {
                    Map<TestStatusController.Status, Integer> methodStats = classContext.getMethodStats(includeTestMethods, includeConfigMethods);
                    methodStats.keySet().forEach(status -> {
                        Integer oldValue = counts.get(status);
                        int newValue = oldValue + methodStats.get(status);
                        counts.put(status, newValue);
                    });
                });
            });
        });

        return counts;
    }

    /**
     * Get method statistics for all effective classes.
     * The classes are all classContext from the context tree (without merged ones) AND mergedClassContexts from runContext.
     *
     * @param includeTestMethods .
     * @param includeConfigMethods  .
     * @return a map
     */
    public Map<ClassContext, Map> getMethodStatsPerClass(boolean includeTestMethods, boolean includeConfigMethods) {
        final Map<ClassContext, Map> methodStatsPerClass = new LinkedHashMap<>();
        List < SuiteContext > suiteContexts = copyOfSuiteContexts();
        suiteContexts.forEach(suiteContext -> {
            suiteContext.copyOfTestContexts().forEach(testContext -> {
                testContext.copyOfClassContexts().forEach(classContext -> {
                    if (!classContext.merged) {
                        Map<TestStatusController.Status, Integer> methodStats = classContext.getMethodStats(includeTestMethods, includeConfigMethods);
                        methodStatsPerClass.put(classContext, methodStats);
                    }
                });
            });
        });

        mergedClassContexts.forEach(classContext -> methodStatsPerClass.put(classContext, classContext.getMethodStats(includeTestMethods, includeConfigMethods)));

        /*
        sort
         */
        Comparator<? super Map> comp = (Comparator<Map>) (m1, m2) -> {
            IntRef i1 = new IntRef();
            m1.keySet().forEach(status -> i1.increaseBy((int) m1.get(status)));

            IntRef i2 = new IntRef();
            m2.keySet().forEach(status -> i2.increaseBy((int) m2.get(status)));

            return i2.getI() - i1.getI();
        };
        final Map sortedMap = methodStatsPerClass.entrySet().stream().sorted(Map.Entry.comparingByValue(comp))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return sortedMap;
    }

    public TestStatusController.Status[] getAvailableStatuses() {
        return TestStatusController.Status.values();
    }

    public TestStatusController.Status[] getAvailableStatus() {
        return TestStatusController.Status.values();
    }
}
