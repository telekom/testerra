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
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.WebTestPage;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 * 
 * @author pele
 */
public class ReportingCompleteTestMethodResultsTest extends AbstractTest {

    static {
        System.setProperty(FennecProperties.MODULE_SOURCE_ROOT, "fennec-core-tests/src");
    }

    /*
     * AnnotationTransformerListener only works with global listener. So execute the AnnotationTransformerListener.xml
     * from resources.
     */

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testSimpleAssert() {
        logger.info("Something 1");
        Assert.assertTrue(false);
    }

    @Test
    public void testSimpleException() throws Exception {
        logger.info("Something 2");
        throw new Exception("Damn, an exception");
    }

    @Test
    public void testSkip() {
        throw new SkipException("MUST Skip");
    }

    @Test
    public void testGuiAssertInInit() {
        POConfig.setUiElementTimeoutInSeconds(3);
        WebDriver driver = WebDriverManager.getWebDriver();
        // driver.get(WebTestPage.URL);
        WebTestPage fennecWebTestPage = new WebTestPage(driver);
        fennecWebTestPage.gotoHell();
    }

    @Test
    public void testAnotherFailingTest() {
        POConfig.setUiElementTimeoutInSeconds(3);
        WebDriver driver = WebDriverManager.getWebDriver();
        // driver.get(WebTestPage.URL);
        WebTestPage fennecWebTestPage = new WebTestPage(driver);
        fennecWebTestPage.gotoHell();
    }

    @Test
    public void testGuiExceptionOnElement() {
        POConfig.setUiElementTimeoutInSeconds(3);
        WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(WebTestPage.URL);
        WebTestPage fennecWebTestPage = new WebTestPage(driver);
        fennecWebTestPage.takeScreenshot();
        fennecWebTestPage.takeScreenshot();
        fennecWebTestPage.gotoHell();
    }

    @Test
    public void testGuiMinor() {
        POConfig.setUiElementTimeoutInSeconds(3);
        WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(WebTestPage.URL);
        WebTestPage fennecWebTestPage = new WebTestPage(driver);
        fennecWebTestPage.nonfunctionalAssert();
    }

    @Test
    public void testPass() {
        logger.info("Something 3");
        Assert.assertTrue(true);
    }

    @Test
    public void testMinor() {
        NonFunctionalAssert.assertTrue(false, "ohoh");
        logger.info("Something 3");
        Assert.assertTrue(true);
    }

}
