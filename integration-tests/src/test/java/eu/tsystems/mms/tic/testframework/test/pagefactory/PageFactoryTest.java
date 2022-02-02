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
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.BasePage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.GuiElementListPage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tests the responsive page factory for correct instantiated classes.
 */
public class PageFactoryTest extends AbstractTestSitesTest {

    @Test
    public void test_pageLoadedCallback() {
        WebDriver webDriver = getWebDriver();
        AtomicBoolean atomicBoolean = new AtomicBoolean();

        Page testPage = new Page(webDriver) {
            @Override
            protected void pageLoaded() {
                super.pageLoaded();
                atomicBoolean.set(true);
            }
        };

        testPage.checkPage();
        Assert.assertTrue(atomicBoolean.get());
    }

    @Test
    public void testT08_CheckPage_ScreenshotOnLoad() {

        final File reportScreenshotDirectory = TesterraListener.getReport().getReportDirectory(Report.SCREENSHOTS_FOLDER_NAME);
        Assert.assertNotNull(reportScreenshotDirectory);

        final WebDriver driver = getWebDriver();

        final int fileCountBeforeAction = getNumFiles(reportScreenshotDirectory);
        PropertyManager.getTestLocalProperties().setProperty(TesterraProperties.SCREENSHOT_ON_PAGELOAD, "false");
        new PageWithExistingElement(driver);

        final int fileCountAfterCheckPageWithoutScreenshot = getNumFiles(reportScreenshotDirectory);
        Assert.assertEquals(fileCountBeforeAction, fileCountAfterCheckPageWithoutScreenshot, "Record Screenshot count not altered.");

        PropertyManager.getTestLocalProperties().setProperty(TesterraProperties.SCREENSHOT_ON_PAGELOAD, "true");
        new PageWithExistingElement(driver);
        final int fileCountAfterCheckPageWithScreenshot = getNumFiles(reportScreenshotDirectory);

        Assert.assertNotEquals(fileCountAfterCheckPageWithoutScreenshot, fileCountAfterCheckPageWithScreenshot, "Record Screenshot count altered.");
    }

//    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "PageFactory create loop detected loading.*")
//    public void testT10_LoopDetectionTest_Exception() {
//        WebDriver driver = WebDriverManager.getWebDriver();
//        for (int i = 0; i < 6; i++) {
//            PageFactory.create(GuiElementListPage.class, driver);
//        }
//    }
//
//    @Test()
//    public void testT11_LoopDetectionTest_Passed() {
//        WebDriver driver = WebDriverManager.getWebDriver();
//        for (int i = 0; i < 6; i++) {
//            PageFactory.create(GuiElementListPage.class, driver);
//            PageFactory.create(BasePage.class, driver);
//        }
//    }
//
//    @DataProvider(name = "LoopDetectionInDataProvider", parallel = false)
//    public Object[][] testT12_Dataprovider() {
//        return new Object[][]{
//                {"Test_1"}, {"Test_2"}, {"Test_3"}
//        };
//    }

    /**
     * PageFactory loop detection buffer is cleared after every method
     *
     * @param loop
     */
    @Test(dataProvider = "LoopDetectionInDataProvider")
    public void testT12_LoopDetectionTest_DataProvider_ParallelFalse(String loop) {
        WebDriver driver = WebDriverManager.getWebDriver();
        for (int i = 0; i < 2; i++) {
            PageFactory.create(BasePage.class, driver);
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
