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

    private static void safelyAddCapsValue(DesiredCapabilities caps, String key, Object value) {
        if (value == null) {
            return;
        }
        if (StringUtils.isStringEmpty("" + value)) {
            return;
        }
        caps.setCapability(key, value);
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

        if (baseCapabilities == null) {
            baseCapabilities = new DesiredCapabilities();
        }

        /*
        add global caps
         */
        for (String key : GLOBALCAPABILITIES.keySet()) {
            Object value = GLOBALCAPABILITIES.get(key);
            safelyAddCapsValue(baseCapabilities, key, value);
        }

        /*
        add thread local caps
         */
        Map<String, Object> threadLocalCaps = THREAD_CAPABILITIES.get();
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
                desiredCapabilities.setBrowserName(BrowserType.FIREFOX);

                FirefoxOptions firefoxOptions = new FirefoxOptions();
                desiredCapabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
                break;
            case Browsers.safari:
                LOGGER.info("Creating capabilities for SafariDriver");
                desiredCapabilities.setBrowserName(BrowserType.SAFARI);
                break;
            case Browsers.edge:
                LOGGER.info("Creating capabilities for Edge");
                desiredCapabilities.setBrowserName(BrowserType.EDGE);

                EdgeOptions edgeOptions = new EdgeOptions();
                final String platform = null;
                edgeOptions.setCapability("platform", platform);

                desiredCapabilities.setCapability("edgeOptions", edgeOptions);
//                desiredCapabilities.setCapability(EdgeOptions.CAPABILITY, edgeOptions);
                break;
            case Browsers.chrome:
            case Browsers.chromeHeadless:
                LOGGER.info("Creating capabilities for ChromeDriver");
                desiredCapabilities.setBrowserName(BrowserType.CHROME);
                {
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--no-sandbox");
                    desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                }
                break;
            case Browsers.ie:
                LOGGER.info("Creating capabilities for InternetExplorerDriver");
                desiredCapabilities.setBrowserName(BrowserType.IEXPLORE);

                InternetExplorerOptions options = new InternetExplorerOptions();
                options.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

                desiredCapabilities.setCapability("se:ieOptions", options);
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

        /*
        add some hidden configs
         */
        if (Browsers.chromeHeadless.equalsIgnoreCase(browser)) {
            ChromeOptions chromeOptions = (ChromeOptions) desiredCapabilities.getCapability(ChromeOptions.CAPABILITY);
            if (chromeOptions == null) {
                chromeOptions = new ChromeOptions();
            }
            chromeOptions.addArguments("--headless", "--disable-gpu");
            desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        }

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

    private static final Map<Pattern, Capabilities> ENDPOINT_CAPABILITIES = new LinkedHashMap<>();

    public static void registerEndPointCapabilities(Pattern endPointSelector, Capabilities capabilities) {
        ENDPOINT_CAPABILITIES.put(endPointSelector, capabilities);
    }
}
