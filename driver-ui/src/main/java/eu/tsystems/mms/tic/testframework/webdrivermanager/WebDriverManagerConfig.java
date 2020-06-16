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
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.enums.Position;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;

/**
 * Class holding configuration settings for the WebDriverManager. Some are writable. This class is not ThreadSafe, some
 * settings may not be valid.
 */
public class WebDriverManagerConfig {

    /*
     * Default values.
     */

    /**
     * Specifies if windows should be closed.
     */
    public boolean executeCloseWindows = true;
    /**
     * WebDriverMode that is used.
     */
    public WebDriverMode webDriverMode = WebDriverMode.valueOf(Testerra.Properties.WEBDRIVER_MODE.asString());

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

}
