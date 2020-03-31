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
package eu.tsystems.mms.tic.testframework.execution.worker.start;

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;

public class PerformanceStartWorker extends MethodWorker {
    @Override
    public void run() {
        if (isTest()) {
            /*
             * LoadTest setup
             */
            if (Flags.GENERATE_PERF_STATISTICS) {
                StopWatch.initializeStopWatch();
            }
        }
    }
}
