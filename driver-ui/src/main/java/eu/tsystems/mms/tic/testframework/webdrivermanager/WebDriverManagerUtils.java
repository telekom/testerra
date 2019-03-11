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
/* 
 * Created on 28.03.2013
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.model.HostInfo;
import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.report.model.BrowserInformation;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class containing utility methods for WebDriverManager. To keep the WebDriverManager Class cleaner.
 *
 * @author sepr
 */
public final class WebDriverManagerUtils {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverManagerUtils.class);

    // url pattern for node proxy requests
    private static final String urlPattern = "http://<ip>:<port>/grid/api/<request>";

    /**
     * Hide constructor.
     */
    private WebDriverManagerUtils() {
    }

    /**
     * Computes the baseUrl to use from the possible sources.
     *
     * @param presetBaseURL                        A manually set baseUrl
     * @return BaseUrl to use.
     */
    protected static String getBaseUrl(final String presetBaseURL) {
        String baseUrl;
        /*
         * preset baseurl
         */
        if (presetBaseURL != null) {
            baseUrl = presetBaseURL;
        }
        /*
         * baseURL defined by System property or config file
         */
        else {
            baseUrl = PropertyManager.getProperty(FennecProperties.BASEURL, "");
        }
        return baseUrl;
    }

    /**
     * Set Browser type + version in TestRunConfiguration and log infos.
     *
     * @param driver WebDriver or Selenium to get info from.
     */
    protected static void logUserAgent(final String sessionKey, final WebDriver driver,
            final HostInfo hostInfo) {

        String browserInfo = pLogUserAgent(driver);
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        ClassContext classContext = methodContext.classContext;
        String context = classContext.name + "." + methodContext.name + " : " + sessionKey + " on " + hostInfo;
        BrowserInformation.setBrowserInfoWithContext(browserInfo, context);
    }

    public static void logUserAgent(WebDriver driver) {
        pLogUserAgent(driver);
    }

    private static String pLogUserAgent(WebDriver driver) {
        String browserInfo;
        String browserName = "Browser unknown";
        String browserVersion = " unknown";
        final String msg = "Error logging user agent";
        try {
            final BrowserInformation browserInformation = getBrowserInformation(driver);
            browserName = browserInformation.getBrowserName();
            browserVersion = browserInformation.getBrowserVersion();
        } catch (final Exception we) {
            LOGGER.error(msg, we);
        }

        browserInfo = browserName + " - v" + browserVersion;

        LOGGER.info("Browser: " + browserInfo);
        return browserInfo;
    }

    private static final Map<WebDriver, BrowserInformation> CACHED_BROWSER_INFOS = new ConcurrentHashMap<>();

    public static BrowserInformation getBrowserInformation(final WebDriver driver) {
        if (driver == null) {
            return null;
        }

        if (CACHED_BROWSER_INFOS.containsKey(driver)) {
            return CACHED_BROWSER_INFOS.get(driver);
        }

        BrowserInformation browserInformation;
        WebDriver realDriver = driver;
        if (EventFiringWebDriver.class.isAssignableFrom(driver.getClass())) {
            realDriver = ((EventFiringWebDriver) driver).getWrappedDriver();
        }

        if (ProvidesBrowserInformation.class.isAssignableFrom(realDriver.getClass())) {
            browserInformation = ((ProvidesBrowserInformation) realDriver).getBrowserInformation();
        }
        else if (JavascriptExecutor.class.isAssignableFrom(realDriver.getClass())) {
            String userAgentString = "unknown";
            try {
                userAgentString = (String) ((JavascriptExecutor) realDriver).executeScript("return navigator.userAgent;");
            } catch (Exception e) {
                LOGGER.error("Error requesting user agent", e);

            }
            browserInformation = new BrowserInformation(userAgentString);
        }
        else {
            browserInformation = new BrowserInformation(null);
        }

        CACHED_BROWSER_INFOS.put(driver, browserInformation);
        return browserInformation;
    }

    static void removeCachedBrowserInformation(WebDriver eventFiringWebDriver) {
        if (CACHED_BROWSER_INFOS.containsKey(eventFiringWebDriver)) {
            CACHED_BROWSER_INFOS.remove(eventFiringWebDriver);
        }
    }

    @Deprecated
    public static HostInfo getExecutingSeleniumHost(WebDriver driver) {
        if (driver instanceof EventFiringWebDriver) {
            driver = ((EventFiringWebDriver) driver).getWrappedDriver();
        }

        if (driver instanceof RemoteWebDriver) {
            return getExecutingRemoteWebDriverNode((RemoteWebDriver) driver);
        } else {
            // local mode without supervisor
            return null;
        }
    }

    @Deprecated
    public static NodeInfo getExecutingRemoteWebDriverNode(WebDriver driver) {
        return new NodeInfo("grid", 3);
        //        if (driver instanceof EventFiringWebDriver) {
//            driver = ((EventFiringWebDriver) driver).getWrappedDriver();
//        }
//
//        if (driver instanceof RemoteWebDriver) {
//            return getExecutingRemoteWebDriverNode((RemoteWebDriver) driver);
//        } else {
//            // local mode without supervisor
//            return null;
//        }
    }

    /**
     * Get information about grid nodes and log them.
     *
     * @param newDriver Driver to get Node details from.
     * @return host name and port
     */
    private static NodeInfo getExecutingRemoteWebDriverNode(final RemoteWebDriver newDriver) {

        // get selenium grid hub
        final CommandExecutor commandExecutor = newDriver.getCommandExecutor();
        final URL url;
        if (commandExecutor instanceof HttpCommandExecutor) {
            url = ((HttpCommandExecutor) commandExecutor).getAddressOfRemoteServer();
        }
        else {
            return new NodeInfo("local_mode", 0);
        }
        String host = url.getHost();
        int port = url.getPort();
        NodeInfo nodeInfo;
        String proxyID = null;
        JSONObject answer;
        Integer mtsPort;
        Integer vncPort;

        try {
            answer = requestSeleniumServerConfig(host, String.valueOf(port), "testsession?session=" + newDriver.getSessionId());
        } catch (Exception e) {
            LOGGER.warn("Getting proxyID failed. Probably in StandaloneMode. Using fallback (host) to detect node info.");
            nodeInfo = new NodeInfo(host, port);
            nodeInfo.setMTSPort(port - 1000);
            LOGGER.info("Using Grid Node: " + nodeInfo.toString());
            return nodeInfo;
        }

        try {
            // get config of node (that runs the current session) from hub
            proxyID = answer.getString("proxyId");
            answer = requestSeleniumServerConfig(host, String.valueOf(port), "proxy?id=" + proxyID);
            JSONObject answerPart = answer.getJSONObject("request");

            // try to read NodeInfo from NodeConfiguration
            JSONObject nodeConfig = answerPart.getJSONObject("configuration");

            // first try is to check for remote host param
            try {
                URL hostURL = new URL(nodeConfig.getString("remoteHost"));
                host = hostURL.getHost();
                port = hostURL.getPort();
            } catch (final JSONException | MalformedURLException me) {

                // second try is to check for host param
                try {
                    host = nodeConfig.getString("host");
                } catch (final JSONException je1) {
                    // if neither remote host param nor host param are found, localhost is assumed
                    host = "localhost";
                }

                // but still a node port might be set
                try {
                    port = nodeConfig.getInt("nodePort");
                } catch (final JSONException je1) {
                    port = 5555;
                }

            } finally { // check for mts and vnc ports
                try {
                    mtsPort = nodeConfig.getInt("mtsPort");
                } catch (final JSONException je) {
                    mtsPort = port - 1000;
                }
                try {
                    vncPort = nodeConfig.getInt("vncPort");
                } catch (final JSONException je) {
                    vncPort = 0;
                }
            }

            nodeInfo = new NodeInfo(host, port, vncPort, mtsPort);
            LOGGER.info("Using Grid Node: " + nodeInfo.toString());
            return nodeInfo;
        } catch (final UniformInterfaceException ue) {  // if POSt for session fails, standalone is assumed
            LOGGER.warn("Getting proxyID failed. Probably in StandaloneMode. Using fallback (host) to detect node info.");
            nodeInfo = new NodeInfo(host, port);
            nodeInfo.setMTSPort(port - 1000);
            LOGGER.info("Using Grid Node: " + nodeInfo.toString());
            return nodeInfo;
        } catch (final JSONException je) { // if no NodeConfiguration is available, try to parse the NodeInfo from the proxyID
            LOGGER.warn("Getting node info from config failed. Probably node has been started improperly. Using fallback (proxyID) to detect node info.");
            final URL nodeURL;
            try {
                nodeURL = new URL(proxyID);
            } catch (MalformedURLException e) {
                throw new FennecRuntimeException("IP and nodePort not readable from proxyID.", e);
            }
            nodeInfo = new NodeInfo(nodeURL.getHost(), nodeURL.getPort());
            nodeInfo.setMTSPort(port - 1000);
            LOGGER.info("Using Grid Node: " + nodeInfo.toString());
            return nodeInfo;
        } catch (final Exception e) {
            LOGGER.error("Unexpected Error while getting webdriver node details", e);
            throw new FennecRuntimeException("Error while retrieving node info: " + e.getMessage(), e);
        }
    }

    private static JSONObject requestSeleniumServerConfig(String ip, String port, String request) throws UniformInterfaceException {
        String url = urlPattern.replace("<ip>", ip).replace("<port>", port).replace("<request>", request);
        WebResource resource = Client.create().resource(url);

        LOGGER.debug("Sending request: " + resource.toString());

        String response = resource.post(String.class);
        final JSONObject answer = new JSONObject(response);

        LOGGER.debug("Answer received: " + answer.toString());

        return answer;
    }

    /**
     * Quit WebDriver Session.
     *
     * @param driver .
     */
    protected static void quitWebDriverSession(final WebDriver driver) {
        try {
            if (driver == null) {
                LOGGER.info("No WebDriver found. Maybe it has already been closed.");
            } else {
                driver.quit();
                removeCachedBrowserInformation(driver);
            }
        } catch (final Throwable e) {
            LOGGER.info("WebDriver could not be quit. May someone did before.", e);
        }
    }

    /**
     * deletes all cookies of the given Webdriver
     *
     * @param driver Webdriver
     */
    public static void deleteAllCookies(final WebDriver driver) {
        driver.manage().deleteAllCookies();
    }

    public static void addBrowserVersionToCapabilities(final DesiredCapabilities capabilities, final String version) {
        capabilities.setCapability(CapabilityType.VERSION, version);
        capabilities.setCapability(CapabilityType.BROWSER_VERSION, version);
    }

    /**
     * Add proxy settings to capabilities. proxyString may be "proxyhost:8080".
     *
     * @param capabilities  .
     * @param proxyString   .
     * @param noProxyString .
     */
    @Deprecated
    public static void addProxyToCapabilities(final DesiredCapabilities capabilities, final String proxyString, final String noProxyString) {
        addProxyToCapabilities(capabilities, proxyString);
    }

    public static void addProxyToCapabilities(final DesiredCapabilities capabilities, final String proxyString) {
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyString);
        proxy.setFtpProxy(proxyString);
        proxy.setSslProxy(proxyString);
//        proxy.setSocksProxy(proxyString);

//        JSONArray a = new JSONArray();
//        a.put(noProxyString);
//        proxy.setNoProxy(a + "");

        capabilities.setCapability(CapabilityType.PROXY, proxy);
    }

    /**
     * Generate DesiredCapabilities.
     *
     * @return DesiredCapabilities.
     */
    public static DesiredCapabilities generateNewDesiredCapabilities() {
        DesiredCapabilities cap = new DesiredCapabilities();
        return cap;
    }

    public static String getSessionId(WebDriver driver) {
        return WebDriverSessionsManager.getSessionKey(driver);
    }

}


