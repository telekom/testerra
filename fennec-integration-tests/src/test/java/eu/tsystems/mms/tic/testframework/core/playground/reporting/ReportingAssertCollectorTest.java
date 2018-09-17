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
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ReportingAssertCollectorTest extends AbstractTest {

    /*
    AnnotationTransformerListener only works with global listener. So execute the AnnotationTransformerListener.xml from resources.
     */

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testT01Collect() {
        AssertCollector.assertTrue(false, "CA 1");
    }

    @Test
    public void testT02CollectWithMinor() {
        AssertCollector.assertTrue(false, "CA 1");
        NonFunctionalAssert.assertTrue(false, "NF1");
        AssertCollector.assertEquals(false, true, "CA 2");
    }

    @Test
    public void testPassed() {
    }

    @Test
    public void testFailed() {
        Assert.assertTrue(false, "must fail");
    }

    @Test
    public void testFailedWithMinor() {
        NonFunctionalAssert.assertTrue(false, "NF1");
        Assert.assertTrue(false, "must fail");
    }

//    @Test
//    public void testGuiElementExplicitly() {
//        WebDriver driver = WebDriverManager.getWebDriver();
//        new GuiElement(driver, By.id("not_existing_1")).assertCollector().assertIsDisplayed();
//        new GuiElement(driver, By.id("not_existing_2")).assertCollector().assertIsDisplayed();
//        new GuiElement(driver, By.id("not_existing_3")).assertCollector().assertIsDisplayed();
//    }
//
//    @Test
//    public void testGuiElementDefaultAssertCollector() {
//        WebDriver driver = WebDriverManager.getWebDriver();
//        GuiElement.setDefaultAssertCollector(true);
//        new GuiElement(driver, By.id("not_existing_1")).asserts().assertIsDisplayed();
//        new GuiElement(driver, By.id("not_existing_2")).asserts().assertIsDisplayed();
//        new GuiElement(driver, By.id("not_existing_3")).asserts().assertIsDisplayed();
//    }

    @AfterSuite
    public void afterSuite() {
        int allFailed = TestStatusController.getAllFailed();
        int testsFailed = TestStatusController.getTestsFailed();
        System.out.println("test: " + testsFailed + " all: " + allFailed);
    }
}
