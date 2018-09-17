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
 * Created on 02.04.2014
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.model.HostInfo;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ReportingWithWebdriverTest extends AbstractTest {

    Logger logger = LoggerFactory.getLogger(ReportingWithWebdriverTest.class);

    /**
     * T01_WebDriverSessionInfo
     * <p/>
     * Description: T01 WebDriverSessionInfo
     */
    @Test
    public void testT01_WebDriverSessionInfo() {
        WebDriverManager.getWebDriver();
    }

    @Test
    public void testT02_ScreenshotsPreviewOff() {
        System.setProperty(FennecProperties.REPORT_SCREENSHOTS_PREVIEW, "false");
        WebDriver driver = WebDriverManager.getWebDriver();
        HostInfo hostInfo = WebDriverManagerUtils.getExecutingSeleniumHost(driver);
        logger.info("Executing host: " + hostInfo);
        UITestUtils.takeScreenshots();
        TestUtils.sleep(1000);
        Assert.assertTrue(false, "Must fail");
    }

    @Test
    public void testT03_ScreenshotsPreviewOn() {
        WebDriverManager.getWebDriver();
        UITestUtils.takeScreenshots();
        TestUtils.sleep(1000);
        Assert.assertTrue(false, "Must fail");
    }

    @Test
    public void testT04_MultipleScreenshots() {
        WebDriverManager.getWebDriver();
        for (int i = 0; i < 10; i++) {
            UITestUtils.takeScreenshots();
        }
        Assert.assertTrue(false, "Must fail");
    }

    @Test
    public void testT05_SingleScreenshot() {
        WebDriverManager.getWebDriver();
        Assert.assertTrue(false, "Must fail");
    }

}
