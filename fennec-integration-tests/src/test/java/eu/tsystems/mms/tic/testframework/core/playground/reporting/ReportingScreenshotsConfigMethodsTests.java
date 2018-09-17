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
 * Created on 22.02.2013
 *
 * Copyright(c) 2013 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class LoggingTests.
 *
 * @author pele
 */
@Listeners(FennecListener.class)
public class ReportingScreenshotsConfigMethodsTests {

//    @BeforeMethod
//    @BeforeClass
//    @BeforeTest
//    @BeforeSuite
    @AfterSuite
    public void testScreenshot1() {
        errorExecution();
    }

    @Test
    public void testTest() throws Exception {
    }

    private void errorExecution() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement notHere = new GuiElement(driver, By.id("nothere"));
        notHere.setTimeoutInSeconds(1);
        notHere.assertIsPresent();
    }

    @BeforeClass
    public void setup() {
        String url = "http://www.google.de";
        WebDriverManager.setBaseURL(url);
    }
}
