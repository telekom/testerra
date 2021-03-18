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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public abstract class AbstractWebDriverFactory<R extends WebDriverRequest> implements Loggable, WebDriverFactory {

    protected abstract R buildRequest(WebDriverRequest webDriverRequest);

    protected abstract DesiredCapabilities buildCapabilities(DesiredCapabilities preSetCaps, R request);

    protected abstract WebDriver getRawWebDriver(R webDriverRequest, DesiredCapabilities desiredCapabilities, SessionContext sessionContext);

    /**
     * @deprecated Use {@link #setupNewWebDriverSession(EventFiringWebDriver, SessionContext)} instead
     */
    protected abstract void setupSession(EventFiringWebDriver eventFiringWebDriver, WebDriverRequest request);

    public WebDriver createWebDriver(WebDriverRequest request, SessionContext sessionContext) {
        /*
        build the final request (filled with all requested values)
         */
        R finalRequest = buildRequest(request);

        /*
        create capabilities
         */
        DesiredCapabilities caps = new DesiredCapabilities();
        DesiredCapabilities preparedCaps = buildCapabilities(caps, finalRequest);

        /**
         * @todo Move these options to the platform-connector
         */
        DesiredCapabilities tapOptions = new DesiredCapabilities();
        ExecutionContextController.getCurrentExecutionContext().getMetaData().forEach(tapOptions::setCapability);
        tapOptions.setCapability("scid", sessionContext.getId());
        tapOptions.setCapability("sessionKey", finalRequest.getSessionKey());
        preparedCaps.setCapability("tapOptions", tapOptions);

        /*
        create the web driver session
         */
        return getRawWebDriver(finalRequest, preparedCaps, sessionContext);
    }

    @Override
    public void setupNewWebDriverSession(EventFiringWebDriver webDriver, SessionContext sessionContext) {
        setupSession(webDriver, sessionContext.getWebDriverRequest());
    }
}
