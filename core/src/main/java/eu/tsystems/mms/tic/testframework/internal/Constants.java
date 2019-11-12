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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.internal;

/**
 * Created with IntelliJ IDEA.
 * User: pele
 * Date: 15.07.13
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public final class Constants {

    /** Hidden constructor. */
    private Constants() {
    }

    /** Screenshots path relative to report index file. */
    public static final String SCREENSHOTS_PATH = "screenshots/";

    /** Screen references path relative to report index file. */
    public static final String SCREENREFERENCES_PATH = "screenreferences";

    public static final String DYNATRACE_LOGGER_DECORATOR_CLASS = "eu.tsystems.mms.tic.testframework.bmp.dynatrace.DynatraceLoggingDecorator";
    public static final String DYNATRACE_LOGGER_CLASS = "eu.tsystems.mms.tic.testframework.bmp.dynatrace.DynaTraceLogger";

    public static int IE_SCREENSHOT_LIMIT = 1200;
}
