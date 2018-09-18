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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.pagefactory;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.*;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests the responsive page factory for correct instantiated classes.
 */
public class PageFactoryTest extends AbstractTestSitesTest {

    private ResponsiveWebTestPage getPage() {
        return PageFactory.create(ResponsiveWebTestPage.class, WebDriverManager.getWebDriver());
    }

    String baseURL = "unset";

    @BeforeClass
    public void before() {
        baseURL = WebDriverManager.getBaseURL();
        WebDriverManager.setBaseURL("http://www.google.com");
        WebDriverManager.config().closeWindowsAfterTestMethod = false;

//        DesiredCapabilities caps = new DesiredCapabilities();
//        WebDriverManagerUtils.addProxyToCapabilities(caps, "proxy.mms-dresden.de:8080");
//        WebDriverManager.setGlobalExtraCapabilities(caps);
    }

    @AfterClass
    public void after() {
        WebDriverManager.setBaseURL(baseURL);
        WebDriverManager.config().closeWindowsAfterTestMethod = true;
    }

    @Test
    public void testT01_InRange_1() {
        PageFactoryPrefixedTest.setViewportSize(WebDriverManager.getWebDriver(), 799, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT02_InRange_2() {
        PageFactoryPrefixedTest.setViewportSize(WebDriverManager.getWebDriver(), 1024, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_801px_1234px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT03_ClassPerfectMatch_LowerValue() {
        PageFactoryPrefixedTest.setViewportSize(WebDriverManager.getWebDriver(), 601, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT04_ClassPerfectMatch_UpperValue() {
        PageFactoryPrefixedTest.setViewportSize(WebDriverManager.getWebDriver(), 800, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT05_Match_Min() {
        PageFactoryPrefixedTest.setViewportSize(WebDriverManager.getWebDriver(), 599, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_Min_600px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT06_Match_Max() {
        PageFactoryPrefixedTest.setViewportSize(WebDriverManager.getWebDriver(), 1600, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_1235px_Max.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT07_IgnoreOtherImpls() {
        WebDriver driver = WebDriverManager.getWebDriver();
        BasePage basePage = PageFactory.create(BasePage.class, driver);

        AssertCollector.assertFalse(basePage instanceof BasePage2016, "its a BasePage2016");
        AssertCollector.assertFalse(basePage instanceof PrefixBasePage, "its a PrefixBasePage");
        AssertCollector.assertFalse(basePage instanceof PrefixBasePage2016, "its a PrefixBasePage2016");
    }

}
