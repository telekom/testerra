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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pele on 23.11.2016.
 */
public final class TimingConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimingConstants.class);

    public static final int WATCHDOG_THREAD_POLL_INTERVAL_SECONDS = 10;

    public static final int WEBDRIVER_COMMAND_TIMEOUT_SECONDS = PropertyManager.getIntProperty(FennecProperties.Fennec_WATCHDOG_TIMEOUT_SECONDS, 600);
    public static final int WATCHDOG_FIRST_ANNOUNCEMENT_SECONDS;
    public static final int WATCHDOG_THREAD_HANGING_TIMEOUT_SECONDS;
    public static final int WATCHDOG_FORCE_QUIT_TIMEOUT_SECONDS;

    static {
        if (WEBDRIVER_COMMAND_TIMEOUT_SECONDS < 2 * 60) {
            String msg = "\n\nWatchDog timeout to low: " + WEBDRIVER_COMMAND_TIMEOUT_SECONDS + " Set at least to 2 minutes.\n\n";
            System.err.println(msg);
            throw new FennecRuntimeException(msg);
        }
        WATCHDOG_FIRST_ANNOUNCEMENT_SECONDS = WEBDRIVER_COMMAND_TIMEOUT_SECONDS / 2;
        WATCHDOG_THREAD_HANGING_TIMEOUT_SECONDS = WEBDRIVER_COMMAND_TIMEOUT_SECONDS - 20;
        WATCHDOG_FORCE_QUIT_TIMEOUT_SECONDS = WEBDRIVER_COMMAND_TIMEOUT_SECONDS + 120;

        LOGGER.info("WatchDog Timings:" +
                "\n FlagUp:    " + WATCHDOG_THREAD_HANGING_TIMEOUT_SECONDS + "s" +
                "\n SelfStop:  " + WEBDRIVER_COMMAND_TIMEOUT_SECONDS + "s" +
                "\n ForceQuit: " + WATCHDOG_FORCE_QUIT_TIMEOUT_SECONDS + "s");
    }

}
