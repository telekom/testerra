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

import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created on 2023-10-19
 *
 * @author mgn
 */
public interface MetricsController {

    void start(Measurable measurable, MetricsType type);

    void stop(Measurable measurable, MetricsType type);

    Duration getDuration(Measurable measurable, MetricsType type);

    void addMetric(Measurable measurable, MetricsType type, TimeInfo timeInfo);

    Stream<Map.Entry<Measurable, Map<MetricsType, TimeInfo>>> readMetrics();

}
