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
package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Defaults {

    private static String load(String property, String defaultValue, String pattern) {
        Pattern compile = Pattern.compile(pattern);
        String value = PropertyManager.getProperty(property, defaultValue);
        Matcher matcher = compile.matcher(value);
        if (!matcher.find()) {
            throw new FennecRuntimeException(property + " does not match pattern " + pattern + " : " + value);
        }
        return value;
    }

    public static String DISPLAY_RESOLUTION = load(FennecProperties.DISPLAY_RESOLUTION, "1920x1200", "\\d+x\\d+");

}
