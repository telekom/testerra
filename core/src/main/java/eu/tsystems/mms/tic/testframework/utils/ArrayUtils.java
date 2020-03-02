/*
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
package eu.tsystems.mms.tic.testframework.utils;

@Deprecated
public class ArrayUtils extends  org.apache.commons.lang3.ArrayUtils {

    /**
     * @deprecated Use {@link String#join} instead
     */
    @Deprecated
    public static String join(final Object[] objects, String seperator) {
        if (objects == null || objects.length == 0) {
            throw new IllegalArgumentException("Parameter #1 Object[] cannot be null or empty");
        }
        if (seperator == null) {
            seperator = "";
        }

        String out = "";
        for (Object object: objects) {
            out += object + seperator;
        }

        out = out.substring(0, out.length() - seperator.length());
        return out;
    }
}
