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
    public static final String BROWSER = "fennec.browser";
    public static final String BROWSER_VERSION = "fennec.browser.version";

    // BROWSER_SETTING syntax: browser:version
    // BROWSER_SETTING overrides BROWSER and BROWSER_VERSION
    public static final String BROWSER_SETTING = "fennec.browser.setting";

    /**
     * Property to set the host of the remote selenium server.
     */
    public static final String SELENIUM_SERVER_URL = "fennec.selenium.server.url";
    public static final String SELENIUM_SERVER_HOST = "fennec.selenium.server.host";

    /**
     * Property to set the port of the remote selenium server.
     */
    public static final String SELENIUM_SERVER_PORT = "fennec.selenium.server.port";

    /**
     * Property Key for package where qcconnector looks for tests.
     */
    public static final String WEBDRIVERMODE = "fennec.webdriver.mode";

    /**
     * Property key of baseUrl used by Selenium.
     */
    public static final String BASEURL = "fennec.baseurl";

    /**
     * Property key stating to take automatic screenshots or not.
     */
    public static final String AUTOSCREENSHOTS = "fennec.takeAutomaticScreenshot";

    /**
     * Key of reportDir Property.
     */
    public static final String REPORTDIR = "fennec.reportdir";
    public static final String REPORTNAME = "fennec.reportname";

    /**
     * fennec test property file.
     */
    public static final String TEST_PROPERTIES_FILE = "fennec.test.properties.file";

    /**
     * Failed tests maximum number of retries.
     */
    public static final String FAILED_TESTS_MAX_RETRIES = "fennec.failed.tests.max.retries";
    public static final String FAILED_TESTS_RETRY_FLAPPING = "fennec.failed.tests.retry.flapping";

    /**
     * Failed tests condition: Throwable Class(~es, devided by ','.
     */
    public static final String FAILED_TESTS_IF_THROWABLE_CLASSES = "fennec.failed.tests.if.throwable.classes";

    /**
     * Failed tests condition. Throwable Message(~s, devided by ',').
     */
    public static final String FAILED_TESTS_IF_THROWABLE_MESSAGES = "fennec.failed.tests.if.throwable.messages";

    /**
     * WDM close windows rule.
     */
    public static final String CLOSE_WINDOWS_AFTER_TEST_METHODS = "fennec.wdm.closewindows.aftertestmethods";

    /**
     * WDM close windows rule.
     */
    public static final String CLOSE_WINDOWS_ON_FAILURE = "fennec.wdm.closewindows.onfailure";

    /**
     * Visually marks every GuiElement that is being processed. Might break a LayoutTest.
     */
    public static final String DEMO_MODE = "fennec.demomode";

    /**
     * Element timeout seconds.
     */
    public static final String ELEMENT_TIMEOUT_SECONDS = "fennec.element.timeout.seconds";

    /**
     * State condition.
     */
    public static final String ON_STATE_TESTFAILED_SKIP_FOLLOWING_TESTS = "fennec.on.state.testfailed.skip.following.tests";
    /**
     * State condition.
     */
    public static final String ON_STATE_TESTFAILED_SKIP_SHUTDOWN = "fennec.on.state.testfailed.skip.shutdown";

    public static final String REPORT_SCREENSHOTS_PREVIEW = "fennec.report.screenshots.preview";

    /**
     * Module source root
     */
    public static final String MODULE_SOURCE_ROOT = "fennec.module.source.root";
    /**
     * fennec.source.lines.prefetch
     */
    public static final String SOURCE_LINES_PREFETCH = "fennec.source.lines.prefetch";
    /**
     * fennec.report.activate.sources
     */
    public static final String REPORT_ACTIVATE_SOURCES = "fennec.report.activate.sources";

    /**
     * Guielement easy mode.
     */
    public static final String GUIELEMENT_TYPE = "fennec.guielement.type";
    public static final String GUIELEMENT_DEFAULT_ASSERT_IS_COLLECTOR = "fennec.guielement.default.assertcollector";
    /*
     * Package the project is located in.
     */
    public static final String PROJECT_PACKAGE = "fennec.project.package";

    /**
     * Flag for Perf Test Statistics generation.
     */
    public static final String PERF_GENERATE_STATISTICS = "fennec.perf.generate.statistics";
    /**
     * Perf test thinktime.
     */
    public static final String PERF_PAGE_THINKTIME_MS = "fennec.perf.page.thinktime.ms";
    /** reuse existing driver for a thread of dataprovider */
    public static final String REUSE_DATAPROVIDER_DRIVER_BY_THREAD = "fennec.reuse.dataprovider.driver.by.thread";
    /** Perf test Property, used to set default values of a Load test */
    public static final String PERF_TEST = "fennec.perf.test";

    /**
     * If true, screenshot after page is loaded will be taken.
     */
    public static final String SCREENSHOT_ON_PAGELOAD = "fennec.screenshot.on.pageload";

    public static final String DB_TIMEOUT = "fennec.db.timeout";

    public static final String MONITOR_MEMORY = "fennec.monitor.memory";

    public static final String DRY_RUN = "fennec.dryrun";

    public static final String GUIELEMENT_CHECK_RULE = "fennec.guielement.checkrule";

    public static final String SKIP_EXECUTION_WHEN_PREVIOUSLY_PASSED = "fennec.skip.execution.when.previously.passed";

    public static final String BROWSER_MAXIMIZE = "fennec.browser.maximize";

    public static final String WEBDRIVER_TIMEOUT_SECONDS_PAGELOAD = "webdriver.timeouts.seconds.pageload";
    public static final String WEBDRIVER_TIMEOUT_SECONDS_SCRIPT = "webdriver.timeouts.seconds.script";

    public static final String PROXY_SETTINGS_LOAD = "fennec.proxy.settings.load";
    public static final String PROXY_SETTINGS_FILE = "fennec.proxy.settings.file";

    public static final String REPORT_DATAPROVIDER_INFOS = "fennec.report.dataprovider.infos";

    public static final String LIST_TESTS = "fennec.list.tests";

    public static final String DELAY_AFTER_GUIELEMENT_FIND_MILLIS = "fennec.delay.after.guielement.find.millis";
    public static final String DELAY_BEFORE_GUIELEMENT_ACTION_MILLIS = "fennec.delay.before.guielement.action.millis";
    public static final String DELAY_AFTER_GUIELEMENT_ACTION_MILLIS = "fennec.delay.after.guielement.action.millis";

    public static final String WEB_TAKE_ACTION_SCREENSHOTS = "fennec.web.take.action.screenshots";

    public static final String RUNCFG = "fennec.runcfg";

    public static final String WATCHDOG_ENABLE = "fennec.watchdog.enable";
    public static final String WATCHDOG_TIMEOUT_SECONDS = "fennec.watchdog.timeout.seconds";

    /*
    failure corridor
     */
    public static final String FAILURE_CORRIDOR_ACTIVE = "fennec.failure.corridor.active";
    public static final String FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS = "fennec.failure.corridor.allowed.failed.tests";
    public static final String FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_HIGH = "fennec.failure.corridor.allowed.failed.tests.high";
    public static final String FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_MID = "fennec.failure.corridor.allowed.failed.tests.mid";
    public static final String FAILURE_CORRIDOR_ALLOWED_FAILED_TESTS_LOW = "fennec.failure.corridor.allowed.failed.tests.low";

    public static final String STITCH_CHROME_SCREENSHOTS = "fennec.stitch.chrome.screenshots";

    public static final String DISPLAY_RESOLUTION = "fennec.display.resolution";

    public static final String PAGE_FACTORY_LOOPS = "fennec.page.factory.loops";
    public static final String EXECUTION_OMIT_IN_DEVELOPMENT = "fennec.execution.omit.indevelopment";

    public static final String GUIELEMENT_HIGHLIGHTS_ACTIVE = "fennec.guielement.highlights.active";
    
    /*
    layout stuff
     */

    /*
     * The mode for the screenreferencer. Values are pixel (default) or annotated.
     */
    public static final String LAYOUTCHECK_MODE = "fennec.layoutcheck.mode";
    // if true, will use non-functional asserts
    public static final String LAYOUTCHECK_ASSERT_INFO_MODE = "fennec.layoutcheck.assert.info.mode";

    public static final String LAYOUTCHECK_TAKEREFERENCE = "fennec.layoutcheck.takereference";
    public static final String LAYOUTCHECK_REFERENCE_NAMETEMPLATE = "fennec.layoutcheck.reference.nametemplate";
    public static final String LAYOUTCHECK_ANNOTATED_NAMETEMPLATE = "fennec.layoutcheck.annotated.nametemplate";
    public static final String LAYOUTCHECK_ANNOTATIONDATA_NAMETEMPLATE = "fennec.layoutcheck.annotationdata.nametemplate";
    public static final String LAYOUTCHECK_ACTUAL_NAMETEMPLATE = "fennec.layoutcheck.actual.nametemplate";
    public static final String LAYOUTCHECK_DISTANCE_NAMETEMPLATE = "fennec.layoutcheck.distance.nametemplate";
    public static final String LAYOUTCHECK_REFERENCE_PATH = "fennec.layoutcheck.reference.path";
    public static final String LAYOUTCHECK_DISTANCE_PATH = "fennec.layoutcheck.distance.path";
    public static final String LAYOUTCHECK_ACTUAL_PATH = "fennec.layoutcheck.actual.path";
    public static final String LAYOUTCHECK_USE_IGNORE_COLOR = "fennec.layoutcheck.use.ignore.color";
    public static final String LAYOUTCHECK_PIXEL_RGB_DEVIATION_PERCENT = "fennec.layout.pixel.rgb.deviation.percent";

    // Properties for the layout comparator working with
    public static final String LAYOUTCHECK_MATCH_THRESHOLD = "fennec.layoutcheck.match.threshold";
    public static final String LAYOUTCHECK_DISPLACEMENT_THRESHOLD = "fennec.layoutcheck.displacement.threshold";
    public static final String LAYOUTCHECK_INTRA_GROUPING_THRESHOLD = "fennec.layoutcheck.intra.grouping.threshold";
    public static final String LAYOUTCHECK_MINIMUM_MARKED_PIXELS = "fennec.layoutcheck.minimum.marked.pixels";
    public static final String LAYOUTCHECK_MAXIMUM_MARKED_PIXELS_RATIO = "fennec.layoutcheck.maximum.marked.pixels.ratio";
    public static final String LAYOUTCHECK_MATCHING_ALGORITHM = "fennec.layoutcheck.matching.algorithm";
    /**
     * minimalDistanceBetweenMatches
     */
    public static final String LAYOUTCHECK_INTERNAL_PARAMETER_1 = "fennec.layoutcheck.internal.parameter.1";
    /**
     * minimalSizeDifferenceOfSubImages
     */
    public static final String LAYOUTCHECK_INTERNAL_PARAMETER_2 = "fennec.layoutcheck.internal.parameter.2";
    /**
     * minimumSimilarMovementErrorsForDisplacementCorrection
     */
    public static final String LAYOUTCHECK_INTERNAL_PARAMETER_3 = "fennec.layoutcheck.internal.parameter.3";
    /**
     * distanceBetweenMultipleMatchesToProduceWarning
     */
    public static final String LAYOUTCHECK_INTERNAL_PARAMETER_4 = "fennec.layoutcheck.internal.parameter.4";
    public static final String LAYOUTCHECK_IGNORE_AMBIGUOUS_MOVEMENT = "fennec.layoutcheck.ignore.ambiguous.movement";
    public static final String LAYOUTCHECK_IGNORE_MOVEMENT = "fennec.layoutcheck.ignore.movement";
    public static final String LAYOUTCHECK_IGNORE_GROUP_MOVEMENT = "fennec.layoutcheck.ignore.group.movement";
    public static final String LAYOUTCHECK_IGNORE_MISSING_ELEMENTS = "fennec.layoutcheck.ignore.missing.elements";
    public static final String LAYOUTCHECK_IGNORE_AMBIGUOUS_MATCH = "fennec.layoutcheck.ignore.ambiguous.match";

    /**
     * If below 1, the value is regarded as percent threshold for erroneous pixels / all edge and text pixel. If 1 or
     * greater, it is regarded as absolute error pixel count.
     */
    public static final String LAYOUTCHECK_TEXT_ERRORDETECTOR_ERROR_THRESHOLD = "fennec.layoutcheck.text.error.detector.error.threshold";
    public static final String LAYOUTCHECK_TEXT_ERRORDETECTOR_MINIMAL_LINELENGTH = "fennec.layoutcheck.text.error.detector.minimal.line.length";
    public static final String LAYOUTCHECK_TEXT_ERRORDETECTOR_MINIMAL_EDGESTRENGTH = "fennec.layoutcheck.text.error.detector.minimal.edge.strength";
    
}
