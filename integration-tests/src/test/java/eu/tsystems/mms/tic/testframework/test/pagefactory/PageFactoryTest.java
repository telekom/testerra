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
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.BasePage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.BasePage2016;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNotExistingElementWithoutCheckPage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PrefixBasePage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PrefixBasePage2016;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.ResponsiveWebTestPage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.ResponsiveWebTestPage_1235px_Max;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.ResponsiveWebTestPage_601px_800px;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.ResponsiveWebTestPage_801px_1234px;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.ResponsiveWebTestPage_Min_600px;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.AbstractWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.UnspecificWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerConfig;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.checkerframework.checker.units.qual.A;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests the responsive page factory for correct instantiated classes.
 */
public class PageFactoryTest extends AbstractTestSitesTest {

    private ResponsiveWebTestPage getPage() {
        return PageFactory.create(ResponsiveWebTestPage.class, getWebDriver());
    }

    @Override
    protected AbstractWebDriverRequest getWebDriverRequest() {
        UnspecificWebDriverRequest unspecificWebDriverRequest = new UnspecificWebDriverRequest();
        try {
            unspecificWebDriverRequest.setBaseUrl("http://www.google.com");
        } catch (MalformedURLException e) {
            log().error(e.getMessage());
        }
        return unspecificWebDriverRequest;
    }

    @BeforeClass
    public void before() throws MalformedURLException {
        WebDriverManagerConfig config = WebDriverManager.getConfig();
        config.setShutdownSessionAfterTestMethod(false);
    }

    @AfterClass
    public void after() {
        WebDriverManagerConfig config = WebDriverManager.getConfig();
        config.setShutdownSessionAfterTestMethod(true);
    }

    @Test
    public void testT01_InRange_1() {
        PageFactoryPrefixedTest.setViewportSize(getWebDriver(), 799, 1000);
        ResponsiveWebTestPage blaPage = getPage();
        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT02_InRange_2() {
        PageFactoryPrefixedTest.setViewportSize(getWebDriver(), 1024, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_801px_1234px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT03_ClassPerfectMatch_LowerValue() {
        PageFactoryPrefixedTest.setViewportSize(getWebDriver(), 601, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT04_ClassPerfectMatch_UpperValue() {
        PageFactoryPrefixedTest.setViewportSize(getWebDriver(), 800, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_601px_800px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT05_Match_Min() {
        PageFactoryPrefixedTest.setViewportSize(getWebDriver(), 599, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_Min_600px.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT06_Match_Max() {
        PageFactoryPrefixedTest.setViewportSize(getWebDriver(), 1600, 1000);
        ResponsiveWebTestPage blaPage = getPage();

        Assert.assertEquals(blaPage.getClass().getSimpleName(), ResponsiveWebTestPage_1235px_Max.class.getSimpleName(), "Instantiated correct page.");
    }

    @Test
    public void testT07_IgnoreOtherImpls() {
        WebDriver driver = getWebDriver();
        BasePage basePage = PageFactory.create(BasePage.class, driver);

        AssertCollector.assertFalse(basePage instanceof BasePage2016, "its a BasePage2016");
        AssertCollector.assertFalse(basePage instanceof PrefixBasePage, "its a PrefixBasePage");
        AssertCollector.assertFalse(basePage instanceof PrefixBasePage2016, "its a PrefixBasePage2016");
    }

    @Test
    public void testT08_CheckNot() {

        WebDriver driver = getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);

        final PageWithExistingElement pageWithExistingElement = PageFactory.create(PageWithExistingElement.class, driver);
        final PageWithNotExistingElementWithoutCheckPage pageWithNotExistingElement = PageFactory.checkNot(PageWithNotExistingElementWithoutCheckPage.class, driver);
    }

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
}
