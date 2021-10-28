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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.apache.commons.lang3.StringUtils;

/**
 * This is a simple helper to modify log messages of {@link Capabilities} to short long values or do other opertations
 * <p>
 * Date: 13.01.2021
 * Time: 07:14
 *
 * @author Eric Kubenka
 */
public class DefaultCapabilityUtils {

    /**
     * Clean the given {@link Capabilities} and return a {@link Map}
     *
     * @param capabilities {@link Capabilities}
     * @return Map
     */
    public Map<String, Object> clean(Capabilities capabilities) {

        // 1. make map modifiable.
        Map<String, Object> capabilityMap = new TreeMap<>(capabilities.asMap());

        // 2. do all the operations
        capabilityMap = shortChromeExtensionStrings(capabilityMap);

        // 3. make the map unmodifiable again.
        return Collections.unmodifiableMap(capabilityMap);
    }

    /**
     * Extensions strings are very long, so therefore we will cut them off
     *
     * @param capabilityMap {@link Map}
     * @return Map
     */
    private Map<String, Object> shortChromeExtensionStrings(Map<String, Object> capabilityMap) {

        final Object chromeOptionsObject = capabilityMap.get(ChromeOptions.CAPABILITY);
        final Object extensionsObject = capabilityMap.get("extensions");

        if (chromeOptionsObject != null) {
            final Map chromeOptions = (Map) chromeOptionsObject;
            if (chromeOptions.containsKey("extensions")) {
                chromeOptions.put("extensions", shortAllStringsInLists(chromeOptions.get("extensions")));
            }
        }

        if (extensionsObject != null) {
            capabilityMap.put("extensions", shortAllStringsInLists(extensionsObject));
        }

        return capabilityMap;
    }

    private List<String> shortAllStringsInLists(final Object stringListObject) {
        final List<String> extList = new ArrayList<>();
        ((List<String>) stringListObject).forEach(e -> extList.add(e.substring(0, 10) + "..."));
        return extList;
    }

    /**
     * Sets a capability value if the existing value doesn't match the same type,
     * is an empty string or doesn't exist.
     * @param capabilities
     * @param capabilityName
     * @param capability
     * @param <T>
     */
    public <T> void putIfAbsent(DesiredCapabilities capabilities, String capabilityName, T capability) {
        Object existingCapability = capabilities.getCapability(capabilityName);
        if (!capability.getClass().isInstance(existingCapability) || (existingCapability instanceof String && StringUtils.isBlank((String)existingCapability))) {
            capabilities.setCapability(capabilityName, capability);
        }
    }
}
