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

package eu.tsystems.mms.tic.testframework.test.pagefactory;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.BasePage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.GuiElementListPage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithPageLoadedCallback;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Tests the responsive page factory for correct instantiated classes.
 */
public class PageFactoryTest extends AbstractTestSitesTest implements PageFactoryProvider, PropertyManagerProvider {

    @Test
    public void testT08_CheckPage_ScreenshotOnLoad() {

        final File reportScreenshotDirectory = Testerra.getInjector().getInstance(Report.class).getReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);
        Assert.assertNotNull(reportScreenshotDirectory);

        final WebDriver driver = getWebDriver();

        final int fileCountBeforeAction = getNumFiles(reportScreenshotDirectory);
        PROPERTY_MANAGER.setTestLocalProperty(Testerra.Properties.SCREENSHOT_ON_PAGELOAD, false);
        PAGE_FACTORY.createPage(PageWithExistingElement.class, driver);

        final int fileCountAfterCheckPageWithoutScreenshot = getNumFiles(reportScreenshotDirectory);
        Assert.assertEquals(fileCountBeforeAction, fileCountAfterCheckPageWithoutScreenshot, "Record Screenshot count not altered.");

        PROPERTY_MANAGER.setTestLocalProperty(Testerra.Properties.SCREENSHOT_ON_PAGELOAD, true);
        PAGE_FACTORY.createPage(PageWithExistingElement.class, driver);
        final int fileCountAfterCheckPageWithScreenshot = getNumFiles(reportScreenshotDirectory);

        Assert.assertNotEquals(fileCountAfterCheckPageWithoutScreenshot, fileCountAfterCheckPageWithScreenshot, "Record Screenshot count altered.");
    }

    @Test
    public void testT09_pageLoadedCallback() {
        WebDriver webDriver = getWebDriver();
        PageWithPageLoadedCallback page1 = new PageWithPageLoadedCallback(webDriver);
        Assert.assertFalse(page1.isLoaded);

        PageWithPageLoadedCallback page2 = PAGE_FACTORY.createPage(PageWithPageLoadedCallback.class, webDriver);
        Assert.assertTrue(page2.isLoaded);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "PageFactory create loop detected loading.*")
    public void testT10_LoopDetectionTest_Exception() {
        WebDriver driver = getWebDriver();
        for (int i = 0; i < 6; i++) {
            PAGE_FACTORY.createPage(PageWithExistingElement.class, driver);
        }
    }

    @Test()
    public void testT11_LoopDetectionTest_Passed() {
        WebDriver driver = getWebDriver();
        for (int i = 0; i < 6; i++) {
            PAGE_FACTORY.createPage(GuiElementListPage.class, driver);
            PAGE_FACTORY.createPage(BasePage.class, driver);
        }
    }

    @DataProvider(name = "LoopDetectionInDataProvider")
    public Object[][] testT12_Dataprovider() {
        return new Object[][]{
                {"Test_1"}, {"Test_2"}, {"Test_3"}
        };
    }

    /**
     * PageFactory loop detection buffer is cleared after every method
     *
     * @param loop
     */
    @Test(dataProvider = "LoopDetectionInDataProvider")
    public void testT12_LoopDetectionTest_DataProvider_ParallelFalse(String loop) {
        WebDriver driver = getWebDriver();
        for (int i = 0; i < 4; i++) {
            PAGE_FACTORY.createPage(BasePage.class, driver);
        }
    }

    private int getNumFiles(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return 0;
        } else {
            return files.length;
        }
    }

}
