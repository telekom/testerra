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
import eu.tsystems.mms.tic.testframework.constants.fennecProperties;

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

    public static boolean REPORT_SCREENSHOTS_PREVIEW = PropertyManager.getBooleanProperty(fennecProperties.REPORT_SCREENSHOTS_PREVIEW, true);
    public static String fennec_DB_PATH_SUFFIX = "not_set";
    public static boolean GENERATE_PERF_STATISTICS = PropertyManager.getBooleanProperty(fennecProperties.fennec_PERF_GENERATE_STATISTICS, false);
    public static boolean PERF_STOP_WATCH_ACTIVE = false;
    public static boolean REUSE_DATAPROVIDER_DRIVER_BY_THREAD = PropertyManager.getBooleanProperty(fennecProperties.fennec_REUSE_DATAPROVIDER_DRIVER_BY_THREAD, false);
    public static boolean MONITOR_MEMORY = PropertyManager.getBooleanProperty(fennecProperties.fennec_MONITOR_MEMORY, true);
    public static boolean DRY_RUN = PropertyManager.getBooleanProperty(fennecProperties.fennec_DRY_RUN, false);
    public static boolean DYNATRACE_LOGGING = PropertyManager.getBooleanProperty(fennecProperties.DYNATRACE_LOGGING, false);

    public static boolean fennec_REUSE_REPORTDIR = PropertyManager.getBooleanProperty(fennecProperties.fennec_REUSE_REPORTDIR, true);

    public static boolean fennec_REPORT_DATAPROVIDER_INFOS = PropertyManager.getBooleanProperty(fennecProperties.fennec_REPORT_DATAPROVIDER_INFOS, true);

    public static boolean LIST_TESTS = PropertyManager.getBooleanProperty(fennecProperties.fennec_LIST_TESTS, false);

    public static boolean fennec_WEB_TAKE_ACTION_SCREENSHOTS = PropertyManager.getBooleanProperty(fennecProperties.fennec_WEB_TAKE_ACTION_SCREENSHOTS, false);

    public static boolean FAILED_TESTS_RETRY_FLAPPING = PropertyManager.getBooleanProperty(fennecProperties.FAILED_TESTS_RETRY_FLAPPING, true);

    public static boolean fennec_FAILURE_CORRIDOR_ACTIVE = PropertyManager.getBooleanProperty(fennecProperties.fennec_FAILURE_CORRIDOR_ACTIVE, false);

    public static boolean fennec_KAFKA_ACTIVE = PropertyManager.getBooleanProperty(fennecProperties.fennec_KAFKA_ACTIVE, false);

    public static boolean fennec_EXECUTION_OMIT_IN_DEVELOPMENT = PropertyManager.getBooleanProperty(fennecProperties.fennec_EXECUTION_OMIT_IN_DEVELOPMENT, false);

    public static boolean fennec_GUIELEMENT_HIGHLIGHTS = PropertyManager.getBooleanProperty(fennecProperties.fennec_GUIELEMENT_HIGHLIGHTS_ACTIVE, true);
}
