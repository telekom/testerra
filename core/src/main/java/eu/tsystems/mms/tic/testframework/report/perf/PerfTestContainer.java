/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.report.perf;

import eu.tsystems.mms.tic.testframework.internal.TimingInfo;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.*;

public final class PerfTestContainer {

    private PerfTestContainer() {
    }

    /**
     * contains all measurements of all Thread of all Testmethods; it's used for csv-generation
     */
    private static final Map<String, Map<Long, List<TimingInfo>>> PAGE_LOAD_INFOS_PER_TEST_METHOD =
            Collections.synchronizedMap(new HashMap<String, Map<Long, List<TimingInfo>>>());

    /**
     * save ITestResult for each Thread
     */
    private static final Map<Long, ITestResult> TEST_RESULT_INFOS = Collections.synchronizedMap(
            new HashMap<Long, ITestResult>());

    /**
     * save the VU to each Thread
     */
    private static final Map<Long, String> THREAD_NAMES = Collections.synchronizedMap(new HashMap<Long, String>());

    /**
     * ITestContext of the whole test
     */
    private static ITestContext iTestContext = null;

    /**
     * add pageLoadInfo to pageLoadInfoByThreadID and pageLoadInfosByTestMethod
     *
     * @param threadID .
     * @param timingInfos .
     * @param testMethodName .
     */
    public static void addPageLoadInfo(Long threadID, List<TimingInfo> timingInfos, String testMethodName) {
        // add the pageLoadInfo in a map with test method name as key
        if (!PAGE_LOAD_INFOS_PER_TEST_METHOD.containsKey(testMethodName)) {

            Map<Long, List<TimingInfo>> map = Collections.synchronizedMap(new HashMap<Long, List<TimingInfo>>());
            map.put(threadID, timingInfos);
            PAGE_LOAD_INFOS_PER_TEST_METHOD.put(testMethodName, map);
        } else {
            Map<Long, List<TimingInfo>> map = PAGE_LOAD_INFOS_PER_TEST_METHOD.get(testMethodName);
            map.put(threadID, timingInfos);
            PAGE_LOAD_INFOS_PER_TEST_METHOD.put(testMethodName, map);
        }
    }
//
//    /**
//     * set the ITestContext
//     *
//     * @param iTestContext .
//     */
//    public static void setTestContext(ITestContext iTestContext) {
//        PerfTestContainer.iTestContext = iTestContext;
//    }
//
//    /**
//     * get the ITestContext
//     *
//     * @return iTestContext
//     */
//    public static ITestContext getTestContext() {
//        return iTestContext;
//    }

    public static Map<String, Map<Long, List<TimingInfo>>> getPageLoadInfosPerTestMethod() {
        return PAGE_LOAD_INFOS_PER_TEST_METHOD;
    }

    /**
     * get test result
     * @param threadID .
     * @return test result info
     */
    public static ITestResult getTestResult(Long threadID) {
        return TEST_RESULT_INFOS.get(threadID);
    }

    /**
     * set test result
     *
     * @param threadID .
     * @param iTestResult .
     */
    public static void setTestResult(Long threadID, ITestResult iTestResult) {
        TEST_RESULT_INFOS.put(threadID, iTestResult);
    }

    /**
     * save the thread name
     *
     * @param threadID .
     * @param threadName .
     */
    public static void saveThreadName(Long threadID, String threadName) {
        THREAD_NAMES.put(threadID, threadName);
    }

    /**
     * gets the name of the thread by threadID
     *
     * @param threadID .
     * @return thread name
     */
    public static String getThreadNameByID(Long threadID) {
        return THREAD_NAMES.get(threadID);
    }
    /**
     * returns the Map with all ThreadNames
     *
     * @return THREAD_NAMES
     */
    public static Set<String> getVirtualUsers() {
        Set<Long> threadIDSet = THREAD_NAMES.keySet();
        Set<String> vuSet = new HashSet<String>();
        for(Long threadID:threadIDSet) {
            vuSet.add(THREAD_NAMES.get(threadID));
        }
        return  vuSet;
    }

//    public static void genereateReport() {
//        // holds the pageLoadInfos of each thread of one specific testmethod
//        Map<String, Map<Long, List<TimingInfo>>> pageLoadInfosPerTestMethod = PerfTestContainer
//                .getPageLoadInfosPerTestMethod();
//
//        // generate folders
//        PerfTestReportUtils.setOutputFolderName();
//
//        // holds pageLoadInfos of all threads: thread id maps to list of pageLoadInfos
//        Map<Long, List<TimingInfo>> pageLoadInfos = PerfTestReportUtils
//                .createPageLoadInfosOfAllThreads(pageLoadInfosPerTestMethod);
//
//        // just CSV, because it it can use all PageLoadInfos of all TestMethods
//        PerfTestReportUtils.processLoadTestDataForCSV(pageLoadInfos);
//
//        // Graphs need to be generated per Testmethod, therefore a different Map is used
//        PerfTestReportUtils.processLoadTestDataForGraphs(pageLoadInfosPerTestMethod);
//
//        // save configs of PerfTest in a csv-file
//        PerfTestReportUtils.saveTestConfigs();
//    }

    public static void prepareMeasurementsForTesterraReport() {

        Map<String, Map<Long, List<TimingInfo>>> pageLoadInfosPerTestMethod = PerfTestContainer
                .getPageLoadInfosPerTestMethod();


        // 1. sort the Map per method like in perftest--> all PageLoadInfos that occured are sorted
        Set<String> testMethodNames = pageLoadInfosPerTestMethod.keySet();

        for(String testMethodName : testMethodNames) {
            Map<Long, List<TimingInfo>> pageLoadInfoPerTestMethod = pageLoadInfosPerTestMethod.get(testMethodName);
            Map<Long, List<TimingInfo>> sortedPageLoadInfos = PerfTestReportUtils.sortMap(pageLoadInfoPerTestMethod);
            pageLoadInfosPerTestMethod.put(testMethodName, sortedPageLoadInfos);
        }

        // 2. create Map per Action --> the List of pageLoadInfos is sorted implicitly
        PerfTestReportUtils.createPageLoadInfosOfAllActions(pageLoadInfosPerTestMethod);

        PerfTestReportUtils.generateAverageMinMaxPerAction();

    }
}
