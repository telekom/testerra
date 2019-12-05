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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.ErrorMessages;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by pele on 08.01.2015.
 */
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
            throw new TesterraRuntimeException(
                    "Browser is not set correctly");
        }
        final DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(preSetCaps);

        /*
         * This is the standard way of setting the browser locale for Selenoid based sessions
         * @see https://aerokube.com/selenoid/latest/#_per_session_environment_variables_env
         */
//        final Locale browserLocale = Locale.getDefault();
//        desiredCapabilities.setCapability("env",
//            String.format(
//                "[\"LANG=%s.UTF-8\", \"LANGUAGE=%s\", \"LC_ALL=%s.UTF-8\"]",
//                browserLocale,
//                browserLocale.getLanguage(),
//                browserLocale
//            )
//        );

        switch (browser) {
            case Browsers.htmlunit:
                LOGGER.info("Creating capabilities for HtmlUnitDriver");
                // need to enable Javascript
                desiredCapabilities.setBrowserName(BrowserType.HTMLUNIT);
                desiredCapabilities.setJavascriptEnabled(false);
                break;
            case Browsers.phantomjs:
                LOGGER.info("Creating capabilities for PhantomJS");
                // need to enable Javascript
                desiredCapabilities.setBrowserName(BrowserType.PHANTOMJS);
                desiredCapabilities.setJavascriptEnabled(true);
                break;
            case Browsers.firefox:
                //firefoxOptions.addPreference("intl.accept_languages", String.format("%s-%s", browserLocale.getLanguage(), browserLocale.getCountry()));
                break;
            case Browsers.edge:
                LOGGER.info("Creating capabilities for Edge");
                EdgeOptions edgeOptions = WebDriverManager.getBrowserOptions(EdgeOptions.class);
                final String platform = null;
                edgeOptions.setCapability("platform", platform);
                break;
            case Browsers.chrome:
            case Browsers.chromeHeadless:
                LOGGER.info("Creating capabilities for ChromeDriver");
                ChromeOptions chromeOptions = WebDriverManager.getBrowserOptions(ChromeOptions.class);
                chromeOptions.addArguments("--no-sandbox");
                //Map<String, Object> prefs = new HashMap<>();
                //prefs.put("intl.accept_languages", String.format("%s_%s", browserLocale.getLanguage(), browserLocale.getCountry()));
                //chromeOptions.setExperimentalOption("prefs", prefs);

                if (browser.equals(Browsers.chromeHeadless)) {
                    chromeOptions.addArguments("--headless", "--disable-gpu");
                }
                break;
            case Browsers.ie:
                LOGGER.info("Creating capabilities for InternetExplorerDriver");
                InternetExplorerOptions options = WebDriverManager.getBrowserOptions(InternetExplorerOptions.class);
                options.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                break;
            default:
                throw new TesterraSystemException(ErrorMessages.browserNotSupportedHere(browser));
        }

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
