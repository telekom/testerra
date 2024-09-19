/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testing.UiElementFinderFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverRetainer;
import eu.tsystems.mms.tic.testframework.webdrivermanager.AbstractWebDriverRequest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Abstract test class for tests using a WebDriver
 */
public abstract class AbstractWebDriverTest extends TesterraTest implements
        WebDriverRetainer,
        WebDriverManagerProvider,
        Loggable,
        UiElementFinderFactoryProvider,
        AssertProvider {

    private String exclusiveSessionId;

    @AfterSuite(alwaysRun = true)
    private void closeBrowsers() {
        WEB_DRIVER_MANAGER.shutdownAllSessions();
    }

    /**
     * Fixing up testing issues when /dev/shm becomes to small for test execution
     * this will fix "session deleted because of page crash"
     * https://stackoverflow.com/questions/53902507/unknown-error-session-deleted-because-of-page-crash-from-unknown-error-cannot
     */
    @BeforeMethod(alwaysRun = true)
    public void configureChromeOptions(Method method) {
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chromeHeadless, (ChromeConfig) options -> {
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-search-engine-choice-screen");
        });
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chrome, (ChromeConfig) options -> {
            options.addArguments("--disable-search-engine-choice-screen");
        });
    }

    protected AbstractWebDriverRequest getWebDriverRequest() {
        return null;
    }

    private WebDriver _getWebDriver() {
        WebDriver webDriver;
        AbstractWebDriverRequest request = getWebDriverRequest();
        if (request != null) {
            webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        } else {
            webDriver = WEB_DRIVER_MANAGER.getWebDriver();
        }
        return webDriver;
    }

    public WebDriver getClassExclusiveWebDriver() {
        if (exclusiveSessionId == null) {
            exclusiveSessionId = WEB_DRIVER_MANAGER.makeExclusive(getWebDriver());
        }
        return WEB_DRIVER_MANAGER.getWebDriver(exclusiveSessionId);
    }

    @AfterClass
    public void closeClassExclusiveWebDriverSession() {
        if (this.exclusiveSessionId != null) {
            WEB_DRIVER_MANAGER.shutdownSession(this.exclusiveSessionId);
            this.exclusiveSessionId = null;
        }
    }

    @Override
    public WebDriver getWebDriver() {
        try {
            this._getWebDriver().getWindowHandles();
        } catch (WebDriverException s) {
            log().error(s.getMessage());
            WEB_DRIVER_MANAGER.shutdownAllThreadSessions(); // shutdown all threwad drivers.
        }

        return this._getWebDriver();
    }

}
