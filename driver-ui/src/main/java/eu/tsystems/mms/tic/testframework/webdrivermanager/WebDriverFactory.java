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

import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.ObjectUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rnhb on 12.02.2016.
 */
public abstract class WebDriverFactory<R extends WebDriverRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFactory.class);

    private SessionContext sessionContext = null;

    protected abstract R buildRequest(WebDriverRequest webDriverRequest);

    protected abstract DesiredCapabilities buildCapabilities(DesiredCapabilities preSetCaps, R request);

    protected abstract WebDriver getRawWebDriver(R webDriverRequest, DesiredCapabilities desiredCapabilities);

    protected abstract void setupSession(EventFiringWebDriver eventFiringWebDriver, R request);

    public EventFiringWebDriver getWebDriver(WebDriverRequest r, SessionContext sessionContext) {
        this.sessionContext = sessionContext;

        /*
        set base parameters
         */
        if (r.baseUrl == null) {
            r.baseUrl = WebDriverManager.getBaseURL();
        }


        /*
        build the final request (filled with all requested values)
         */
        R finalRequest = buildRequest(r);
        r = null; // invalidate

        // link session context
        finalRequest.sessionContext = sessionContext;

        /*
        fill the session context
         */
        sessionContext.metaData.put("requested.browser", finalRequest.browser);
        sessionContext.metaData.put("requested.browserVersion", finalRequest.browserVersion);

        /*
        create basic capabilities
         */
        final DesiredCapabilities caps = new DesiredCapabilities();
        DesiredCapabilities tapOptions = new DesiredCapabilities();
        ExecutionContextController.EXECUTION_CONTEXT.metaData.forEach(tapOptions::setCapability);
        tapOptions.setCapability("scid", finalRequest.sessionContext.id);
        sessionContext.metaData.forEach(tapOptions::setCapability);
        tapOptions.setCapability("sessionKey", sessionContext.sessionKey);
        caps.setCapability("tapOptions", tapOptions);

        /*
        create capabilities
         */
        DesiredCapabilities finalCaps = buildCapabilities(caps, finalRequest);

        /*
        log capabilities
         */
        logSessionRequest(finalRequest, finalCaps);

        /*
        create the web driver session
         */
        WebDriver rawDriver = getRawWebDriver(finalRequest, finalCaps);

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
            Class[] interfaces = ObjectUtils.getAllInterfacesOf(rawDriver);
            rawDriver = ObjectUtils.simpleProxy(WebDriver.class, rawDriver, WebDriverProxy.class, interfaces);
        } catch (Exception e) {
            LOGGER.error("Could not create proxy for raw webdriver", e);
        }
        EventFiringWebDriver eventFiringWebDriver = wrapRawWebDriverWithEventFiringWebDriver(rawDriver);

        /*
        store session
         */
        WebDriverSessionsManager.storeWebDriverSession(finalRequest, eventFiringWebDriver, sessionContext);

        /*
        finalize the session setup
         */
        setupSession(eventFiringWebDriver, finalRequest);

        return eventFiringWebDriver;
    }

    private void logSessionRequest(R finalRequest, DesiredCapabilities finalCaps) {
        StringBuffer msg = new StringBuffer();
        msg.append("Requesting new web driver session with capabilities:");
        finalCaps.asMap().forEach((k, v) -> msg.append(",").append(k).append("=").append(v));

        /*
        log proxy
         */
        Proxy proxy = (Proxy) finalCaps.getCapability(CapabilityType.PROXY);
        if (proxy != null) {
            msg.append("\n").append(proxy.toJson());
        }

        LOGGER.info(msg.toString());
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
