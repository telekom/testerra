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
 package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public final class Flags {

    private Flags() {
    }

    private static boolean p(String property, boolean defaultValue) {
        return PropertyManager.getBooleanProperty(property, defaultValue);
    }

    public static boolean REPORT_SCREENSHOTS_PREVIEW = p(TesterraProperties.REPORT_SCREENSHOTS_PREVIEW, true);
    public static boolean GENERATE_PERF_STATISTICS = p(TesterraProperties.PERF_GENERATE_STATISTICS, false);
    public static boolean PERF_STOP_WATCH_ACTIVE = false;
    public static boolean REUSE_DATAPROVIDER_DRIVER_BY_THREAD = p(TesterraProperties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD, false);
    public static boolean DRY_RUN = p(TesterraProperties.DRY_RUN, false);
    public static boolean LIST_TESTS = p(TesterraProperties.LIST_TESTS, false);
    public static boolean FAILURE_CORRIDOR_ACTIVE = p(TesterraProperties.FAILURE_CORRIDOR_ACTIVE, true);
    public static boolean EXECUTION_OMIT_IN_DEVELOPMENT = p(TesterraProperties.EXECUTION_OMIT_IN_DEVELOPMENT, false);
    public static boolean GUIELEMENT_DEFAULT_ASSERT_IS_COLLECTOR = p(TesterraProperties.GUIELEMENT_DEFAULT_ASSERT_IS_COLLECTOR, false);
    @Deprecated
    public static boolean GUIELEMENT_USE_JS_ALTERNATIVES = p(TesterraProperties.GUIELEMENT_USE_JS_ALTERNATIVES, false);

    public static boolean SCREENSHOTTER_ACTIVE = p(TesterraProperties.SCREENSHOTTER_ACTIVE, true);
    public static boolean SCREENCASTER_ACTIVE = p(TesterraProperties.SCREENCASTER_ACTIVE, true);
}
