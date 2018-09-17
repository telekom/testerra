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
package eu.tsystems.mms.tic.testframework.internal.utils;

import eu.tsystems.mms.tic.testframework.internal.TimingInfo;
import eu.tsystems.mms.tic.testframework.utils.NumberUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.*;

public class TimingInfosCollector {

    private List<TimingInfo> timingInfos = Collections.synchronizedList(new LinkedList<>());
    private Map<String, StopWatch> stopWatches = Collections.synchronizedMap(new HashMap<>());
    private boolean stopped = false;

    public void start(String context) {
        if (stopped) {
            return;
        }
        StopWatch stopWatch = StopWatch.createStarted();
        stopWatches.put(context, stopWatch);
    }

    public void stop(String context) {
        if (stopped) {
            return;
        }
        pStop(context);
    }

    public void add(final TimingInfo timingInfo) {
        timingInfos.add(timingInfo);
    }

    private void pStop(String context) {
        StopWatch stopWatch = stopWatches.get(context);
        if (stopWatch != null) {
            stopWatch.stop();
            long time = stopWatch.getTime();
            timingInfos.add(new TimingInfo(context, "", time, System.currentTimeMillis()));
            stopWatches.remove(context);
        }
    }

    private Calculations calculations = new Calculations();

    public void terminate() {
        stopped = true;

        // synchronize just to wait for access
        synchronized (stopWatches) {
            for (String context : stopWatches.keySet()) {
                stopWatches.get(context).stop();
            }
        }
    }

    public Calculations calculate() {
        /*
         generate context maps
          */
        Map<String, List<Long>> times = new LinkedHashMap<>();
        Map<String, List<TimingInfo>> timingInfosPerContext = new LinkedHashMap<>();

        // create empty lists
        timingInfos.stream().forEach(i -> {
            String context = i.getContext();
            if (!times.containsKey(context)) {
                times.put(context, new LinkedList<>());
                timingInfosPerContext.put(context, new LinkedList<>());
            }
        });
        // push values to lists
        timingInfos.stream().forEach(i -> {
            String context = i.getContext();
            long loadDuration = i.getLoadDuration();
            times.get(context).add(loadDuration);
            timingInfosPerContext.get(context).add(i);
        });

        /*
         calculate
          */
        Map<String, Long> minValuesPerContext = new LinkedHashMap<>();
        Map<String, Long> avgValuesPerContext = new LinkedHashMap<>();
        Map<String, Long> maxValuesPerContext = new LinkedHashMap<>();

        times.keySet().stream().forEach(context -> {
            List<Long> measurements = times.get(context);
            Long minValue = NumberUtils.getMinValue(measurements);
            Long avgValue = NumberUtils.getAverageValue(measurements);
            Long maxValue = NumberUtils.getMaxValue(measurements);

            minValuesPerContext.put(context, minValue);
            avgValuesPerContext.put(context, avgValue);
            maxValuesPerContext.put(context, maxValue);
        });

        calculations.minPerActions = minValuesPerContext;
        calculations.avgPerActions = avgValuesPerContext;
        calculations.maxPerActions = maxValuesPerContext;
        calculations.timingInfosPerAction = timingInfosPerContext;
        calculations.empty = false;

        return calculations;
    }

    public Calculations getCalculations() {
        return calculations;
    }

    public void announceTimingInfos(Collection<? extends TimingInfo> timingInfosToMerge) {
        timingInfos.addAll(timingInfosToMerge);
    }

    public static class Calculations {
        Map<String, Long> minPerActions;
        Map<String, Long> avgPerActions;
        Map<String, Long> maxPerActions;
        Map<String, List<TimingInfo>> timingInfosPerAction;
        boolean empty = true;

        public Map<String, Long> getMinPerActions() {
            return minPerActions;
        }

        public Map<String, Long> getAvgPerActions() {
            return avgPerActions;
        }

        public Map<String, Long> getMaxPerActions() {
            return maxPerActions;
        }

        public Map<String, List<TimingInfo>> getTimingInfosPerAction() {
            return timingInfosPerAction;
        }

        public boolean isEmpty() {
            return empty;
        }
    }

}
