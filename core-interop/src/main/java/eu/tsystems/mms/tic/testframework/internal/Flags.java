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
/*
 * Created on 24.02.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;

/**
 * @deprecated These flags should be used where needed, not initialized by default
 */
@Deprecated
public final class Flags {

    private Flags() {
    }

    private static boolean p(String property, boolean defaultValue) {
        return PropertyManager.getBooleanProperty(property, defaultValue);
    }

    public static boolean GENERATE_PERF_STATISTICS = p(TesterraProperties.PERF_GENERATE_STATISTICS, false);
    public static boolean PERF_STOP_WATCH_ACTIVE = false;
    public static boolean REUSE_DATAPROVIDER_DRIVER_BY_THREAD = p(TesterraProperties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD, false);
    public static boolean LIST_TESTS = p(TesterraProperties.LIST_TESTS, false);
    public static boolean WEB_TAKE_ACTION_SCREENSHOTS = p(TesterraProperties.WEB_TAKE_ACTION_SCREENSHOTS, false);
    public static boolean FAILURE_CORRIDOR_ACTIVE = p(TesterraProperties.FAILURE_CORRIDOR_ACTIVE, false);
    public static boolean EXECUTION_OMIT_IN_DEVELOPMENT = p(TesterraProperties.EXECUTION_OMIT_IN_DEVELOPMENT, false);

    public static boolean SCREENSHOTTER_ACTIVE = p(TesterraProperties.SCREENSHOTTER_ACTIVE, true);
    public static boolean SCREENCASTER_ACTIVE = p(TesterraProperties.SCREENCASTER_ACTIVE, true);

    public static final boolean SCREENSHOT_ON_PAGELOAD = p(TesterraProperties.SCREENSHOT_ON_PAGELOAD, false);

}
