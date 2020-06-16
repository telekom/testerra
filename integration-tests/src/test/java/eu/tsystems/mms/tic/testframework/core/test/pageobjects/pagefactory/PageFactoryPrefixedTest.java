/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.core.test.pageobjects.pagefactory;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.BasePage;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.BasePage2016;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PrefixBasePage;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PrefixBasePage2016;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PrefixResponsiveWebTestPage_1235px_Max;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PrefixResponsiveWebTestPage_601px_800px;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PrefixResponsiveWebTestPage_801px_1234px;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PrefixResponsiveWebTestPage_Min_600px;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.ResponsiveWebTestPage;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.TLPrefix1ResponsiveWebTestPage_601px_800px;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.TLPrefix1ResponsiveWebTestPage_801px_1234px;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.TLPrefix2ResponsiveWebTestPage_601px_800px;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.TLPrefix2ResponsiveWebTestPage_801px_1234px;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests the responsive page factory for correct instantiated classes.
 */
public class PageFactoryPrefixedTest extends AbstractTestSitesTest implements Loggable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageFactoryPrefixedTest.class);

    /**
     * Set the browser size by adjusting it to the given viewport size.
     *
     * @param driver The web driver
     * @param width The target width
     * @param height The target height
     */
    public static void setViewportSize(final WebDriver driver, final int width, final int height) {
        try {
            int innerWidth = (int) (long) (Long) JSUtils.executeScript(driver, "return window.innerWidth");
            int innerHeight = (int) (long) (Long) JSUtils.executeScript(driver, "return window.innerHeight");

            int outerWidth = (int) (long) (Long) JSUtils.executeScript(driver, "return window.outerWidth");
            int outerHeight = (int) (long) (Long) JSUtils.executeScript(driver, "return window.outerHeight");

            int diffX = outerWidth - innerWidth;
            int diffY = outerHeight - innerHeight;


            int w = width + diffX;
            int h = height + diffY;

            if (w <= 0 || h <= 0) {
                w = width;
                h = height;
            }

            LOGGER.info("Setting browser size to " + w + "x" + h);
            driver.manage().window().setSize(new Dimension(w, h));
        } catch (Exception e) {
            throw new TesterraRuntimeException("Unable to set viewport size", e);
        }
    }

    public ResponsiveWebTestPage getPage() {
        return PageFactory.create(ResponsiveWebTestPage.class, WebDriverManager.getWebDriver());
    }

    String baseURL = "unset";

    @BeforeClass(alwaysRun = true)
    public void before() {
        PageFactory.clearCache();
        PageFactory.setGlobalPagesPrefix("Prefix");
        baseURL = WebDriverManager.getBaseURL();
        WebDriverManager.setBaseURL("http://www.google.com");

        WebDriverManager.config().closeWindowsAfterTestMethod = false;
    }

    @AfterClass(alwaysRun = true)
    public void after() {
        PageFactory.setGlobalPagesPrefix(null);
        WebDriverManager.setBaseURL(baseURL);
        WebDriverManager.config().closeWindowsAfterTestMethod = true;
    }

    @Test
    public void testT01_InRange_1() {
        setViewportSize(WebDriverManager.getWebDriver(), 799, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), PrefixResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT01_InRange_1_ThreadLocalPrefix1() {
        PageFactory.setThreadLocalPagesPrefix("TLPrefix1");
        setViewportSize(WebDriverManager.getWebDriver(), 799, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        try {
            Assert.assertEquals(blaPage.getClass().getSimpleName(), TLPrefix1ResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
        }
        finally {
            PageFactory.clearThreadLocalPagesPrefix();
        }
    }

    @Test
    public void testT01_InRange_1_ThreadLocalPrefix2() {
        PageFactory.setThreadLocalPagesPrefix("TLPrefix2");
        setViewportSize(WebDriverManager.getWebDriver(), 799, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), TLPrefix2ResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
        PageFactory.clearThreadLocalPagesPrefix();
    }

    @Test
    public void testT02_InRange_2() {
        setViewportSize(WebDriverManager.getWebDriver(), 1024, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), PrefixResponsiveWebTestPage_801px_1234px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT02_InRange_2_ThreadLocalPrefix1() {
        PageFactory.setThreadLocalPagesPrefix("TLPrefix1");
        setViewportSize(WebDriverManager.getWebDriver(), 1024, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        try {
            Assert.assertEquals(blaPage.getClass().getSimpleName(), TLPrefix1ResponsiveWebTestPage_801px_1234px.class.getSimpleName(), "Instantiated correct page.");
        } finally {
            PageFactory.clearThreadLocalPagesPrefix();
        }
    }

    @Test
    public void testT02_InRange_2_ThreadLocalPrefix2() {
        PageFactory.setThreadLocalPagesPrefix("TLPrefix2");
        setViewportSize(WebDriverManager.getWebDriver(), 1024, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        try {
            Assert.assertEquals(blaPage.getClass().getSimpleName(), TLPrefix2ResponsiveWebTestPage_801px_1234px.class.getSimpleName(), "Instantiated correct page.");
        }
        finally {
            PageFactory.clearThreadLocalPagesPrefix();
        }
    }

    @Test
    public void testT03_ClassPerfectMatch_LowerValue() {
        setViewportSize(WebDriverManager.getWebDriver(), 601, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), PrefixResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT04_ClassPerfectMatch_UpperValue() {
        setViewportSize(WebDriverManager.getWebDriver(), 800, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), PrefixResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT05_Match_Min() {
        setViewportSize(WebDriverManager.getWebDriver(), 599, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), PrefixResponsiveWebTestPage_Min_600px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT06_Match_Max() {
        setViewportSize(WebDriverManager.getWebDriver(), 1600, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), PrefixResponsiveWebTestPage_1235px_Max.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT07_LoadBasePage() {
        WebDriver driver = WebDriverManager.getWebDriver();
        BasePage basePage = PageFactory.create(BasePage.class, driver);

        AssertCollector.assertFalse(basePage instanceof BasePage2016, "its a BasePage2016");
        AssertCollector.assertFalse(basePage instanceof PrefixBasePage2016, "its a PrefixBasePage2016");
        AssertCollector.assertTrue(basePage instanceof PrefixBasePage, "its a PrefixBasePage");
    }

}
