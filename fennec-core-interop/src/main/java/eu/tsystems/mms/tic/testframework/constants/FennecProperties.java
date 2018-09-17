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
 *     jhmr <Johannes.Maresch@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/* 
 * Created on 26.03.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.constants;

/**
 * Class holding keys of all properties.
 *
 * @author sepr
 */
public final class FennecProperties {

    /**
     * Hide default constructor.
     */
    private FennecProperties() {
    }

    /**
     * Property to set the browser used by Selenium/ Webdriver.
     */
    public static final String BROWSER = "browser";

    /**
     * Property to set the host of the remote selenium server.
     */
    public static final String SELENIUM_SERVER_URL = "selenium.server.url";
    public static final String SELENIUM_SERVER_HOST = "selenium.server.host";

    /**
     * Property to set the port of the remote selenium server.
     */
    public static final String SELENIUM_SERVER_PORT = "selenium.server.port";

    /**
     * Property to set the location of the chrome driver.
     */
    public static final String CHROMEDRIVER = "webdriver.chrome.driver";

    /**
     * Property to set the location of the edge driver.
     */
    public static final String EDGEDRIVER = "webdriver.edge.driver";

    /**
     * Property to set the location of the ie driver.
     */
    public static final String IEDRIVER = "webdriver.ie.driver";

    /**
     * Property to set the location of the firefox installation.
     */
    public static final String FIREFOXBIN = "webdriver.firefox.bin";

    /**
     * Property Key for package where qcconnector looks for tests.
     */
    public static final String WEBDRIVERMODE = "webdriver.mode";

    /**
     * Property key of baseUrl used by Selenium.
     */
    public static final String BASEURL = "baseURL";

    /**
     * Property key stating to take automatic screenshots or not.
     */
    public static final String AUTOSCREENSHOTS = "fennec.takeAutomaticScreenshot";

    /**
     * Key of reportDir Property.
     */
    public static final String REPORTDIR = "reportDir";
    public static final String REPORTNAME = "reportName";

    /**
     * fennec test property file.
     */
    public static final String TEST_PROPERTIES_FILE = "test.properties.file";

    /**
     * Failed tests maximum number of retries.
     */
    public static final String FAILED_TESTS_MAX_RETRIES = "failed.tests.max.retries";
    public static final String FAILED_TESTS_RETRY_FLAPPING = "failed.tests.retry.flapping";

    /**
     * Failed tests condition: Throwable Class(~es, devided by ','.
     */
    public static final String FAILED_TESTS_IF_THROWABLE_CLASSES = "failed.tests.if.throwable.classes";

    /**
     * Failed tests condition. Throwable Message(~s, devided by ',').
     */
    public static final String FAILED_TESTS_IF_THROWABLE_MESSAGES = "failed.tests.if.throwable.messages";

    /**
     * WDM close windows rule.
     */
    public static final String CLOSE_WINDOWS_AFTER_TEST_METHODS = "wdm.closeWindows.afterTestMethods";

    /**
     * WDM close windows rule.
     */
    public static final String CLOSE_WINDOWS_ON_FAILURE = "wdm.closeWindows.onFailure";

    /**
     * Visually marks every GuiElement that is being processed. Might break a LayoutTest.
     */
    public static final String DEMO_MODE = "demomode";

    /**
     * Element timeout seconds.
     */
    public static final String ELEMENT_TIMEOUT_SECONDS = "element.timeout.seconds";

    /**
     * State condition.
     */
    public static final String ON_STATE_TESTFAILED_SKIP_FOLLOWING_TESTS = "on.state.testfailed.skip.following.tests";
    /**
     * State condition.
     */
    public static final String ON_STATE_TESTFAILED_SKIP_SHUTDOWN = "on.state.testfailed.skip.shutdown";

    /**
     * Dimension of screen
     */
    public static final String SCREEN_SIZE_X = "screen.size.x";
    /**
     * Dimension of screen
     */
    public static final String SCREEN_SIZE_Y = "screen.size.y";

    /**
     * "report.screenshots.preview"
     */
    public static final String REPORT_SCREENSHOTS_PREVIEW = "report.screenshots.preview";

    /**
     * Module source root
     */
    public static final String MODULE_SOURCE_ROOT = "module.source.root";
    /**
     * source.lines.prefetch
     */
    public static final String SOURCE_LINES_PREFETCH = "source.lines.prefetch";
    /**
     * report.activate.sources
     */
    public static final String REPORT_ACTIVATE_SOURCES = "report.activate.sources";

    /**
     * Guielement easy mode.
     */
    public static final String Fennec_GUIELEMENT_TYPE = "fennec.guielement.type";
    public static final String Fennec_GUIELEMENT_DEFAULT_ASSERT_IS_COLLECTOR = "fennec.guielement.default.assertcollector";
    /*
     * Package the project is located in.
     */
    public static final String Fennec_PROJECT_PACKAGE = "fennec.project.package";

    /**
     * Flag for Perf Test Statistics generation.
     */
    public static final String Fennec_PERF_GENERATE_STATISTICS = "fennec.perf.generate.statistics";
    /**
     * Perf test thinktime.
     */
    public static final String Fennec_PERF_PAGE_THINKTIME_MS = "fennec.perf.page.thinktime.ms";
    /** reuse existing driver for a thread of dataprovider */
    public static final String Fennec_REUSE_DATAPROVIDER_DRIVER_BY_THREAD = "fennec.reuse.dataprovider.driver.by.thread";
    /** Perf test Property, used to set default values of a Load test */
    public static final String Fennec_PERF_TEST = "fennec.perf.test";

    /**
     * If true, screenshot after page is loaded will be taken.
     */
    public static final String Fennec_SCREENSHOT_ON_PAGELOAD = "fennec.screenshot.on.pageload";

    public static final String Fennec_DB_TIMEOUT = "fennec.db.timeout";

    public static final String BROWSER_VERSION = "browser.version";

    public static final String Fennec_MONITOR_MEMORY = "fennec.monitor.memory";

    public static final String Fennec_DRY_RUN = "fennec.dryrun";

    /** Execution mode remote or local */
    public static final String SIKULI_MODE = "sikuli.mode";

    /** Port the server and client shall use */
    public static final String SIKULI_SERVER_PORT = "sikuli.server.port";

    /** Address of the server or "localhost" */
    public static final String SIKULI_SERVER_ADDRESS = "sikuli.server.address";

    /** Maximum number of server sessions for nodes */
    public static final String SIKULI_SERVER_NODE_SESSIONS_MAX = "sikuli.server.node.sessions.max";

    public static final String DYNATRACE_LOGGING = "fennec.dynatrace.logging";

    public static final String DASHBOARD_SYNC_ENABLED = "dashboard.sync.enabled";
    public static final String DASHBOARD_URL = "dashboard.url";
    public static final String DASHBOARD_PROJECT_NAME = "dashboard.project.name";

    public static final String GUIELEMENT_CHECK_RULE = "guielement.checkrule";

    public static final String Fennec_REUSE_REPORTDIR = "fennec.reuse.reportdir";

    public static final String SKIP_EXECUTION_WHEN_PREVIOUSLY_PASSED = "skip.execution.when.previously.passed";

    public static final String Fennec_BROWSER_MAXIMIZE = "fennec.browser.maximize";

    public static final String WEBDRIVER_TIMEOUT_SECONDS_PAGELOAD = "webdriver.timeouts.seconds.pageload";
    public static final String WEBDRIVER_TIMEOUT_SECONDS_SCRIPT = "webdriver.timeouts.seconds.script";

    public static final String Fennec_PROXY_SETTINGS_LOAD = "fennec.proxy.settings.load";
    public static final String Fennec_PROXY_SETTINGS_FILE = "fennec.proxy.settings.file";

    public static final String Fennec_REPORT_DATAPROVIDER_INFOS = "fennec.report.dataprovider.infos";

    public static final String Fennec_LIST_TESTS = "fennec.list.tests";

    public static final String DELAY_AFTER_GUIELEMENT_FIND_MILLIS = "fennec.delay.after.guielement.find.millis";
    public static final String DELAY_BEFORE_GUIELEMENT_ACTION_MILLIS = "fennec.delay.before.guielement.action.millis";
    public static final String DELAY_AFTER_GUIELEMENT_ACTION_MILLIS = "fennec.delay.after.guielement.action.millis";

    public static final String Fennec_WEB_TAKE_ACTION_SCREENSHOTS = "fennec.web.take.action.screenshots";

    public static final String Fennec_RUNCFG = "fennec.runcfg";

    public static final String Fennec_WATCHDOG_ENABLE = "fennec.watchdog.enable";
    public static final String Fennec_WATCHDOG_TIMEOUT_SECONDS = "fennec.watchdog.timeout.seconds";

    /*
    Fehlerkorridor
     */
    public static final String Fennec_FAILURE_CORRIDOR_ACTIVE = "fennec.failure.corridor.active";
    public static final String Fennec_FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS = "fennec.failure.corridor.allowed.failed.tests";
    public static final String Fennec_FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_HIGH = "fennec.failure.corridor.allowed.failed.tests.high";
    public static final String Fennec_FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_MID = "fennec.failure.corridor.allowed.failed.tests.mid";
    public static final String Fennec_FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_LOW = "fennec.failure.corridor.allowed.failed.tests.low";

    public static final String Fennec_STITCH_CHROME_SCREENSHOTS = "fennec.stitch.chrome.screenshots";

    public static final String Fennec_KAFKA_ACTIVE = "fennec.kafka.active";

    public static final String Fennec_DISPLAY_RESOLUTION = "fennec.display.resolution";

    public static final String Fennec_PAGE_FACTORY_LOOPS = "fennec.page.factory.loops";
    public static final String Fennec_EXECUTION_OMIT_IN_DEVELOPMENT = "fennec.execution.omit.indevelopment";

    public static final String Fennec_GUIELEMENT_HIGHLIGHTS_ACTIVE = "fennec.guielement.highlights.active";
}
