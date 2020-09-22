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
 * A lightweight timer
 * @author Mike Reiche
 */
public class TinyTimer implements Loggable {
    private long pauseMs = 0;
    private long periodMs = 0;

    public long getPauseMs() {
        return pauseMs;
    }

    public TinyTimer setPauseMs(long pauseMs) {
        this.pauseMs = pauseMs;
        return this;
    }

    public long getPeriodMs() {
        return periodMs;
    }

    public TinyTimer setPeriodMs(long periodMs) {
        this.periodMs = periodMs;
        return this;
    }

    /**
     * Runs the supplier in a timer loop and when it returns true, the timer stops
     */
    public void run(Supplier<Boolean> runnable) {
        try {
            long startTimeMs = System.currentTimeMillis();

            do {
                if (runnable.get()) {
                    break;
                }
                Thread.sleep(pauseMs);
            } while ((System.currentTimeMillis()-startTimeMs) < periodMs);

        } catch (InterruptedException e) {
            log().error(e.getMessage(), e);
        }
    }
}
