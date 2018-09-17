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
 * Created on 07.03.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ReportingBasicAuthTest extends AbstractTest {

    static {
        System.setProperty(FennecProperties.XETA_WATCHDOG_TIMEOUT_SECONDS, "120");
    }

    Logger logger = LoggerFactory.getLogger(this.getClass());

    int i = 0;

    /**
     * FailFirstPassSecond
     * <p/>
     * Description: FailFirstPassSecond
     */
    @Test
    public void testT01_BasicAuth_WatchDogTest() {
        if (i > 0) {
            return; // passed on second run
        }
        i++;
        FirefoxProfile profile = new FirefoxProfile();
        WebDriverManager.getThreadCapabilities().put(FirefoxDriver.PROFILE, profile);
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get("https://development.soliver.de/");
//        driver.get("https://storefront:sOliver11dw@development.soliver.de/");

        // bad steps:
//
        GuiElement countryLink = new GuiElement(driver, By.id("countryLink"));
        GuiElement nederlands = new GuiElement(driver, By.linkText("Nederlands"));
        countryLink.mouseOver();
//        try {
            nederlands.click();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(new Date());
            driver.getWindowHandle();
//            return;
//        }

        // backup if test passes
//        WebDriverManager.report();
//        final org.openqa.selenium.TimeoutException timeoutException = new org.openqa.selenium.TimeoutException("Timed out waiting for page load");
//        throw new TimeoutException("dummy", timeoutException);
    }


}
