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
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;

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
public class MetricsController implements Loggable {

    private final Map<SessionContext, Map<MetricsType, TimeInfo>> sessionMetrics = new ConcurrentHashMap<>();
    // private final Map<MethodContext, Map<MetricsType, TimeInfo>> methodMetrics = new ConcurrentHashMap<>();
    // page timings

    private static MetricsController instance;

    private MetricsController() {

    }

    public static MetricsController get() {
        if (instance == null) {
            instance = new MetricsController();
        }
        return instance;
    }

    public void start(SessionContext context, MetricsType type) {
        TimeInfo timeInfo = new TimeInfo();
        this.addMetric(context, type, timeInfo);
    }

    public void stop(SessionContext context, MetricsType type) {
        Map<MetricsType, TimeInfo> entry = this.sessionMetrics.get(context);
        if (entry != null) {
            entry.get(type).finish();
        } else {
            log().warn("Cannot stop time: There is no entry for {} - {}", context.getName(), type.toString());
        }
    }

    public TimeInfo readStopWatch(SessionContext context, MetricsType type) {
        Map<MetricsType, TimeInfo> entry = this.sessionMetrics.get(context);
        if (entry != null) {
            return entry.get(type);
        }
        return null;
    }

    public Duration readDuration(SessionContext context, MetricsType type) {
        Map<MetricsType, TimeInfo> entry = this.sessionMetrics.get(context);
        if (entry != null && entry.get(type) != null) {
            TimeInfo timeInfo = entry.get(type);
            return Duration.between(timeInfo.getStartTime(), timeInfo.getEndTime());
        } else {
            return Duration.ZERO;
        }

    }

    public void addMetric(SessionContext context, MetricsType type, TimeInfo timeInfo) {
        Map<MetricsType, TimeInfo> mapEntry;
        if (this.sessionMetrics.containsKey(context)) {
            mapEntry = this.sessionMetrics.get(context);
        } else {
            mapEntry = new HashMap<>();
            this.sessionMetrics.put(context, mapEntry);
        }
        mapEntry.put(type, timeInfo);
    }

    public Stream<Map.Entry<SessionContext, Map<MetricsType, TimeInfo>>> readSessionMetrics() {
        return this.sessionMetrics.entrySet().stream();
    }

}
