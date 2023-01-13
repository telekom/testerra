/*
 * Testerra
 *
 * (C) 2022, Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testing.UiElementFinderFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DriverAndGuiElementTest extends AbstractTestSitesTest implements UiElementFinderFactoryProvider {

    @Test
    public void testUiElement() throws Exception {
        WebDriver driver = getWebDriver();

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(driver);
        UiElement element = uiElementFinder.find(By.id("1"));
        element.waitFor().attribute("href").getActual();
        element.waitFor().tagName().getActual();
        element.waitFor().classes().getActual();
    }

    @Test
    public void testTapCapabilities() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");
        request.setWebDriverMode(WebDriverMode.local);
        request.setBrowser(Browsers.phantomjs);

        /*
        create caps
         */
        DesiredCapabilities caps = new DesiredCapabilities();

        // start session
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);

        SessionContext sessionContext = WebDriverSessionsManager.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = sessionContext.getWebDriverRequest().getCapabilities();

        Assert.assertEquals(sessionCapabilities.get("projectId"), caps.getCapability("projectId"), "EndPoint Capability is set");
    }

    @Test
    public void testFailing() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");
        request.setWebDriverMode(WebDriverMode.local);
        request.setBrowser(Browsers.phantomjs);

        WEB_DRIVER_MANAGER.getWebDriver(request);
        Assert.assertTrue(false);
    }
}
