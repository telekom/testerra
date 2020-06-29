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

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.enums.Position;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.ProxyUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class holding configuration settings for the WebDriverManager. Some are writable. This class is not ThreadSafe, some
 * settings may not be valid.
 */
public class WebDriverManagerConfig implements Loggable {

    /*
     * Default values.
     */

    /**
     * Specifies if windows should be closed.
     */
    public boolean executeCloseWindows = true;

    private URL proxyUrl;
    /**
     * WebDriverMode that is used.
     */
    public WebDriverMode webDriverMode = WebDriverMode.valueOf(PropertyManager.getProperty(TesterraProperties.WEBDRIVERMODE, WebDriverMode.remote.name()));

    /**
     * Close windows after Test Methods.
     */
    public boolean closeWindowsAfterTestMethod = PropertyManager.getBooleanProperty(
            TesterraProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS,
            true);
    /**
     * Close windows on failure.
     */
    public boolean closeWindowsOnFailure = PropertyManager.getBooleanProperty(
            TesterraProperties.CLOSE_WINDOWS_ON_FAILURE,
            true);

    public boolean maximize = PropertyManager.getBooleanProperty(TesterraProperties.BROWSER_MAXIMIZE, false);
    public Position maximizePosition = Position.valueOf(PropertyManager.getProperty(TesterraProperties.BROWSER_MAXIMIZE_POSITION, Position.CENTER.toString()).toUpperCase());

    /**
     * Default constructor.
     */
    public WebDriverManagerConfig() {
    }

    /**
     * @deprecated Use {@link #browser()} instead
     */
    @Deprecated
    public String getDefaultBrowser() {
        return browser();
    }

    public WebDriverMode getWebDriverMode() {
        return webDriverMode;
    }

    public String browser() {
        String browser = PropertyManager.getProperty(TesterraProperties.BROWSER, null);

        String browserSetting = PropertyManager.getProperty(TesterraProperties.BROWSER_SETTING);
        if (!StringUtils.isStringEmpty(browserSetting) && browserSetting.contains(":")) {
            String[] split = browserSetting.split(":");
            browser = split[0];
        }

        return browser;
    }

    public String browserVersion() {
        String browserVersion = PropertyManager.getProperty(TesterraProperties.BROWSER_VERSION, null);
        String browserSetting = PropertyManager.getProperty(TesterraProperties.BROWSER_SETTING);
        if (!StringUtils.isStringEmpty(browserSetting) && browserSetting.contains(":")) {
            String[] split = browserSetting.split(":");
            browserVersion = split[1];
        }
        return browserVersion;
    }

    public boolean areSessionsClosedAfterTestMethod() {
        return executeCloseWindows && closeWindowsAfterTestMethod;
    }

    /**
     * Returns the configured proxy url for the web driver.
     * @return The browser proxy URL
     */
    public URL proxyUrl() {
        if (proxyUrl==null) {
            final String propertyName = "tt.browser.proxy";
            String browserProxy = PropertyManager.getProperty(propertyName);
            if (browserProxy != null && browserProxy.length() > 0) {
                try {
                    this.proxyUrl = new URL(browserProxy);
                } catch (MalformedURLException e) {
                    log().error(String.format("Unable to parse propererty %s", propertyName), e);
                    if (PropertyManager.getBooleanProperty("tt.browser.proxy.useSystemDefault", false)) {
                        this.proxyUrl = ProxyUtils.getSystemHttpProxyUrl();
                    }
                }
            }
        }
        return this.proxyUrl;
    }

    public WebDriverManagerConfig setProxyUrl(URL proxyUrl) {
        this.proxyUrl = proxyUrl;
        return this;
    }
}
