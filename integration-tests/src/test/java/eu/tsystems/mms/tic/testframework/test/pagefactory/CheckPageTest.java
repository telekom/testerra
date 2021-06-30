/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.test.pagefactory;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingStaticElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNonCheckableCheck;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNotExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNullElement;
import eu.tsystems.mms.tic.testframework.exceptions.PageFactoryException;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import java.io.File;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckPageTest extends AbstractTestSitesTest implements PageFactoryProvider {

    @Test
    public void testT01_checkExistingElement() throws Exception {
        PAGE_FACTORY.createPage(PageWithExistingElement.class, getClassExclusiveWebDriver());
    }

    @Test(expectedExceptions = PageFactoryException.class)
    public void testT02_checkNotExistingElement() throws Throwable {
        PAGE_FACTORY.createPage(PageWithNotExistingElement.class, getClassExclusiveWebDriver());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testT03_checkNullElement() throws Exception {
        PAGE_FACTORY.createPage(PageWithNullElement.class, getClassExclusiveWebDriver());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testT04_checkStaticElement() throws Exception {
        PAGE_FACTORY.createPage(PageWithExistingStaticElement.class, getClassExclusiveWebDriver());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testT05_checkNonCheckableElement() throws Exception {
        PAGE_FACTORY.createPage(PageWithNonCheckableCheck.class, getClassExclusiveWebDriver());
    }

    @Test
    public void testT08_CheckPage_ScreenshotOnLoad() {

        Report report = Testerra.getInjector().getInstance(Report.class);
        final File reportScreenshotDirectory = report.getReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);
        Assert.assertNotNull(reportScreenshotDirectory);

        final WebDriver driver = getClassExclusiveWebDriver();

        final int fileCountBeforeAction = getNumFiles(reportScreenshotDirectory);
        PropertyManager.getFileProperties().setProperty(TesterraProperties.SCREENSHOT_ON_PAGELOAD, "false");
        new PageWithExistingElement(driver);

        final int fileCountAfterCheckPageWithoutScreenshot = getNumFiles(reportScreenshotDirectory);
        Assert.assertEquals(fileCountBeforeAction, fileCountAfterCheckPageWithoutScreenshot, "Record Screenshot count not altered.");

        PropertyManager.getFileProperties().setProperty(TesterraProperties.SCREENSHOT_ON_PAGELOAD, "true");
        new PageWithExistingElement(driver);
        final int fileCountAfterCheckPageWithScreenshot = getNumFiles(reportScreenshotDirectory);

        Assert.assertNotEquals(fileCountAfterCheckPageWithoutScreenshot, fileCountAfterCheckPageWithScreenshot, "Record Screenshot count altered.");
    }

    private int getNumFiles(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return 0;
        } else {
            return files.length;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownScreenshotOnLoad() {
        PropertyManager.getFileProperties().setProperty(TesterraProperties.SCREENSHOT_ON_PAGELOAD, "false");
    }
}
