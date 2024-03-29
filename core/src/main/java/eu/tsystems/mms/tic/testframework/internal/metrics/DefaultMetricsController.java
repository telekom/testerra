/*
 * Testerra
 *
 * (C) 2023, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.internal.metrics;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created on 2023-08-23
 *
 * @author mgn
 */
public class DefaultMetricsController implements MetricsController, Loggable {

    private final Map<Measurable, Map<MetricsType, TimeInfo>> metrics = new ConcurrentHashMap<>();
    // page timings

    @Override
    public void start(Measurable measurable, MetricsType type) {
        TimeInfo timeInfo = new TimeInfo();
        this.addMetric(measurable, type, timeInfo);
    }

    @Override
    public void stop(Measurable measurable, MetricsType type) {
        Map<MetricsType, TimeInfo> entry = this.metrics.get(measurable);
        if (entry != null) {
            entry.get(type).finish();
        } else {
            log().warn("Cannot stop time: There is no entry for {} - {}", measurable.getClass(), type.toString());
        }
    }

    @Override
    public Duration getDuration(Measurable measurable, MetricsType type) {
        Map<MetricsType, TimeInfo> entry = this.metrics.get(measurable);
        if (entry != null && entry.get(type) != null) {
            TimeInfo timeInfo = entry.get(type);
            return Duration.between(timeInfo.getStartTime(), timeInfo.getEndTime());
        } else {
            return Duration.ZERO;
        }

    }

    @Override
    public void addMetric(Measurable measurable, MetricsType type, TimeInfo timeInfo) {
        Map<MetricsType, TimeInfo> mapEntry;
        if (this.metrics.containsKey(measurable)) {
            mapEntry = this.metrics.get(measurable);
        } else {
            mapEntry = new HashMap<>();
            this.metrics.put(measurable, mapEntry);
        }
        mapEntry.put(type, timeInfo);
    }

    @Override
    public Stream<Map.Entry<Measurable, Map<MetricsType, TimeInfo>>> readMetrics() {
        return this.metrics.entrySet().stream();
    }

}
