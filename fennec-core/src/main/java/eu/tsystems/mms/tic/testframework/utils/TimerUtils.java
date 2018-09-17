/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.tsystems.mms.tic.testframework.utils.timespans.TimeSpan;

/**
 * Created by pele on 05.02.2015.
 */
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

    /**
     * Sleep for an amount of intervals, logging the sleeping of each interval.
     *
     * @param timeSpan An object representing a number of time intervals.
     */
    public static void sleepWithLogging(TimeSpan timeSpan) {
        int currentTimeValue = 0;
        LOGGER.info("Starting wait for " + timeSpan + ".");
        while (currentTimeValue < timeSpan.value) {
            try {
                Thread.sleep(timeSpan.milliseconds);
            } catch (final InterruptedException e) {
                LOGGER.error("Sleeping was interrupted.", e);
            }
            currentTimeValue++;
            LOGGER.info("Waited for " + currentTimeValue + " of " + timeSpan + ".");
        }
    }

    public static void sleepSilent(final int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (final InterruptedException e) {
            LOGGER.error("Sleeping was interrupted", e);
        }
    }
}
