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
package eu.tsystems.mms.tic.testframework.constants;

import eu.tsystems.mms.tic.testframework.common.Testerra.Properties;

import java.util.function.Predicate;

/**
 * Class holding keys of all properties.
 *
 * @todo Move these Properties to {@link Properties}
 * @deprecated Use {@link Properties} instead
 */
public final class TesterraProperties {
    /**
     * Hide default constructor.
     */
    private TesterraProperties() {

    }

    /**
     * WDM close windows rule.
     */
    public static final String CLOSE_WINDOWS_AFTER_TEST_METHODS = "tt.wdm.closewindows.aftertestmethods";

    /**
     * WDM close windows rule.
     */
    public static final String CLOSE_WINDOWS_ON_FAILURE = "tt.wdm.closewindows.onfailure";

    /**
     * WDM: Timeout / Duration Setting for Window Switching
     *
     * @deprecated Use {@link IWebDriverManager#switchToWindow(Predicate)} and {@link CONTROL#waitFor(int, Predicate)} instead
     */
    @Deprecated
    public static final String WEBDRIVER_WINDOW_SWITCH_MAX_DURATION = "tt.wdm.timeouts.seconds.window.switch.duration";

    /**
     * State condition.
     */
    public static final String ON_STATE_TESTFAILED_SKIP_FOLLOWING_TESTS = "tt.on.state.testfailed.skip.following.tests";
    /**
     * State condition.
     */
    public static final String ON_STATE_TESTFAILED_SKIP_SHUTDOWN = "tt.on.state.testfailed.skip.shutdown";

    /*
     * Package the project is located in.
     */
    @Deprecated
    public static final String PROJECT_PACKAGE = "tt.project.package";

    /**
     * Perf test thinktime.
     */
    @Deprecated
    public static final String PERF_PAGE_THINKTIME_MS = "tt.perf.page.thinktime.ms";

    public static final String SYSTEM_SETTINGS_FILE = "tt.system.settings.file";

    public static final String RUNCFG = "tt.runcfg";

    public static final String WATCHDOG_ENABLE = "tt.watchdog.enable";
    public static final String WATCHDOG_TIMEOUT_SECONDS = "tt.watchdog.timeout.seconds";

    /**
     * @deprecated Use {@link Properties#SELENIUM_SERVER_URL} instead
     */
    public static final String SELENIUM_SERVER_URL = Properties.SELENIUM_SERVER_URL.toString();

    /**
     * @deprecated Use {@link Properties#WEBDRIVER_MODE} instead
     */
    public static final String WEBDRIVERMODE = Properties.WEBDRIVER_MODE.toString();
}
