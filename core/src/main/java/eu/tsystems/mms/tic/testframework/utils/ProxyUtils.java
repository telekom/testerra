/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ProxyUtils implements PropertyManagerProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyUtils.class);

    /**
     * Will return proxy url like http://{http.proxyUser}:{http.proxyPassword}@{http.proxyHost}:{http.proxyPort}
     * based on the System's proxy settings ea. from the proxysettings.properties file
     *
     * @return null If there is no URL configured
     */
    public static URL getSystemHttpProxyUrl() {
        return getSpecificProxyTypeUrlWithPrefix("http", "http");
    }

    /**
     * Will return proxy url like http://{https.proxyUser}:{https.proxyPassword}@{https.proxyHost}:{https.proxyPort}
     * based on the System's proxy settings ea. from the proxysettings.properties file
     *
     * @return null If there is no URL configured
     */
    public static URL getSystemHttpsProxyUrl() {
        return getSpecificProxyTypeUrlWithPrefix("https", "http");
    }

    private static String getSpecificProxyTypeString(final String proxyType) {

        String proxyString = "";

        final String proxyHost = PROPERTY_MANAGER.getProperty(proxyType + ".proxyHost");
        if (StringUtils.isNotBlank(proxyHost)) {
            proxyString += proxyHost;
        } else {
            return null;
        }

        final String proxyPort = PROPERTY_MANAGER.getProperty(proxyType + ".proxyPort");
        if (StringUtils.isNotBlank(proxyHost)) {
            proxyString += ":" + proxyPort;
        }

        return proxyString;
    }

    /**
     * ProxyType can be "http" or "https" according to java env properties proxy.httpHost or proxy.httpsHost
     *
     * @param proxyType {@link String} http or https
     * @param prefix {@link String} Prefix for URL Scheme
     * @return URL
     */
    private static URL getSpecificProxyTypeUrlWithPrefix(final String proxyType, final String prefix) {

        final String urlEncoding = "UTF-8";
        String proxyUrlString = prefix + "://";

        try {

            final String user = PROPERTY_MANAGER.getProperty(proxyType + ".proxyUser");
            if (StringUtils.isNotBlank(user)) {
                proxyUrlString += URLEncoder.encode(user, urlEncoding);
            }

            final String password = PROPERTY_MANAGER.getProperty(proxyType + ".proxyPassword");
            if (StringUtils.isNotBlank(password)) {
                proxyUrlString += ":" + URLEncoder.encode(password, urlEncoding);
            }

            if (StringUtils.isNotBlank(user)) {
                proxyUrlString += "@";
            }

            final String specificProxyTypeString = getSpecificProxyTypeString(proxyType);
            if (specificProxyTypeString == null) {
                return null;
            }

            proxyUrlString += specificProxyTypeString;

            return new URL(proxyUrlString);
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            LOGGER.error("Error receiving Proxy URL", e);
        }

        return null;
    }
}
