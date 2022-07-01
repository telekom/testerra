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

package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.UiElementShadowDomMenuPage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.UiElementShadowDomWeatherPage;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.UiElementShadowRootPage;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.exceptions.UiElementAssertionError;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

public class UiElementShadowRootTest extends AbstractExclusiveTestSitesTest<UiElementShadowRootPage> implements PageFactoryTest {

    @Override
    protected TestPage getTestPage() {
        return TestPage.SHADOW_ROOT;
    }

    @Override
    public Class<UiElementShadowRootPage> getPageClass() {
        return UiElementShadowRootPage.class;
    }

    @Test
    public void testT01_ShadowRootVisibility() {

        TestStep.begin("Step 1 - Call shadow root page");
        final UiElementShadowRootPage page = getPage();

        TestStep.begin("Step 2 - Assert correct visibility");
        page.assertShadowRootVisibility();
    }

    @Test
    public void testT02_ShadowRootInput() {

        final String expectedText = "asserting your shadow";

        TestStep.begin("Step 1 - Call shadow root page");
        final UiElementShadowRootPage page = getPage();

        TestStep.begin("Step 2 - type '" + expectedText + "' to shadow root input");
        page.typeText(expectedText);

        TestStep.begin("Step 3 - assert '" + expectedText + "' is in displayed in shadow root input");
        page.assertInputText(expectedText);
    }

    @Test(expectedExceptions = UiElementAssertionError.class)
    public void testT03_ShadowRoot_find_byClassName_fails() {
        UiElement shadowRootElement = getPage().shadowRootElement;
        Assert.assertTrue(shadowRootElement.find(XPath.from("div").classes("shadow-content")).waitFor().displayed(true));
        shadowRootElement.find(By.className("shadow-content")).expect().present(true);
    }

    @Test(expectedExceptions = UiElementAssertionError.class)
    public void testT04_ShadowRoot_findById_fails() {
        UiElement shadowRootElement = getPage().shadowRootElement;
        Assert.assertTrue(shadowRootElement.find(XPath.from("div").attribute("id", "shadow-content")).waitFor().displayed(true));
        shadowRootElement.findById("shadow-content").expect().present(true);
    }

    @Test
    public void testT05_ShadowRoot_external_page() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("https://examples.code-fever.de/2020/selenium-shadow-root/demosite/");
        request.setServerUrl("http://grid3.testauto.mms-at-work.de:4444/wd/hub");
        request.setBrowser(Browsers.firefox);
        request.setBrowserVersion("99");
        request.getDesiredCapabilities().setCapability("enableVNC", true);

        UiElementShadowRootPage page = PAGE_FACTORY.createPage(UiElementShadowRootPage.class, WEB_DRIVER_MANAGER.getWebDriver(request));
        page.assertShadowRootVisibility();
        log().info(page.shadowContentParagraph.waitFor().text().getActual());
    }

    @Test
    public void testT05_ShadowRoot_external_weather_page() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("https://react-shadow.herokuapp.com/Berlin");
        request.setBrowser(Browsers.chrome);
        request.setWindowSize(new Dimension(1200, 800));
        request.setBrowserVersion("99");
//        request.getDesiredCapabilities().setCapability("enableVNC", true);

        UiElementShadowDomWeatherPage page = PAGE_FACTORY.createPage(UiElementShadowDomWeatherPage.class, WEB_DRIVER_MANAGER.getWebDriver(request));
        page.changeCity("Munich");
        log().info(page.getWeather());
        page.getTemperature().assertThat().text().contains("°C");
    }

    @Test
    public void testT05_ShadowRoot_external_menu_page() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("https://www.htmlelements.com/demos/menu/shadow-dom/index.htm");
        request.setBrowser(Browsers.chrome);
        request.setWindowSize(new Dimension(1200, 800));
        request.setBrowserVersion("99");
//        request.getDesiredCapabilities().setCapability("enableVNC", true);

        UiElementShadowDomMenuPage page = PAGE_FACTORY.createPage(UiElementShadowDomMenuPage.class, WEB_DRIVER_MANAGER.getWebDriver(request));
        page.openMenu("Encoding");
    }
}
