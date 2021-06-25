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

import java.net.URLConnection;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.net.ssl.HttpsURLConnection;
import org.openqa.selenium.WebDriver;

public class DefaultConnectionConfigurator implements Consumer<URLConnection> {

    private final Map<String, String> headers = new HashMap<>();
    private CertUtils certUtils;
    private Duration timeout;

    public DefaultConnectionConfigurator() {
        this.setTimeout(Duration.ofSeconds(10));
    }

    public DefaultConnectionConfigurator imitateCookiesFrom(WebDriver webDriver) {
        this.headers.put("Cookie", WebDriverUtils.getCookieString(webDriver));
        return this;
    }

    public DefaultConnectionConfigurator useCertUtils(CertUtils certUtils) {
        this.certUtils = certUtils;
        return this;
    }

    public DefaultConnectionConfigurator setUserAgent(String userAgent) {
        this.headers.put("User-Agent", userAgent);
        return this;
    }

    public DefaultConnectionConfigurator setHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public DefaultConnectionConfigurator setTimeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public void accept(URLConnection urlConnection) {
        if (urlConnection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
            if (this.certUtils != null) {
                httpsURLConnection.setSSLSocketFactory(certUtils.createTrustingSslSocketFactory());
                httpsURLConnection.setHostnameVerifier(certUtils.getHostnameVerifier());
            }
        }

        if (this.timeout != null) {
            Long timeout = this.timeout.toMillis();
            urlConnection.setConnectTimeout(timeout.intValue());
            urlConnection.setReadTimeout(timeout.intValue());
        }

        this.headers.forEach(urlConnection::setRequestProperty);
    }
}
