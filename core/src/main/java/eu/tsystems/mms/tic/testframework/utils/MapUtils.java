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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class MapUtils {

    private MapUtils() {

    }

    public static Map<String, String> propertiesToMap(Properties properties) {
        Map<String, String> out = new HashMap<>();
        if (properties != null) {
            for (Object key : properties.keySet()) {
                out.put("" + key, properties.getProperty("" + key));
            }
        }
        return out;
    }

    public static Properties mapToProperties(Map<String, String> map) {
        Properties out = new Properties();
        if (map != null) {
            for (String key : map.keySet()) {
                out.setProperty(key, map.get(key));
            }
        }
        return out;
    }
}
