/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.test.webdrivermanager;

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverCapabilities;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import java.util.Map;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DesktopWebDriverFactoryTest extends TesterraTest {

    @Test
    public void testT01_BaseURL() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");
        //request.webDriverMode = WebDriverMode.local;
        //request.browser = Browsers.phantomjs;

        WebDriver driver = WebDriverManager.getWebDriver(request);
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("google"), "Current URL contains google - actual: " + currentUrl);
    }

    @Test
    public void testT02_BaseURL_NotSet() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        //request.webDriverMode = WebDriverMode.local;
        //request.browser = Browsers.phantomjs;

        WebDriver driver = WebDriverManager.getWebDriver(request);
        String currentUrl = driver.getCurrentUrl();
        // Empty baseUrl of Chrome
        Assert.assertTrue(currentUrl.contains("data"), "Current URL is invalid - actual: " + currentUrl);
    }

    @Test
    public void testT03_EndPointCapabilities() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");
        //request.webDriverMode = WebDriverMode.local;
        //request.browser = Browsers.phantomjs;

        /*
        create caps
         */
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("enableVideo", true);
        caps.setCapability("enableVNC", true);

        // register caps
        DesktopWebDriverCapabilities.registerEndPointCapabilities(Pattern.compile(".*localhost.*"), caps);

        // start session
        WebDriver driver = WebDriverManager.getWebDriver(request);
        SessionContext sessionContext = WebDriverSessionsManager.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = sessionContext.getCapabilities().get();

        Assert.assertEquals(sessionCapabilities.get("tap:projectId"), caps.getCapability("tap:projectId"), "EndPoint Capability is set");
    }

}
