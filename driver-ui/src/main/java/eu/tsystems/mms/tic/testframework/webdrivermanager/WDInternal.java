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

import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;
import eu.tsystems.mms.tic.testframework.utils.RESTUtils;

/**
 * Created by pele on 07.04.2015.
 */
public class WDInternal {

    public static void cleanupExecutingSeleniumHost() {
        WebDriverManager.EXECUTING_SELENIUM_HOSTS_PER_THREAD.set(null);
        WebDriverManager.EXECUTING_SELENIUM_HOSTS_PER_THREAD.remove();
    }

    /**
     * Cleans EVENTFIRINGWEBDRIVER_MAP and/or SELENIUM_MAP.
     */
    public static void cleanupDriverReferencesInCurrentThread() {
        if (Flags.REUSE_DATAPROVIDER_DRIVER_BY_THREAD) {
            DriverStorage.clearDriverMap();
        }
    }

    public static class Sec1 {
        private Sec1() {
        }

        public Sec2 internal() {
            return new Sec2();
        }
    }
    public static class Sec2 {
        private Sec2() {
        }

        public Sec3 IKnowWhatImDoing() {
            return new Sec3();
        }
    }
    public static class Sec3 {
        private Sec3() {
        }

        public Sec4 IDontBlameAnyoneElseIfSomethingBadHappens() {
            return new Sec4();
        }
    }
    public static class Sec4 {
        private Sec4() {
        }

        public void cleanupBrowserProcesses(String host, String port) {
            RESTUtils.requestPOST("http://" + host + ":" + port + "/ms/cleanup", "f836f307bb3ce081be5e22c2be2d1547");
        }
    }

    public static Sec1 SeleniumSupervisor() {
        return new Sec1();
    }
}
