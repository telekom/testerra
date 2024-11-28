/*
 * Testerra
 *
 * (C) 2023, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import org.openqa.selenium.Credentials;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;

import java.util.List;

/**
 * Created on 2023-06-19
 *
 * @author mgn
 */
public interface ChromeDevTools extends BiDiTools {

    DevTools getRawDevTools(WebDriver webDriver);

    void setGeoLocation(WebDriver webDriver, double latitude, double longitude, int accuracy);

    void setDevice(WebDriver webDriver, Dimension dimension, int scaleFactor, boolean mobile);

    void setBasicAuthentication(WebDriver webDriver, Supplier<Credentials> credentials);

    default boolean isSupported(WebDriver driver) {
        Optional<String> requestedBrowser = WEB_DRIVER_MANAGER.getRequestedBrowser(driver);
        return Optional.ofNullable(requestedBrowser)
                .map(Optional::get)
                .map(browser -> browser.toLowerCase().contains(Browsers.chrome))
                .orElse(false);
    }

    default boolean isRemoteDriver(WebDriver webDriver) {
        WebDriverRequest webDriverRequest = WEB_DRIVER_MANAGER.getSessionContext(webDriver).get().getWebDriverRequest();
        return webDriverRequest.getServerUrl().isPresent();
    default List<String> getSupportedBrowsers() {
        return List.of(Browsers.chrome, Browsers.chromeHeadless);
    }

}
