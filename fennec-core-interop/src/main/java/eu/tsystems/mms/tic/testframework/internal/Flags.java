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
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public final class Flags {

    private Flags() {
    }

    @Deprecated
    public static boolean METRICS_ACTIVE = false;
    @Deprecated
    public static boolean TRACKING_ACTIVE = false;

    public static boolean REPORT_SCREENSHOTS_PREVIEW = PropertyManager.getBooleanProperty(FennecProperties.REPORT_SCREENSHOTS_PREVIEW, true);
    public static String DB_PATH_SUFFIX = "not_set";
    public static boolean GENERATE_PERF_STATISTICS = PropertyManager.getBooleanProperty(FennecProperties.PERF_GENERATE_STATISTICS, false);
    public static boolean PERF_STOP_WATCH_ACTIVE = false;
    public static boolean REUSE_DATAPROVIDER_DRIVER_BY_THREAD = PropertyManager.getBooleanProperty(FennecProperties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD, false);
    public static boolean MONITOR_MEMORY = PropertyManager.getBooleanProperty(FennecProperties.MONITOR_MEMORY, true);
    public static boolean DRY_RUN = PropertyManager.getBooleanProperty(FennecProperties.DRY_RUN, false);
    public static boolean DYNATRACE_LOGGING = PropertyManager.getBooleanProperty(FennecProperties.DYNATRACE_LOGGING, false);

    public static boolean REUSE_REPORTDIR = PropertyManager.getBooleanProperty(FennecProperties.REUSE_REPORTDIR, true);

    public static boolean REPORT_DATAPROVIDER_INFOS = PropertyManager.getBooleanProperty(FennecProperties.REPORT_DATAPROVIDER_INFOS, true);

    public static boolean LIST_TESTS = PropertyManager.getBooleanProperty(FennecProperties.LIST_TESTS, false);

    public static boolean WEB_TAKE_ACTION_SCREENSHOTS = PropertyManager.getBooleanProperty(FennecProperties.WEB_TAKE_ACTION_SCREENSHOTS, false);

    public static boolean FAILED_TESTS_RETRY_FLAPPING = PropertyManager.getBooleanProperty(FennecProperties.FAILED_TESTS_RETRY_FLAPPING, true);

    public static boolean FAILURE_CORRIDOR_ACTIVE = PropertyManager.getBooleanProperty(FennecProperties.FAILURE_CORRIDOR_ACTIVE, false);

    public static boolean KAFKA_ACTIVE = PropertyManager.getBooleanProperty(FennecProperties.KAFKA_ACTIVE, false);

    public static boolean EXECUTION_OMIT_IN_DEVELOPMENT = PropertyManager.getBooleanProperty(FennecProperties.EXECUTION_OMIT_IN_DEVELOPMENT, false);

    public static boolean GUIELEMENT_HIGHLIGHTS = PropertyManager.getBooleanProperty(FennecProperties.GUIELEMENT_HIGHLIGHTS_ACTIVE, true);
}
