/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class WebDriverCapabilities {

    /**
     * Extra capabilities.
     */
    protected static final Map<String, Object> GLOBALCAPABILITIES = new HashMap<>();
    protected static final ThreadLocal<Map<String, Object>> THREAD_CAPABILITIES = new ThreadLocal<>();

    protected static final Logger LOGGER = LoggerFactory.getLogger(WebDriverCapabilities.class);

    protected static void safelyAddCapsValue(DesiredCapabilities caps, String key, Object value) {
        if (value == null) {
            return;
        }
        if (StringUtils.isStringEmpty("" + value)) {
            return;
        }
        caps.setCapability(key, value);
    }

    /**
     * Adds a capability.
     *
     * @param key   The key of the capability to set.
     * @param value The value of the capability to set.
     */
    static void addGlobalCapability(String key, Object value) {
        if (CapabilityType.BROWSER_NAME.equals(key)) {
            LOGGER.warn("Skipping extra desired capability " + key);
            return;
        }
        GLOBALCAPABILITIES.put(key, value);
    }

    @Deprecated
    static void addThreadCapability(String key, Object value) {
        if (CapabilityType.BROWSER_NAME.equals(key)) {
            LOGGER.warn("Skipping extra desired capability " + key);
            return;
        }

        getThreadCapabilities().put(key, value);
    }

    @Deprecated
    static Map<String, Object> getThreadCapabilities() {
        if (THREAD_CAPABILITIES.get() == null) {
            THREAD_CAPABILITIES.set(new HashMap<>());
        }
        return THREAD_CAPABILITIES.get();
    }

    /**
     * Remove extra capability from capabilities.
     *
     * @param key The key of the capability to remove.
     */
    static void removeGlobalExtraCapability(final String key) {
        GLOBALCAPABILITIES.remove(key);
    }

    /**
     * Clear capabilities.
     */
    static void clearGlobalCapabilities() {
        GLOBALCAPABILITIES.clear();
    }

    @Deprecated
    static void clearThreadCapabilities() {
        THREAD_CAPABILITIES.remove();
    }

    /**
     * Adds extra capabilities to desired capabilities.
     *
     * @param desiredCapabilities .
     */
    static void setGlobalExtraCapabilities(final DesiredCapabilities desiredCapabilities) {
        Map<String, ?> map = desiredCapabilities.asMap();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            addGlobalCapability(key, value);
        }
    }

    static Map<String, Object> getGlobalExtraCapabilities() {
        return GLOBALCAPABILITIES;
    }

}
