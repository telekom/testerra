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
 package eu.tsystems.mms.tic.testframework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TimerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerUtils.class);

    private TimerUtils() {}

    /**
     * Sleep like Thread.sleep and catch InterruptedException.
     *
     * @param milliSeconds ms to sleep.
     */
    public static void sleep(final int milliSeconds) {
        sleep(milliSeconds, null);
    }

    public static void sleep(final int milliSeconds, final String comment) {
        String msg = "Sleeping " + milliSeconds + " ms";

        if (comment != null) {
            msg += " " + comment;
        }

        LOGGER.info(msg);
        sleepSilent(milliSeconds);
    }

    public static void sleepSilent(final int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (final InterruptedException e) {
            LOGGER.error("Sleeping was interrupted", e);
        }
    }
}
