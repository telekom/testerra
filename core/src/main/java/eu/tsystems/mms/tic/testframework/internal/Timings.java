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
package eu.tsystems.mms.tic.testframework.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Timings {

    public static final long LARGE_LIMIT = 2000;

    public static final Map<Integer, Long> TIMING_GUIELEMENT_FIND = new ConcurrentHashMap<Integer, Long>();
    public static final Map<Integer, Long> TIMING_GUIELEMENT_FIND_WITH_PARENT = new ConcurrentHashMap<Integer, Long>();

    private static int findCounter = 0;

    private Timings() {

    }

    public static synchronized int raiseFindCounter() {
        findCounter++;
        return findCounter;
    }

    public static int getFindCounter() {
        return findCounter;
    }
}
