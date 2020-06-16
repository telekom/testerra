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
import eu.tsystems.mms.tic.testframework.bmp.BMProxyManager;
import eu.tsystems.mms.tic.testframework.bmp.ProxyServer;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import net.lightbody.bmp.core.har.Har;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.testng.annotations.Test;

public class ProxyServerPlaygroundTest extends AbstractTest {

    static final String PROXY_HOST = PropertyManager.getProperty("http.proxyHost");
    static final int PROXY_PORT = PropertyManager.getIntProperty("http.proxyPort");

    @Test
    public void testProxyServer() {
        HttpHost proxyHost = new HttpHost(PROXY_HOST, PROXY_PORT);
        UsernamePasswordCredentials credentials = null;
        ProxyServer proxyServer = new ProxyServer(9999, proxyHost, credentials);

        proxyServer.startCapture();
        //...
        Har har = proxyServer.stopCapture();
    }

    @Test
    public void testMultipleInstances() throws Exception {

        HttpHost proxyHost = new HttpHost(PROXY_HOST, PROXY_PORT);
        UsernamePasswordCredentials credentials = null;
        ProxyServer proxyServer1 = new ProxyServer(9991, proxyHost, credentials);
        ProxyServer proxyServer2 = new ProxyServer(9992, proxyHost, credentials);
        ProxyServer proxyServer3 = new ProxyServer(9993, proxyHost, credentials);

        TimerUtils.sleep(2000);

        proxyServer1.stopProxy();
        proxyServer2.stopProxy();
        proxyServer3.stopProxy();
    }

    @Test
    public void testBMPManger() throws Exception {
        final ProxyServer proxyServer = BMProxyManager.getProxyServer();

        TimerUtils.sleep(3000);

        BMProxyManager.shutDownProxyServer();
    }
}
