/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithExistingStaticElement;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithNonCheckableCheck;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithNotExistingElement;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithNullElement;
import eu.tsystems.mms.tic.testframework.exceptions.PageNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithExistingStaticElement;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithNonCheckableCheck;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithNotExistingElement;
import eu.tsystems.mms.tic.testframework.pageobjects.PageWithNullElement;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.report.general.TestsUnderTestGroup;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;

public class CheckPageTest extends AbstractTestSitesTest {

    @Test
    public void testT01_checkExistingElement() throws Exception {
        pageFactory.createPage(PageWithExistingElement.class);
    }

    @Test(expectedExceptions = PageNotFoundException.class)
    public void testT02_checkNotExistingElement() throws Exception {
        pageFactory.createPage(PageWithNotExistingElement.class);
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT03_checkNullElement() throws Exception {
        pageFactory.createPage(PageWithNullElement.class);
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT04_checkStaticElement() throws Exception {
        pageFactory.createPage(PageWithExistingStaticElement.class);
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT05_checkNonCheckableElement() throws Exception {
        pageFactory.createPage(PageWithNonCheckableCheck.class);
    }

    @Test
    public void testT08_CheckPage_ScreenshotOnLoad() {

        final File reportScreenshotDirectory = Report.SCREENSHOTS_DIRECTORY;
        Assert.assertNotNull(reportScreenshotDirectory);
        Assert.assertTrue(reportScreenshotDirectory.exists());
        Assert.assertTrue(reportScreenshotDirectory.isDirectory());
        Assert.assertNotNull(reportScreenshotDirectory.listFiles());

        final WebDriver driver = WebDriverManager.getWebDriver();

        final int fileCountBeforeAction = reportScreenshotDirectory.listFiles().length;
        PropertyManager.getFileProperties().setProperty(TesterraProperties.SCREENSHOT_ON_PAGELOAD, "false");
        new PageWithExistingElement(driver);

        final int fileCountAfterCheckPageWithoutScreenshot = reportScreenshotDirectory.listFiles().length;
        Assert.assertEquals(fileCountBeforeAction, fileCountAfterCheckPageWithoutScreenshot, "Record Screenshot count not altered.");

        PropertyManager.getFileProperties().setProperty(TesterraProperties.SCREENSHOT_ON_PAGELOAD, "true");
        new PageWithExistingElement(driver);
        final int fileCountAfterCheckPageWithScreenshot = reportScreenshotDirectory.listFiles().length;

        Assert.assertNotEquals(fileCountAfterCheckPageWithoutScreenshot, fileCountAfterCheckPageWithScreenshot, "Record Screenshot count altered.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownScreenshotOnLoad() {
        PropertyManager.getFileProperties().setProperty(TesterraProperties.SCREENSHOT_ON_PAGELOAD, "false");
    }
}
