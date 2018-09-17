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
/*
 * Created on 07.08.2012
 *
 * Copyright(c) 2012 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.ErrorMessages;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class holding configuration settings for the WebDriverManager. Some are writable. This class is not ThreadSafe, some
 * settings may not be valid.
 */
public class WebDriverManagerConfig {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverManagerConfig.class);

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
    public WebDriverMode webDriverMode = WebDriverMode.local;

    /**
     * Take SCreenshots.
     */
    public boolean takeScreenShotsActive = PropertyManager.getBooleanProperty(FennecProperties.AUTOSCREENSHOTS, true);
    /**
     * Close windows after Test Methods.
     */
    public boolean closeWindowsAfterTestMethod = PropertyManager.getBooleanProperty(
            FennecProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS,
            true);
    /**
     * Close windows on failure.
     */
    public boolean closeWindowsOnFailure = PropertyManager.getBooleanProperty(
            FennecProperties.CLOSE_WINDOWS_ON_FAILURE,
            true);

    public boolean maximize = PropertyManager.getBooleanProperty(FennecProperties.Fennec_BROWSER_MAXIMIZE, false);

    /**
     * Browser global setting.
     */
    public String browser = PropertyManager.getProperty(FennecProperties.BROWSER, null);
    public String browserVersion = PropertyManager.getProperty(FennecProperties.BROWSER_VERSION, null);

    /**
     * Default constructor.
     */
    public WebDriverManagerConfig() {
        init();
    }

    /**
     * Init config values.
     */
    public void init() {
        if (browser == null) {
            LOGGER.warn(ErrorMessages.browserNotSetInAnyProperty());
        }

        /*
        set remote
         */
        initWebDriverMode();
    }

    /**
     * Returns the webdriver mode.
     * 
     * @return the webDriverMode
     */
    private WebDriverMode initWebDriverMode() {
        String modeString = PropertyManager.getProperty(FennecProperties.WEBDRIVERMODE, webDriverMode.name()).trim();
        webDriverMode = WebDriverMode.valueOf(modeString);
        return webDriverMode;
    }

}
