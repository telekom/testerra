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

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatusCounter {
    private final Map<Status, Integer> statusCounts = new ConcurrentHashMap<>();

    private final Map<String, Status> methodMaps = new ConcurrentHashMap<>();

    public int get(Status status) {
        return Collections.frequency(this.methodMaps.values(), status);
    }

    public int getSum(Status[] statuses) {
        return Arrays.stream(statuses).mapToInt(this::get).sum();
    }

    public void increment(MethodContext context) {
        // Create unique identifier for status counter
        // The status can be changed through RetryAnalyzer, so this method is called twice for this identifier
        final String identifier = context.getClassContext().getName() + "." + context.getName() + "_" + context.getMethodRunIndex();
        final Status status = context.getStatus();

        methodMaps.put(identifier, status);
    }
}
