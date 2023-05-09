/*
 * Testerra
 *
 * (C) 2022, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pages.utils;

import java.text.SimpleDateFormat;

public class DateTimeUtils {

    public static final SimpleDateFormat formatMilliSeconds = new SimpleDateFormat("SSS'ms'");
    public static final SimpleDateFormat formatSeconds = new SimpleDateFormat("s's' SSS'ms'");
    public static final SimpleDateFormat formatMinutes = new SimpleDateFormat("mm'mins' s's' SSS'ms'");
    public static final SimpleDateFormat formatHours = new SimpleDateFormat("H'h' mm'mins' s's' SSS'ms'");


    public static boolean verifyDateTimeString(final String dateTimeString) {
       return verifyFormat(dateTimeString);
    }

    private static boolean verifyFormat(final String dateTimeString) {
        if (isParsable(dateTimeString, DateTimeUtils.formatMilliSeconds)) {
            return true;
        }

        if (isParsable(dateTimeString, DateTimeUtils.formatSeconds)) {
            return true;
        }

        if (isParsable(dateTimeString, DateTimeUtils.formatMinutes)) {
            return true;
        }

        return isParsable(dateTimeString, DateTimeUtils.formatHours);
    }

    public static boolean isParsable(final String dateTimeString, final SimpleDateFormat format) {
        try {
            format.parse(dateTimeString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
