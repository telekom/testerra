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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.useragents.BrowserInformation;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing utility methods for WebDriverManager. To keep the WebDriverManager Class cleaner.
 *
 * @author sepr
 */
public final class WebDriverManagerUtils {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverManagerUtils.class);

    /**
     * Hide constructor.
     */
    private WebDriverManagerUtils() {
    }

    public static BrowserInformation getBrowserInformation(final WebDriver driver) {
        if (driver == null) {
            return null;
        }

        BrowserInformation browserInformation;
        WebDriver realDriver = driver;

        if (ProvidesBrowserInformation.class.isAssignableFrom(realDriver.getClass())) {
            browserInformation = ((ProvidesBrowserInformation) realDriver).getBrowserInformation();
        } else if (JavascriptExecutor.class.isAssignableFrom(realDriver.getClass())) {
            String userAgentString = "unknown";
            try {
                userAgentString = (String) ((JavascriptExecutor) realDriver).executeScript("return navigator.userAgent;");
            } catch (Exception e) {
                LOGGER.error("Error requesting user agent", e);
            }
            browserInformation = Testerra.getInjector().getInstance(BrowserInformation.class);
            browserInformation.parseUserAgent(userAgentString);
        } else {
            browserInformation = Testerra.getInjector().getInstance(BrowserInformation.class);
        }

        return browserInformation;
    }

    /**
     * Quit WebDriver Session.
     *
     * @param driver .
     */
    protected static void quitWebDriverSession(final WebDriver driver) {
        try {
            driver.quit();
        } catch (final Throwable e) {
            LOGGER.warn("WebDriver could not be quit. May someone did before.", e);
        }
    }

    /**
     * @deprecated Use {@link WebDriverSessionsManager#getSessionContext(WebDriver)} instead
     */
    public static String getSessionKey(WebDriver driver) {

        return WebDriverSessionsManager.getSessionKey(driver);
    }

}


