/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.report;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatusCounter {
    private final Map<TestStatusController.Status, Integer> statusCounts = new ConcurrentHashMap<>();

    public int get(TestStatusController.Status status) {
        return statusCounts.getOrDefault(status, 0);
    }

    public int getSum(TestStatusController.Status[] statuses) {
        return Arrays.stream(statuses).mapToInt(this::get).sum();
    }

    public void increment(TestStatusController.Status status) {
        int statusCount = get(status);
        statusCount++;
        statusCounts.put(status, statusCount);
    }
}
