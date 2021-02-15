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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

@Deprecated
public final class DesktopWebDriverCapabilities extends WebDriverCapabilities {

    private static final Map<Pattern, Capabilities> ENDPOINT_CAPABILITIES = new LinkedHashMap<>();

    private DesktopWebDriverCapabilities() {

    }

    static void addContextCapabilities(DesiredCapabilities baseCapabilities, DesktopWebDriverRequest desktopWebDriverRequest) {
        /*
        priority:

        baseCapabilities
        overwritten by GLOBAL CAPS
        overwritten by THREAD CAPS
        overwritten by session caps from map
        overwritten by session caps desired

        ** overwritten if not empty
         */
        /*
        add global caps
         */
        getGlobalExtraCapabilities().forEach((s, o) -> safelyAddCapsValue(baseCapabilities, s, o));

       /*
        add thread local caps
         */
        Map<String, Object> threadLocalCaps = getThreadCapabilities();
        if (threadLocalCaps != null) {
            for (String key : threadLocalCaps.keySet()) {
                Object value = threadLocalCaps.get(key);
                safelyAddCapsValue(baseCapabilities, key, value);
            }
        }

        Map<String, ?> stringMap = desktopWebDriverRequest.getDesiredCapabilities().asMap();
        for (String key : stringMap.keySet()) {
            Object value = stringMap.get(key);
            safelyAddCapsValue(baseCapabilities, key, value);
        }
    }

    static DesiredCapabilities createCapabilities(final WebDriverManagerConfig config, DesiredCapabilities preSetCaps, DesktopWebDriverRequest desktopWebDriverRequest) {
        String browser = desktopWebDriverRequest.getBrowser();
        if (browser == null) {
            throw new RuntimeException("Browser is not set correctly");
        }
        final DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(preSetCaps);

        // set browser version into capabilities
        if (!StringUtils.isStringEmpty(desktopWebDriverRequest.getBrowserVersion())) {
            WebDriverManagerUtils.addBrowserVersionToCapabilities(desiredCapabilities, desktopWebDriverRequest.getBrowserVersion());
        }

        /*
        add endpoint bases caps
         */
        addEndPointCapabilities(desktopWebDriverRequest);

        /*
        add own desired capabilities
         */
        addContextCapabilities(desiredCapabilities, desktopWebDriverRequest);

        return desiredCapabilities;
    }

    @Deprecated
    private static void addEndPointCapabilities(DesktopWebDriverRequest desktopWebDriverRequest) {
        for (Pattern pattern : ENDPOINT_CAPABILITIES.keySet()) {
            if (pattern.matcher(desktopWebDriverRequest.getSeleniumServerUrl().getHost()).find()) {
                Capabilities capabilities = ENDPOINT_CAPABILITIES.get(pattern);
                desktopWebDriverRequest.getDesiredCapabilities().merge(capabilities);
                LOGGER.info("Applying EndPoint Capabilities: " + capabilities);
            }
        }
    }

    @Deprecated
    public static void registerEndPointCapabilities(Pattern endPointSelector, Capabilities capabilities) {
        ENDPOINT_CAPABILITIES.put(endPointSelector, capabilities);
    }
}
