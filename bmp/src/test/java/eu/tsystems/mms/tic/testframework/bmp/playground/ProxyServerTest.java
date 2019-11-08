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
package eu.tsystems.mms.tic.testframework.bmp.playground;

import eu.tsystems.mms.tic.testframework.bmp.AbstractTest;
import eu.tsystems.mms.tic.testframework.bmp.ProxyServer;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarLog;
import org.apache.http.HttpHost;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Created by pele on 20.10.2014.
 */
public class ProxyServerTest extends AbstractTest {

    @BeforeSuite
    public void setupFFProfile() {
        WebDriverManager.config().webDriverMode = WebDriverMode.remote;

        DesiredCapabilities desiredCapabilities = WebDriverManagerUtils.generateNewDesiredCapabilities();
        WebDriverManagerUtils.addProxyToCapabilities(desiredCapabilities, "localhost:9999");
        WebDriverManager.setGlobalExtraCapabilities(desiredCapabilities);

        WebDriverManager.setBaseURL("https://www.google.de");
    }

    @Test
    public void testProxyServer() {

        HttpHost proxyHost = new HttpHost("localhost", 8080);
        ProxyServer proxyServer = new ProxyServer(9999, proxyHost, null);

        try {
            // start capture
            proxyServer.startCapture();

            // open ff
            WebDriverManager.getWebDriver();

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
