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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.ProxyUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.net.URL;
import org.openqa.selenium.Proxy;

public class WebDriverProxyUtils {

    public WebDriverProxyUtils() {

    }

    static void updateSessionContextRelations(SessionContext sessionContext) {
        /*
        assign usage in current method
        (this is useful for sessions that are shared between method contexts)
         */
        ExecutionContextController.setCurrentSessionContext(sessionContext);
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            if (!methodContext.sessionContexts.contains(sessionContext)) {
                methodContext.sessionContexts.add(sessionContext);

                TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(methodContext));
            }
        }
    }

    /**
     * Will return proxy String for Selenium WebDriver commands like {host}:{port}
     *
     * @param proxyUrl {@link URL} proxy Url
     * @return String - The proxy URL
     */
    private String toProxyString(final URL proxyUrl) {

        String proxyString = null;

        if (proxyUrl != null && StringUtils.isNotBlank(proxyUrl.getHost())) {

            proxyString = proxyUrl.getHost();

            if (proxyUrl.getPort() > -1) {
                proxyString += ":" + proxyUrl.getPort();
            }
        }

        return proxyString;
    }

    /**
     * @return Proxy based on an URL including socks proxy settings
     */
    public Proxy createSocksProxyFromUrl(URL url) {
        Proxy proxy = createHttpProxyFromUrl(url);
        proxy.setSocksProxy(proxy.getHttpProxy());
        String userInfo = url.getUserInfo();
        if (userInfo != null && userInfo.length() > 0) {
            String[] userInfoParts = userInfo.split(":", 2);
            if (userInfoParts.length > 0) {
                proxy.setSocksUsername(userInfoParts[0]);
            }
            if (userInfoParts.length > 1) {
                proxy.setSocksPassword(userInfoParts[1]);
            }
        }
        return proxy;
    }

    /**
     * @return Proxy based on an URL without socks proxy settings
     */
    public Proxy createHttpProxyFromUrl(URL url) {
        String proxyString = toProxyString(url);
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyString);
        proxy.setFtpProxy(proxyString);
        proxy.setSslProxy(proxyString);
        return proxy;
    }

    /**
     * @return Proxy settings including socks proxy based on {@link ProxyUtils#getSystemHttpsProxyUrl()}
     */
    public Proxy getDefaultSocksProxy() {
        Proxy proxy = createSocksProxyFromUrl(ProxyUtils.getSystemHttpsProxyUrl());
        proxy.setNoProxy(PropertyManager.getProperty("https.nonProxyHosts"));
        return proxy;
    }

    /**
     * @return Proxy settings without socks proxy based on {@link ProxyUtils#getSystemHttpsProxyUrl()}.
     */
    public Proxy getDefaultHttpProxy() {
        Proxy proxy = createHttpProxyFromUrl(ProxyUtils.getSystemHttpsProxyUrl());
        proxy.setNoProxy(PropertyManager.getProperty("https.nonProxyHosts"));
        return proxy;
    }
}
