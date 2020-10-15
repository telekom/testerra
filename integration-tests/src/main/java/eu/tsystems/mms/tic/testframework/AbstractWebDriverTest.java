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
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverRetainer;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.lang.reflect.Method;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

/**
 * Abstract test class for tests using a WebDriver
 */
public abstract class AbstractWebDriverTest extends TesterraTest implements WebDriverRetainer, WebDriverManagerProvider, Loggable {


    //    static {
    //        WebDriverManager.config().closeWindowsAfterTestMethod = false;
    //    }
    //
    //    @AfterTest(alwaysRun = true)
    //    public void resetWDCloseWindowsMode() {
    //        WebDriverManager.config().closeWindowsAfterTestMethod = true;
    //    }

    private String exclusiveSessionId;

    @AfterSuite(alwaysRun = true)
    private void closeBrowsers() {
        WebDriverManager.forceShutdownAllThreads();
    }

    /**
     * Fixing up testing issues when /dev/shm becomes to small for test execution
     * this will fix "session deleted because of page crash"
     * https://stackoverflow.com/questions/53902507/unknown-error-session-deleted-because-of-page-crash-from-unknown-error-cannot
     */
    @BeforeMethod(alwaysRun = true)
    public void configureChromeOptions(Method method) {
        WebDriverManager.setUserAgentConfig(Browsers.chromeHeadless, new ChromeConfig() {
            @Override
            public void configure(ChromeOptions options) {
                options.addArguments("--disable-dev-shm-usage");
            }
        });
    }

    /**
     * Sets the unsupportedBrowser=true flag for the @Fails annotation
     */
    protected static void setUnsupportedBrowserFlag() {
        if (Browsers.phantomjs.equals(WebDriverManager.getConfig().getBrowser())) {
            System.setProperty("unsupportedBrowser", "true");
        }
    }

    public WebDriver getClassExclusiveWebDriver() {
        if (exclusiveSessionId == null) {
            exclusiveSessionId = webDriverManager.createExclusiveSessionId(getWebDriver());
        }
        return webDriverManager.getWebDriverBySessionId(exclusiveSessionId);
    }

    @AfterClass
    public void closeClassExclusiveWebDriverSession() {
        if (this.exclusiveSessionId != null) {
            webDriverManager.shutdownExclusiveSessionId(this.exclusiveSessionId);
            this.exclusiveSessionId = null;
        }
    }

    @Override
    public WebDriver getWebDriver() {
        WebDriver webDriver = webDriverManager.getWebDriver();
        try {
            webDriver.getWindowHandles();
        } catch (WebDriverException s) {
            log().error(s.getMessage());
            webDriverManager.shutdownAllSessions(true); // shutdown all threwad drivers.
        }

        return webDriver;
    }

    static {
        setUnsupportedBrowserFlag();
    }
}
