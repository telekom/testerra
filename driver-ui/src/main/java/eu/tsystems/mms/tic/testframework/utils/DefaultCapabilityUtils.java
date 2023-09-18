/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This is a simple helper to modify log messages of {@link Capabilities} to short long values or do other opertations
 * <p>
 * Date: 13.01.2021
 * Time: 07:14
 *
 * @author Eric Kubenka
 */
public class DefaultCapabilityUtils implements Loggable {

    /**
     * Clean the given {@link Capabilities} from very long values and return a {@link Map}
     *
     * @param capabilities {@link Capabilities}
     * @return Map
     */
    public Map<String, Object> clean(Capabilities capabilities) {
        return clean(capabilities.asMap());
    }

    public Map<String, Object> clean(Map<String, Object> capabilityMap) {
        // 1. clone and make map modifiable.
        Map<String, Object> clonedMap = this.clone(capabilityMap);

        // 2. do all the operations
        shortMapValues(clonedMap);

        // 3. make the map unmodifiable again.
        return Collections.unmodifiableMap(clonedMap);
    }

    /**
     * For deep cloning it is needed convert it to JSON and back because Firefox options also contain some immutable map objects
     * Note: Complex objects are simplified to key-value pairs. This is acceptable because capabilities are always simplified to a kind of Map<String, Object>
     */
    public Map<String, Object> clone(Map<String, Object> capabilityMap) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(capabilityMap);
        return (Map<String, Object>) gson.fromJson(json, Map.class);
    }

    /**
     * Some caps like Extensions strings are very long, so therefore we will cut them off
     */
    private void shortMapValues(Map<String, Object> map2Short) {
        // Exception list of keys which should not shorten
        List<String> exceptionList = List.of(
                "path"  // Absolute path for Firefox extension files
        );

        try {
            for (Map.Entry<String, Object> entry : map2Short.entrySet()) {
                Object value = entry.getValue();
                String key = entry.getKey();
                if (value instanceof Map) {
                    Map<String, Object> subMap = (Map<String, Object>) value;
                    shortMapValues(subMap);
                } else {
                    String stringValue = String.valueOf(value);
                    if (stringValue.length() > 40 && !exceptionList.contains(key)) {
                        entry.setValue(stringValue.substring(0, 40) + "...");
                    }
                }
            }
        } catch (Exception e) {
            log().debug("Cannot clean map: ", e);
        }

    }

    /**
     * Sets a capability value if the existing value doesn't match the same type,
     * is an empty string or doesn't exist.
     *
     * @param capabilities
     * @param capabilityName
     * @param capability
     * @param <T>
     */
    public <T> void putIfAbsent(DesiredCapabilities capabilities, String capabilityName, T capability) {
        Object existingCapability = capabilities.getCapability(capabilityName);
        if (!capability.getClass().isInstance(existingCapability) || (existingCapability instanceof String && StringUtils.isBlank((String) existingCapability))) {
            capabilities.setCapability(capabilityName, capability);
        }
    }
}
