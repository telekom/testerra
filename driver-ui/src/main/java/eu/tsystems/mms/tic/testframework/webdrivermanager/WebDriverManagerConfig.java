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
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;

/**
 * Class holding configuration settings for the WebDriverManager. Some are writable. This class is not ThreadSafe, some
 * settings may not be valid.
 */
public class WebDriverManagerConfig {
    /**
     * Specifies if windows should be closed.
     */
    private boolean executeCloseWindows = true;

    /**
     * WebDriverMode that is used.
     */
    private WebDriverMode webDriverMode;

    /**
     * Close windows after Test Methods.
     */
    private boolean closeWindowsAfterTestMethod;

    /**
     * Close windows on failure.
     */
    private boolean closeWindowsOnFailure;

    private boolean maximize;

    private Position maximizePosition;

    private String baseUrl;

    private String browser;
    private String browserVersion;

    /**
     * Default constructor.
     */
    public WebDriverManagerConfig() {
        this.reset();
    }

    public WebDriverManagerConfig reset() {
        this.executeCloseWindows = true;
        this.webDriverMode = WebDriverMode.valueOf(PropertyManager.getProperty(TesterraProperties.WEBDRIVERMODE, WebDriverMode.remote.name()));
        this.closeWindowsAfterTestMethod = PropertyManager.getBooleanProperty(TesterraProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS, true);
        this.closeWindowsOnFailure = PropertyManager.getBooleanProperty(TesterraProperties.CLOSE_WINDOWS_ON_FAILURE, true);
        this.maximize = PropertyManager.getBooleanProperty(TesterraProperties.BROWSER_MAXIMIZE, false);
        this.maximizePosition = null;
        this.baseUrl = null;
        this.browser = null;
        this.browserVersion = null;
        return this;
    }

    public String getBaseUrl() {
        if (baseUrl == null) {
            baseUrl = PropertyManager.getProperty(TesterraProperties.BASEURL, "");
        }
        return baseUrl;
    }

    /**
     * @deprecated Set your base url via. {@link WebDriverRequest#setBaseUrl(String)} instead
     */
    @Deprecated
    public WebDriverManagerConfig setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * @deprecated Use {@link #getBrowser()} instead
     */
    @Deprecated
    public String getDefaultBrowser() {
        return getBrowser();
    }

    private void initBrowser() {
        String browserSetting = PropertyManager.getProperty(TesterraProperties.BROWSER_SETTING);
        if (!StringUtils.isStringEmpty(browserSetting)) {
            String[] split = browserSetting.split(":");
            if (split.length > 0) this.browser = split[0].trim();
            if (split.length > 1) this.browserVersion = split[1].trim();
        }

        if (StringUtils.isStringEmpty(this.browser)) {
            this.browser = PropertyManager.getProperty(TesterraProperties.BROWSER, "");
        }
        if (StringUtils.isStringEmpty(this.browserVersion)) {
            this.browserVersion = PropertyManager.getProperty(TesterraProperties.BROWSER_VERSION, "");
        }
    }

    public String getBrowser() {
        if (this.browser == null) {
            this.initBrowser();
        }
        return this.browser;
    }

    public String getBrowserVersion() {
        if (this.browserVersion == null) {
            this.initBrowser();
        }
        return this.browserVersion;
    }

    public WebDriverMode getWebDriverMode() {
        return webDriverMode;
    }

    /**
     * @deprecated Use {@link #getBrowser()} ()} instead
     */
    @Deprecated
    public String browser() {
        return this.getBrowser();
    }

    /**
     * @deprecated Use {@link #getBrowserVersion()} instead
     */
    @Deprecated
    public String browserVersion() {
       return this.getBrowserVersion();
    }

    public boolean shouldShutdownSessions() {
        return executeCloseWindows;
    }

    public WebDriverManagerConfig setShutdownSessions(boolean close) {
        this.executeCloseWindows = close;
        return this;
    }

    public WebDriverManagerConfig setWebDriverMode(WebDriverMode webDriverMode) {
        this.webDriverMode = webDriverMode;
        return this;
    }

    public boolean shouldShutdownSessionAfterTestMethod() {
        return executeCloseWindows && closeWindowsAfterTestMethod;
    }

    public WebDriverManagerConfig setShutdownSessionAfterTestMethod(boolean shutdown) {
        if (shutdown) {
            this.setShutdownSessions(true);
        }
        this.closeWindowsAfterTestMethod = shutdown;
        return this;
    }

    public boolean shouldShutdownSessionOnFailure() {
        return executeCloseWindows && closeWindowsOnFailure;
    }

    public WebDriverManagerConfig setShutdownSessionOnFailure(boolean shutdown) {
        if (shutdown) {
            this.setShutdownSessions(true);
        }
        this.closeWindowsOnFailure = shutdown;
        return this;
    }

    public boolean shouldMaximizeViewport() {
        return maximize;
    }

    public WebDriverManagerConfig setMaximizeViewport(boolean maximize) {
        this.maximize = maximize;
        return this;
    }

    public Position getMaximizePosition() {
        if (this.maximizePosition == null) {
            this.maximizePosition = Position.valueOf(PropertyManager.getProperty(TesterraProperties.BROWSER_MAXIMIZE_POSITION, Position.CENTER.toString()).toUpperCase());
        }
        return maximizePosition;
    }

    public WebDriverManagerConfig setMaximizePosition(Position maximizePosition) {
        this.maximizePosition = maximizePosition;
        return this;
    }
}
