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
package eu.tsystems.mms.tic.testframework.testing;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.webdrivermanager.ChromeDevTools;
import org.openqa.selenium.Credentials;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v112.emulation.Emulation;
import org.openqa.selenium.devtools.v112.network.Network;
import org.openqa.selenium.devtools.v112.network.model.Headers;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created on 2023-06-19
 *
 * @author mgn
 */
public class SeleniumChromeDevTools implements ChromeDevTools, Loggable {

    /**
     * Create a Chrome DevTools session
     */
    @Override
    public DevTools getRawDevTools(WebDriver webDriver) {
        if (!isSupported(webDriver)) {
            throw new RuntimeException("The current browser does not support DevTools");
        }
        try {
            DevTools devTools = null;
            final String message = "Creating DevTools instance of ";
            // Check if current session is a local or remote session
            if (isRemoteDriver(webDriver)) {
                log().info("{}remote driver session.", message);
                RemoteWebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();
                webDriver = new Augmenter().augment(remoteWebDriver);
                devTools = ((HasDevTools) webDriver).getDevTools();
            } else {
                log().info("{}local driver session.", message);
                ChromeDriver chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, ChromeDriver.class).get();
                devTools = chromeDriver.getDevTools();
            }
            devTools.createSession();
            return devTools;
        } catch (Exception e) {
            throw new RuntimeException("Could not create DevTools", e);
        }
    }

    @Override
    public void setGeoLocation(WebDriver webDriver, double latitude, double longitude, int accuracy) {
        if (!isSupported(webDriver)) {
            throw new RuntimeException("The current browser does not support DevTools");
        }
        DevTools devTools = this.getRawDevTools(webDriver);
        devTools.send(Emulation.setGeolocationOverride(
                Optional.of(latitude),
                Optional.of(longitude),
                Optional.of(accuracy)));
        log().info("Changed geolocation information to lat={}, long={}", latitude, longitude);
    }

    @Override
    public void setBasicAuthentication(WebDriver webDriver, Supplier<Credentials> credentials) {
        if (!isSupported(webDriver)) {
            throw new RuntimeException("The current browser does not support DevTools");
        }
        DevTools devTools = this.getRawDevTools(webDriver);

        try {
            Map<String, Object> headers = new HashMap<>();
            byte[] authByteArray = "".getBytes();

            if (credentials.get() instanceof UsernameAndPassword) {
                UsernameAndPassword usernameAndPassword = (UsernameAndPassword) credentials.get();
                authByteArray = String.format("%s:%s", usernameAndPassword.username(), usernameAndPassword.password()).getBytes("UTF-8");
            } else {
                throw new RuntimeException("Unsupported type of Credentials");
            }

            headers.put("authorization", "Basic " + Base64.getEncoder().encodeToString(authByteArray));
            devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            devTools.send(Network.setExtraHTTPHeaders(new Headers(headers)));
            log().info("Set credentials for basic authentication");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot set basic authentication", e);
        }

    }


}
