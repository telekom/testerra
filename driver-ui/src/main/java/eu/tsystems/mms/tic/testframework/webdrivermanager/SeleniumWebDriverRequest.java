/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems MMS GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;

import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class SeleniumWebDriverRequest extends AbstractWebDriverRequest implements Loggable, Serializable {

    private URL baseUrl = null;

    public SeleniumWebDriverRequest() {
        super();
        String browserSetting = IWebDriverManager.Properties.BROWSER_SETTING.asString();
        if (!StringUtils.isStringEmpty(browserSetting)) {
            String[] split = browserSetting.split(":");
            if (split.length > 0) this.setBrowser(split[0].trim());
            if (split.length > 1) this.setBrowserVersion(split[1].trim());
        }

        if (StringUtils.isStringEmpty(this.getBrowser())) {
            this.setBrowser(IWebDriverManager.Properties.BROWSER.asString());
        }
        if (StringUtils.isStringEmpty(this.getBrowserVersion())) {
            this.setBrowserVersion(IWebDriverManager.Properties.BROWSER_VERSION.asString());
        }

        String baseUrl = Testerra.Properties.BASEURL.asString();
        if (!baseUrl.isEmpty()) {
            try {
                this.setBaseUrl(baseUrl);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Unable to read base url from property " + Testerra.Properties.BASEURL, e);
            }
        }
    }

    public Optional<URL> getBaseUrl() {
        return Optional.ofNullable(baseUrl);
    }

    public SeleniumWebDriverRequest setBaseUrl(String baseUrl) throws MalformedURLException {
        this.baseUrl = new URL(baseUrl);
        return this;
    }

    public SeleniumWebDriverRequest setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }
}
