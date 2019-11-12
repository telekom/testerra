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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.bmp;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.http.BrowserMobHttpClient;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pele
 * Date: 01.07.13
 * Time: 08:54
 * To change this template use File | Settings | File Templates.
 */
//@SuppressWarnings("all")
public class ProxyServer {

    static {
        checkTmpFiles();
    }

    /*
    Tests MAY BE veeeeeeeeeery slow. If so, try this:

    In your temp dir put 2 files:

    userAgentString.properties
    with content:
            lastUpdateCheck=200000000000000
            currentVersion=1

     and

    userAgentString.txt
    with 0 content.
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServer.class);

    private net.lightbody.bmp.proxy.ProxyServer bmpProxyServer;
    private final int internalPort;
    private final HttpHost proxyHost;
    private final UsernamePasswordCredentials credentials;

    protected HashMap<String, Object> sessionCapabilities;

    public ProxyServer(int internalPort, HttpHost proxyHost, UsernamePasswordCredentials credentials) {
        this.internalPort = internalPort;
        this.proxyHost = proxyHost;
        this.credentials = credentials;

        startProxyServer();
    }

    public static void checkTmpFiles() {
        String tmpdir = System.getProperty("java.io.tmpdir");

        String file1Content = "lastUpdateCheck=200000000000000\r\n" +
                "currentVersion=1";

        if (tmpdir != null) {
            try {
                File file1 = new File(tmpdir, "userAgentString.properties");
                FileWriter fw1 = new FileWriter(file1);
                fw1.write(file1Content);
                fw1.close();

                File file2 = new File(tmpdir, "userAgentString.txt");
                FileWriter fw2 = new FileWriter(file2);
                fw2.write("");
                fw2.close();
            } catch (IOException e) {
                throw new TesterraSystemException("Error creating browsermob proxy setup files", e);
            }
        }
    }

    public void startCapture() {
        bmpProxyServer.setCaptureContent(true);
        bmpProxyServer.setCaptureHeaders(true);
        bmpProxyServer.newHar("testerra.captured.har");
    }

    public Har stopCapture() {
        bmpProxyServer.setCaptureContent(false);
        bmpProxyServer.setCaptureHeaders(false);
        return bmpProxyServer.getHar();
    }

    /**
     * @return The created proxy server.
     */
    private net.lightbody.bmp.proxy.ProxyServer startProxyServer() {
        LOGGER.info("Starting BMP server on " + internalPort + "...");

        // Set up the internal proxy server and start it, because otherwise the
        // http client is not started and we end up with a NP.
        bmpProxyServer = new net.lightbody.bmp.proxy.ProxyServer(internalPort);

        final CloseableHttpClient httpClient;

        Map<String, String> options = new HashMap<String, String>();
        if (proxyHost != null) {
            final String proxyHostName = proxyHost.getHostName();
            final int proxyPort = proxyHost.getPort();
            LOGGER.info("Setting proxy for this bmp session: " + proxyHostName + ":" + proxyPort);
            options.put("httpProxy", proxyHostName + ":" + proxyPort);
        }

        try {
            bmpProxyServer.start();
            //            httpClient = getHttpClient(bmpProxyServer);
        } catch (Exception e) {
            throw new TesterraRuntimeException(e);
        }

        bmpProxyServer.setOptions(options);

        //        if (proxyHost != null) {
        //            final String proxyHost = this.proxyHost.getHostName();
        //            final int proxyPort = this.proxyHost.getPort();
        //            LOGGER.info("Setting proxy for this bmp session: " + proxyHost + ":" + proxyPort);
        //
        ////            AuthScope authScope = new AuthScope(proxyHost, proxyPort);
        ////            if (credentials != null) {
        ////                httpClient.getCredentialsProvider().setCredentials(authScope, credentials);
        ////            }
        //
        //            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
        //
        //        }

        LOGGER.info("BMP server started on " + internalPort);
        return bmpProxyServer;
    }

    private CloseableHttpClient getHttpClient(net.lightbody.bmp.proxy.ProxyServer server) throws NoSuchFieldException,
            IllegalAccessException {
        // The httpclient has to be manipulated in order to be configured
        // to use the proxy is private, so we have to rely on reflection.
        Field clientField = net.lightbody.bmp.proxy.ProxyServer.class.getDeclaredField("client");
        setFieldAccessible(clientField);
        BrowserMobHttpClient client = (BrowserMobHttpClient) clientField.get(server);
        Field httpClientfield = BrowserMobHttpClient.class.getDeclaredField("httpClient");
        setFieldAccessible(httpClientfield);
        CloseableHttpClient httpClient = (CloseableHttpClient) httpClientfield
                .get(client);
        return httpClient;
    }

    private void setFieldAccessible(Field clientField) {
        if (!clientField.isAccessible()) {
            clientField.setAccessible(true);
        }
    }

    public void stopProxy() {
        if (bmpProxyServer == null) {
            LOGGER.info("No proxy server to stop.");
        }
        LOGGER.info("Stopping BMP server on port " + bmpProxyServer.getPort() + "...");
        try {
            bmpProxyServer.stop();
            LOGGER.info("BMP server on port " + bmpProxyServer.getPort() + " stopped");
        } catch (Exception e) {
            throw new TesterraRuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        checkTmpFiles();
    }

    public ProxyServer setHeader(String name, String value) {
        bmpProxyServer.addHeader(name, value);
        return this;
    }

    public int getPort() {
        return bmpProxyServer.getPort();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public net.lightbody.bmp.proxy.ProxyServer getBmpProxyServer() {
        return bmpProxyServer;
    }
}
