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
 package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverCapabilities;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import java.util.Map;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DriverAndGuiElementTest extends AbstractTestSitesTest {

    static {
        System.setProperty("tt.browser.setting", "firefox:66");
    }

    @Test
    public void testGuiElement() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl(WebDriverManager.config().getBaseUrl());
        request.setWebDriverMode(WebDriverMode.local);
        request.setBrowser(Browsers.phantomjs);
        request.setBrowserVersion("egal");

//        DesiredCapabilities caps = new DesiredCapabilities();
//        WebDriverManagerUtils.addProxyToCapabilities(caps, "proxyblabla");
//        WebDriverManager.setGlobalExtraCapabilities(caps);

        WebDriver driver = WebDriverManager.getWebDriver(request);
        PageFactory.create(PageWithExistingElement.class, driver);
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
        // register caps
        DesktopWebDriverCapabilities.registerEndPointCapabilities(Pattern.compile(".*localhost.*"), caps);

        // start session
        WebDriver driver = WebDriverManager.getWebDriver(request);

        WebDriverRequest relatedRequest = WebDriverManager.getRelatedWebDriverRequest(driver);
        Map<String, Object> sessionCapabilities = ((DesktopWebDriverRequest) relatedRequest).getSessionCapabilities();

        Assert.assertEquals(sessionCapabilities.get("projectId"), caps.getCapability("projectId"), "EndPoint Capability is set");
    }

    @Test
    public void testFailing() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");
        request.setWebDriverMode(WebDriverMode.local);
        request.setBrowser(Browsers.phantomjs);

        WebDriverManager.getWebDriver(request);
        Assert.assertTrue(false);
    }
}
