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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Deprecated
public final class DesktopWebDriverCapabilities extends WebDriverCapabilities {

    private static final Map<Pattern, Capabilities> ENDPOINT_CAPABILITIES = new LinkedHashMap<>();

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

        /*
        add session caps
         */
        if (desktopWebDriverRequest.sessionCapabilities != null) {
            for (String key : desktopWebDriverRequest.sessionCapabilities.keySet()) {
                Object value = desktopWebDriverRequest.sessionCapabilities.get(key);
                safelyAddCapsValue(baseCapabilities, key, value);
            }
        }

        if (desktopWebDriverRequest.desiredCapabilities != null) {
            Map<String, ?> stringMap = desktopWebDriverRequest.desiredCapabilities.asMap();
            for (String key : stringMap.keySet()) {
                Object value = stringMap.get(key);
                safelyAddCapsValue(baseCapabilities, key, value);
            }
        }
    }

    static DesiredCapabilities createCapabilities(final WebDriverManagerConfig config, DesiredCapabilities preSetCaps, DesktopWebDriverRequest desktopWebDriverRequest) {
        String browser = desktopWebDriverRequest.browser;
        if (browser == null) {
            throw new TesterraRuntimeException("Browser is not set correctly");
        }
        final DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(preSetCaps);

        // set browser version into capabilities
        if (!StringUtils.isStringEmpty(desktopWebDriverRequest.browserVersion)) {
            WebDriverManagerUtils.addBrowserVersionToCapabilities(desiredCapabilities, desktopWebDriverRequest.browserVersion);
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

    private static void addEndPointCapabilities(DesktopWebDriverRequest desktopWebDriverRequest) {
        for (Pattern pattern : ENDPOINT_CAPABILITIES.keySet()) {
            if (pattern.matcher(desktopWebDriverRequest.seleniumServerHost).find()) {
                Capabilities capabilities = ENDPOINT_CAPABILITIES.get(pattern);
                Map<String, ?> m = capabilities.asMap();
                desktopWebDriverRequest.sessionCapabilities.putAll(m);
                LOGGER.info("Applying EndPoint Capabilities: " + m);
            }
        }
    }

    public static void registerEndPointCapabilities(Pattern endPointSelector, Capabilities capabilities) {
        ENDPOINT_CAPABILITIES.put(endPointSelector, capabilities);
    }
}
