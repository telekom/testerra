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

/**
 * Class holding keys of all properties.
 * @deprecated Use {@link Properties} instead
 * @todo Move these Properties to {@link Properties}
 */
public final class TesterraProperties {
    /**
     * Hide default constructor.
     */
    private TesterraProperties() {

    }

    /**
     * Failed tests maximum number of retries.
     */
    public static final String FAILED_TESTS_MAX_RETRIES = "tt.failed.tests.max.retries";

    /**
     * Failed tests condition: Throwable Class(~es, devided by ','.
     */
    public static final String FAILED_TESTS_IF_THROWABLE_CLASSES = "tt.failed.tests.if.throwable.classes";

    /**
     * Failed tests condition. Throwable Message(~s, devided by ',').
     */
    public static final String FAILED_TESTS_IF_THROWABLE_MESSAGES = "tt.failed.tests.if.throwable.messages";

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
     */
    public static final String WEBDRIVER_WINDOW_SWITCH_MAX_DURATION = "tt.wdm.timeouts.seconds.window.switch.duration";

    /**
     * WDM: Kill WebDriver on stucking selenium command after this.
     */
    public static final String WEBDRIVER_TIMEOUT_SECONDS_STUCK_COMMAND = "tt.wdm.timeouts.seconds.selenium.command.stuck";

    /**
     * State condition.
     */
    public static final String ON_STATE_TESTFAILED_SKIP_FOLLOWING_TESTS = "tt.on.state.testfailed.skip.following.tests";
    /**
     * State condition.
     */
    public static final String ON_STATE_TESTFAILED_SKIP_SHUTDOWN = "tt.on.state.testfailed.skip.shutdown";

    /**
     * Module source root
     */
    public static final String MODULE_SOURCE_ROOT = "tt.module.source.root";
    /**
     * tt.source.lines.prefetch
     */
    public static final String SOURCE_LINES_PREFETCH = "tt.source.lines.prefetch";

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

    /**
     * If true, screenshot after page is loaded will be taken.
     */
    public static final String SCREENSHOT_ON_PAGELOAD = "tt.screenshot.on.pageload";

    public static final String BROWSER_MAXIMIZE = "tt.browser.maximize";
    public static final String BROWSER_MAXIMIZE_POSITION = "tt.browser.maximize.position";

    public static final String SYSTEM_SETTINGS_FILE = "tt.system.settings.file";

    public static final String RUNCFG = "tt.runcfg";

    public static final String WATCHDOG_ENABLE = "tt.watchdog.enable";
    public static final String WATCHDOG_TIMEOUT_SECONDS = "tt.watchdog.timeout.seconds";

    public static final String SCREENCASTER_ACTIVE_ON_SUCCESS = "tt.screencaster.active.on.success";
    public static final String SCREENCASTER_ACTIVE_ON_FAILED = "tt.screencaster.active.on.failed";
}
