/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;

public class WDInternal {

    private WDInternal() {

    }

    public static void cleanupExecutingSeleniumHost() {
        WebDriverManager.EXECUTING_SELENIUM_HOSTS_PER_THREAD.set(null);
        WebDriverManager.EXECUTING_SELENIUM_HOSTS_PER_THREAD.remove();
    }

    /**
     * Cleans EVENTFIRINGWEBDRIVER_MAP and/or SELENIUM_MAP.
     */
    public static void cleanupDriverReferencesInCurrentThread() {
        if (Testerra.Properties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD.asBool()) {
            DriverStorage.clearDriverMap();
        }
    }

}
