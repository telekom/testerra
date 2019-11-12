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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.report.perf.PerfTestContainer;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by clgr on 09.09.2014.
 */
public final class StopWatch {

    private StopWatch() {

    }
    private static final ThreadLocal<HashMap<Object, Long>> START_PAGE_LOAD_TIMESTAMP = new ThreadLocal<HashMap<Object, Long>>();
    private static final ThreadLocal<List<TimingInfo>> PAGE_LOAD_INFOS = new ThreadLocal<List<TimingInfo>>();

    /**
     * starts to load page
     *
     * @param reference .
     */
    public static void startPageLoad(Object reference) {
        long currentTimeMillis = System.currentTimeMillis();
        if (START_PAGE_LOAD_TIMESTAMP.get() == null) {
            START_PAGE_LOAD_TIMESTAMP.set(new HashMap<Object, Long>());
        }
        START_PAGE_LOAD_TIMESTAMP.get().put(reference, currentTimeMillis);
    }

    /**
     * initializes the ThreadLocal List for saving LoadTimes
     */
    public static void initializeStopWatch() {
        PAGE_LOAD_INFOS.set(new ArrayList<TimingInfo>());
    }

    /**
     *   @param driver .
     * @param page .
     */
    public static void stopPageLoad(WebDriver driver, Class page) {
        String currentUrl = "URL unknown";
        if (driver != null) {
            currentUrl = driver.getCurrentUrl();
        }
        // endTime of measurement
        long timeStampMillis = System.currentTimeMillis();

        HashMap<Object, Long> hashMap = START_PAGE_LOAD_TIMESTAMP.get();
        if (hashMap == null) {
            return;
        }

        if (!hashMap.containsKey(driver)) {
            return;
        }

        Long startTimestamp = hashMap.get(driver);
        long durationMillis = timeStampMillis - startTimestamp;
        String context = page.getSimpleName();
        TimingInfo timingInfo = new TimingInfo(context, currentUrl, durationMillis, timeStampMillis);

        // store
        if (PAGE_LOAD_INFOS.get() == null) {
            PAGE_LOAD_INFOS.set(new ArrayList<TimingInfo>());
        }
        List<TimingInfo> timingInfoList = PAGE_LOAD_INFOS.get();
        timingInfoList.add(timingInfo);

        // cleanup
        START_PAGE_LOAD_TIMESTAMP.get().remove(driver);
    }

    public static List<TimingInfo> getPageLoadInfos() {
        return PAGE_LOAD_INFOS.get();
    }

    /**
     * cleans all local threads
     */
    public static void cleanupThreadLocals() {
        START_PAGE_LOAD_TIMESTAMP.remove();
        PAGE_LOAD_INFOS.remove();
    }

    /**
     * loads infos of store page after test method
     *
     * @param iTestResult .
     */
    public static void storePageLoadInfosAfterTestMethod(ITestResult iTestResult) {
        if (PAGE_LOAD_INFOS.get() == null) {
            return;
        }
        List<TimingInfo> timingInfosCopy = new ArrayList<TimingInfo>(PAGE_LOAD_INFOS.get().size());
        timingInfosCopy.addAll(PAGE_LOAD_INFOS.get());


        String threadName = Thread.currentThread().getName();
        Long threadID = Thread.currentThread().getId();

        PerfTestContainer.saveThreadName(threadID, threadName);
        String methodName = iTestResult.getMethod().getMethodName();
        PerfTestContainer.addPageLoadInfo(threadID, timingInfosCopy, methodName);

        //save testResult
        PerfTestContainer.setTestResult(threadID, iTestResult);

        PAGE_LOAD_INFOS.get().clear();
        PAGE_LOAD_INFOS.remove();
    }
}
