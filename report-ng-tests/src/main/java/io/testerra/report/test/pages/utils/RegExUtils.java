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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExUtils {

    public static Matcher createMatcherForRegExp(final RegExp standardRegExp, final String evaluationString) {

        Pattern pattern = Pattern.compile(standardRegExp.getPatternString());
        return pattern.matcher(evaluationString);
    }

    public static String getRegExpResultOfString(final RegExp standardRegExp, String line) {

        final Matcher matcher = createMatcherForRegExp(standardRegExp, line);

        if (matcher.find()) {
            return matcher.group();
        }

        return "";
    }


    public enum RegExp {
        DIGITS_ONLY("\\d+"),
        LINE_BREAK("\\d+.+");

        private String patternString;

        RegExp(final String patternString) {
            this.patternString = patternString;
        }

        public String getPatternString() {
            return this.patternString;
        }

        public void setPatternString(final String valueToSet) {
            this.patternString=valueToSet;
        }

    }
}
