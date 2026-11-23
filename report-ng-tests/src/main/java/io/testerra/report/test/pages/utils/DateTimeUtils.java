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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class DateTimeUtils {

    public static final DateTimeFormatter formatDateTime =
            DateTimeFormatter.ofPattern("EEE, dd MMM uuuu, HH:mm:ss", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter formatSecondsAndMillis =
            DateTimeFormatter.ofPattern("s's' SSS'ms'").withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter formatMinutes =
            DateTimeFormatter.ofPattern("mm'm' s's' SSS'ms'").withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter formatHours =
            DateTimeFormatter.ofPattern("H'h' mm'm' s's' SSS'ms'").withResolverStyle(ResolverStyle.STRICT);

    public static boolean verifyDateTimeString(final String dateTimeString) {
        return verifyFormat(dateTimeString);
    }

    private static boolean verifyFormat(final String dateTimeString) {
        try {
            LocalDateTime.parse(dateTimeString, formatDateTime);
            return true;
        } catch (DateTimeParseException e) {
            // Try the duration formats below.
        }
        if (isParsable(dateTimeString, formatSecondsAndMillis)) {
            return true;
        }
        if (isParsable(dateTimeString, formatMinutes)) {
            return true;
        }
        return isParsable(dateTimeString, formatHours);
    }

    private static boolean isParsable(final String dateTimeString, final DateTimeFormatter format) {
        try {
            format.parse(dateTimeString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
