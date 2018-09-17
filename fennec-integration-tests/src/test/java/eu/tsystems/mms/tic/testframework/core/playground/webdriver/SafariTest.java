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
package eu.tsystems.mms.tic.testframework.core.playground.webdriver;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

public class SafariTest extends AbstractTest {

    private final String ACCESS_KEY = "eyJ4cC51IjozMzI4OTcsInhwLnAiOjE1LCJ4cC5tIjoiTVRVek1Ua3dNemMzTURJek1RIiwiYWxnIjoiSFMyNTYifQ.eyJleHAiOjE4NDcyNjM3NzAsImlzcyI6ImNvbS5leHBlcml0ZXN0In0.goP1yQozMGagvMX4BVHHLNNPsU1VkQmuva5AzNrm-fI";

    private WebDriver createSessionOnMDC(String sessionKey) {
        DesktopWebDriverRequest r = new DesktopWebDriverRequest();
        r.webDriverMode = WebDriverMode.remote;
        r.seleniumServerURL = "https://mobiledevicecloud.t-systems-mms.eu:443/wd/hub";
        r.browser = Browsers.safari;
        r.browserVersion = "11.1";

        r.sessionKey = sessionKey;

        /*
        capabilities.setCapability("accessKey", ACCESS_KEY);
        capabilities.setCapability("requireWindowFocus", true);

         */
        r.sessionCapabilities.put("accessKey", ACCESS_KEY);
        r.sessionCapabilities.put("requireWindowFocus", true);

        r.baseUrl = "http://www.google.com";

        return WebDriverManager.getWebDriver(r);
    }

    private WebDriver createSessionOnGrid(String sessionKey) {
        DesktopWebDriverRequest r = new DesktopWebDriverRequest();
        r.webDriverMode = WebDriverMode.remote;
        r.seleniumServerURL = "http://test:test@192.168.40.26:4011/wd/hub";
//        r.seleniumServerURL = "http://test:test@192.168.61.183:4443/wd/hub";
        r.browser = Browsers.safari;
        r.browserVersion = "11";

        r.sessionKey = sessionKey;

        r.sessionCapabilities.put("accessKey", ACCESS_KEY);
        r.sessionCapabilities.put("requireWindowFocus", true);

        r.baseUrl = "http://www.google.com";

        return WebDriverManager.getWebDriver(r);
    }

    @Test
    public void testSafariMDC() {
        WebDriver driver = createSessionOnMDC("1");
        WebDriver driver2 = createSessionOnMDC("2");
        driver.getTitle();
        throw new RuntimeException("bla");
    }

    @Test
    public void testSafariViaGrid3() {
        WebDriver driver = createSessionOnGrid("1");
        driver.getTitle();
        throw new RuntimeException("bla");
    }
}
