/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.util.function.Supplier;

/**
 * A lightweight sequence
 * @author Mike Reiche
 */
public class Sequence implements Loggable {
    private long waitMsAfterRun = 0;
    private long timeoutMs = 0;
    private long startTime;

    public long getStartTimeMs() {
        return startTime;
    }

    public Sequence setWaitMsAfterRun(long millis) {
        this.waitMsAfterRun = millis;
        return this;
    }

    public Sequence setTimeoutMs(long millis) {
        this.timeoutMs = millis;
        return this;
    }

    public long getDurationMs() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Runs the supplier in a timer loop and when it returns true, the sequence stops
     */
    public Sequence run(Supplier<Boolean> runnable) {
        try {
            startTime = System.currentTimeMillis();
            do {
                if (runnable.get()) {
                    break;
                }
                Thread.sleep(waitMsAfterRun);
            } while (!timedOut());

        } catch (InterruptedException e) {
            log().error(e.getMessage(), e);
        }
        return this;
    }

    public boolean timedOut() {
        return getDurationMs() >= timeoutMs;
    }
}
