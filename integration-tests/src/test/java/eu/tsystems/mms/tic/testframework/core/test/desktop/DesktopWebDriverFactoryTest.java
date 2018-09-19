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
package eu.tsystems.mms.tic.testframework.core.test.desktop;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DesktopWebDriverFactoryTest extends AbstractTest {

    @Test
    public void testT01_BaseURL() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.baseUrl = "http://google.de";
        request.webDriverMode = WebDriverMode.local;
        request.browser = Browsers.phantomjs;

        WebDriver driver = WebDriverManager.getWebDriver(request);
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("google"), "Current URL contains google - actual: " + currentUrl);
    }

    @Test
    public void testT01_BaseURL_NotSet() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.webDriverMode = WebDriverMode.local;
        request.browser = Browsers.phantomjs;

        WebDriver driver = WebDriverManager.getWebDriver(request);
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("192.168"), "Current URL contains local ip - actual: " + currentUrl);
    }
}
