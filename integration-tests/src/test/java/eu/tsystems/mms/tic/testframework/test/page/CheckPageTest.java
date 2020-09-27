/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.test.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingStaticElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNonCheckableCheck;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNotExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNullElement;
import eu.tsystems.mms.tic.testframework.exceptions.PageNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.io.File;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class CheckPageTest extends AbstractTestSitesTest implements PageFactoryProvider {

    @Test
    public void testT01_checkExistingElement() throws Exception {
        pageFactory.createPage(PageWithExistingElement.class, getWebDriver());
    }

    @Test(expectedExceptions = PageNotFoundException.class)
    public void testT02_checkNotExistingElement() throws Throwable {
        try {
            pageFactory.createPage(PageWithNotExistingElement.class, getWebDriver());
        } catch (Throwable e) {
            do {
                e = e.getCause();
                if (e instanceof PageNotFoundException) {
                    throw e;
                }
            } while (e != null);
        }
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT03_checkNullElement() throws Exception {
        pageFactory.createPage(PageWithNullElement.class, getWebDriver());
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT04_checkStaticElement() throws Exception {
        pageFactory.createPage(PageWithExistingStaticElement.class, getWebDriver());
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT05_checkNonCheckableElement() throws Exception {
        pageFactory.createPage(PageWithNonCheckableCheck.class, getWebDriver());
    }

    @Test
    public void testT08_CheckPage_ScreenshotOnLoad() {

        Report report = Testerra.injector.getInstance(Report.class);
        final File reportScreenshotDirectory = report.getReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);
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
