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

import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.utils.ObjectUtils;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rnhb on 12.02.2016.
 */
public abstract class WebDriverFactory<R extends WebDriverRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFactory.class);

    protected abstract R buildRequest(WebDriverRequest webDriverRequest);

    protected abstract WebDriver getRawWebDriver(R webDriverRequest);

    protected abstract void setupSession(EventFiringWebDriver eventFiringWebDriver, R request);

    public EventFiringWebDriver getWebDriver(WebDriverRequest r, SessionContext sessionContext) {
        /*
        set base parameters
         */
        if (r.baseUrl == null) {
            r.baseUrl = WebDriverManager.getBaseURL();
        }

        // not parsing "browser" here, it is already set for picking the correct factory ;)

        if (r.browserVersion == null) {
            r.browserVersion = WebDriverManager.config().browserVersion;
        }

        // link session context
        r.sessionContext = sessionContext;

        /*
        build the final request (filled with all requested values)
         */
        R finalRequest = buildRequest(r);

        /*
        fill the session context
         */
        sessionContext.metaData.put("browser", r.browser);
        sessionContext.metaData.put("browserVersion", r.browserVersion);

        /*
        log the session context
         */
        LOGGER.info("[SCID:" + sessionContext.id + "] Requesting new web driver session: browser=" + finalRequest.browser);

        /*
        create the web driver session
         */
        WebDriver rawDriver = getRawWebDriver(finalRequest);

        /*
        wrap the driver with the proxy
         */
        /*
         * Watch out when wrapping the driver here. Any more wraps than EventFiringWebDriver will break at least
         * the MobileDriverAdapter. This is because we need to compare the lowermost implementation of WebDriver in this case.
         * It can be made more robust, if we always can retrieve the storedSessionId of the WebDriver, given a WebDriver object.
         * For more info, please ask @rnhb
         */
        try {
            rawDriver = ObjectUtils.simpleProxy(WebDriver.class, rawDriver, WebDriverProxy.class, RemoteWebDriver.class.getInterfaces());
        } catch (Exception e) {
            LOGGER.error("Could not create proxy for raw webdriver", e);
        }
        EventFiringWebDriver eventFiringWebDriver = wrapRawWebDriverWithEventFiringWebDriver(rawDriver);

        /*
        store session
         */
        WebDriverSessionsManager.storeWebDriverSession(r.sessionKey, eventFiringWebDriver, sessionContext);

        /*
        finalize the session setup
         */
        setupSession(eventFiringWebDriver, finalRequest);

        return eventFiringWebDriver;
    }

    /**
     * Get EventFiringWebDriver from default WebDriver instance.
     *
     * @param driver The default WebDriver instance.
     * @return An EventFiringWebDriver instance.
     */
    static EventFiringWebDriver wrapRawWebDriverWithEventFiringWebDriver(final WebDriver driver) {
        return new EventFiringWebDriver(driver);
    }

}
