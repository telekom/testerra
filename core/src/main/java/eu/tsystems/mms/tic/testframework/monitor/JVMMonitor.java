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

package eu.tsystems.mms.tic.testframework.monitor;

import eu.tsystems.mms.tic.testframework.events.ITesterraEventType;
import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventListener;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.internal.ConsumptionMeasurementsCollector;
import eu.tsystems.mms.tic.testframework.utils.JVMUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JVMMonitor implements TesterraEventListener {

    private static final int GC_THRESHOLD = 500;
    private static final long MB = 1024 * 1024;
    private static final Logger LOGGER = LoggerFactory.getLogger(JVMMonitor.class);

    private static final Object LOCK = new Object();
    private static final Map<Long, Long> MEASUREMENTS = new LinkedHashMap<>();
    private static final Map<Long, Long> MEASUREMENTS_TOTAL = new LinkedHashMap<>();
    private static final Map<Integer, String> LABELS = new LinkedHashMap<>();

    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final long MAX_MEMORY = RUNTIME.maxMemory() / MB;
    private static long lastValue = 0;
    private static long lastValueTotal = 0;

    private static final long diff = 2;

    private static final ConsumptionMeasurementsCollector cmc = new ConsumptionMeasurementsCollector();
    private static final ConsumptionMeasurementsCollector.ContextMeasurement cmMemUsage =
            new ConsumptionMeasurementsCollector.ContextMeasurement("JVM Memory Usage", "Mem", "MB");
    private static final ConsumptionMeasurementsCollector.ContextMeasurement cmMemTotal =
            new ConsumptionMeasurementsCollector.ContextMeasurement("JVM Memory Reserved", "Mem", "MB");
    private static final ConsumptionMeasurementsCollector.ContextMeasurement cmCPU =
            new ConsumptionMeasurementsCollector.ContextMeasurement("JVM CPU", "CPU", "%");

    static {
        //        cmc.setSquash(true);
        cmMemUsage.setMaxValue(MAX_MEMORY);
        cmMemTotal.setMaxValue(MAX_MEMORY);
    }

    public static void label(String label) {
        getValue(label);

        cmc.addMarker(cmMemUsage, label);
        cmc.addMarker(cmMemTotal, label);
        cmc.addMarker(cmCPU, label);

        // to print start and stop values, too
        getJVMUsageInfo();
    }

    private static boolean isNearLastValue(long usedMem, long total) {
        if (lastValue - diff <= usedMem && usedMem <= (lastValue + diff)) {
            if (lastValueTotal - diff <= total && total <= (lastValueTotal + diff)) {
                return true;
            }
        }
        return false;
    }

    private static long getValue(String labelOrNull) {
        synchronized (LOCK) {
            long total = getTotal();
            long usedMem = (RUNTIME.totalMemory() - RUNTIME.freeMemory()) / MB;

            if (labelOrNull == null) {
                //wenn ohne label, dann nur bei Aenderung hinzufuegen
                if (!isNearLastValue(usedMem, total)) {
                    putValueToMap(usedMem, total);
                }
            } else {
                // wenn mit label, dann ajF hinzufuegen
                putValueToMap(usedMem, total);
                int position = MEASUREMENTS.size() - 1; // must be 1+
                LABELS.put(position, labelOrNull);
            }

            return usedMem;
        }
    }

    private static long getTotal() {
        return RUNTIME.totalMemory() / MB;
    }

    private static void putValueToMap(long value, long total) {
        long timeMillis = System.currentTimeMillis();
        MEASUREMENTS.put(timeMillis, value);
        MEASUREMENTS_TOTAL.put(timeMillis, total);
        lastValue = value;
        lastValueTotal = total;
    }

    public static String getJVMUsageInfo() {
        long usedMem = getValue(null);
        long total = getTotal();
        int cpuUsage = JVMUtils.getCPUUsagePercent();

        // push values to new cm storage
        cmc.addValue(cmMemUsage, usedMem);
        cmc.addValue(cmMemTotal, total);
        cmc.addValue(cmCPU, cpuUsage);

        return "Memory Used: " + usedMem + " MB  Total: " + total + " MB  Max: " + MAX_MEMORY + " MB      CPU usage: " + cpuUsage + " %";
    }

    public static void logJVMUsageInfo() {
        LOGGER.debug(getJVMUsageInfo());
    }

    @Override
    public void fireEvent(TesterraEvent TesterraEvent) {
        ITesterraEventType iTesterraEventType = TesterraEvent.getTesterraEventType();
        if (iTesterraEventType instanceof TesterraEventType) {
            TesterraEventType TesterraEventType = (TesterraEventType) iTesterraEventType;
            switch (TesterraEventType) {
                case TEST_METHOD_START:
                    //                    logJVMUsageInfo();
                    break;
                case TEST_METHOD_END:
                    break;
                case CONFIGURATION_METHOD_START:
                    //                    logJVMUsageInfo();
                    break;
                case CONFIGURATION_METHOD_END:
                    break;
                case TEST_START:
                    break;
                case TEST_END:
                    break;
                case RETRYING_METHOD:
                    break;
                case FIRST_FAILED_TEST:
                    break;
                case TEST_WITH_FILTERED_THROWABLE:
                    break;
            }
        }
    }

    private static boolean threadStop = false;
    private static int sleepTimeInMS = 10000;

    private static Thread MONITOR_THREAD = null;

    private static void init() {
        MONITOR_THREAD = new Thread(() -> {
            LOGGER.debug("Started");
            long start = System.currentTimeMillis();
            long now;
            while (!threadStop) {
                now = System.currentTimeMillis();
                try {
                    Thread.sleep(500); // loop fast
                } catch (InterruptedException e) {
                    // ignore
                }


                if ((now - start >= sleepTimeInMS)) {
                    logJVMUsageInfo();
                    start = System.currentTimeMillis();

                    if (lastValue > GC_THRESHOLD) {
                        LOGGER.debug("Used Mem over threshold (" + GC_THRESHOLD + "), triggering GC");
                        System.gc();
                    }
                }
            }
        });
    }

    public static void start() {
        if (MONITOR_THREAD != null && (MONITOR_THREAD.isAlive() || MONITOR_THREAD.isDaemon())) {
            stop();
        }
        init();
        MONITOR_THREAD.start();
    }

    public static void start(int sleepTimeInMS) {
        JVMMonitor.sleepTimeInMS = sleepTimeInMS;
        start();
    }

    public static void stop() {
        threadStop = true;
        try {
            MONITOR_THREAD.join(30000);
        } catch (InterruptedException e) {
            LOGGER.error("Error joining monitor thread", e);
        }
        logJVMUsageInfo();
        LOGGER.debug("Stopped");
    }

    public static Map<Long, Long> getMeasurements() {
        return MEASUREMENTS;
    }

    public static Map<Long, Long> getMeasurementsTotal() {
        return MEASUREMENTS_TOTAL;
    }

    public static long getMaxMemory() {
        return MAX_MEMORY;
    }

    public static Map<Integer, String> getLabels() {
        return LABELS;
    }

    public static ConsumptionMeasurementsCollector getConsumptionMeasurementsCollector() {
        return cmc;
    }
}
