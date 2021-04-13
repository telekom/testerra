/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems MMS GmbH, Deutsche Telekom AG
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
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;

public interface Sleepy extends Loggable {
    /**
     * Logs and sleeps for {@link UiElement.Properties#ELEMENT_WAIT_INTERVAL_MS}
     */
    default void sleep() {
        sleep(UiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong());
    }

    /**
     * Logs and sleeps by given milliseconds
     */
    default void sleep(long ms) {
        log().info(String.format("sleep(%dms) on %s", ms, this.toString()));
        TimerUtils.sleepSilent(ms);
    }
}
