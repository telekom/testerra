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
package eu.tsystems.mms.tic.testframework.bmp.playground;

import eu.tsystems.mms.tic.testframework.bmp.AbstractTest;
import eu.tsystems.mms.tic.testframework.bmp.ProxyServer;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import java.net.MalformedURLException;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarLog;
import org.apache.http.HttpHost;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class ProxyServerTest extends AbstractTest {

    private DesktopWebDriverRequest request;

    @BeforeSuite
    public void setupFFProfile() throws MalformedURLException {
        this.request = new DesktopWebDriverRequest();
        this.request.setWebDriverMode(WebDriverMode.remote);
        this.request.setBaseUrl("https://www.google.de");

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        WebDriverManagerUtils.addProxyToCapabilities(desiredCapabilities, "localhost:9999");
        WebDriverManager.setGlobalExtraCapabilities(desiredCapabilities);
    }

    @Test
    public void testProxyServer() {

        HttpHost proxyHost = new HttpHost("localhost", 8080);
        ProxyServer proxyServer = new ProxyServer(9999, proxyHost, null);

        try {
            // start capture
            proxyServer.startCapture();

            // open ff
            WebDriverManager.getWebDriver(this.request);

            // stop capture
            Har har = proxyServer.stopCapture();
            HarLog log = har.getLog();
            Assert.assertNotNull(log, "Capture log is not null");
            Assert.assertNotNull(log.getEntries(), "Log Entries are not null");
            Assert.assertTrue(log.getEntries().size() > 0, "Log Entries size > 0");
        } finally {
            proxyServer.stopProxy();
        }
    }
}
