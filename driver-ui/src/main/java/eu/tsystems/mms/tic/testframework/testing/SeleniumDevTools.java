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
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.webdrivermanager.BrowserDevTools;
import org.openqa.selenium.Credentials;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v112.emulation.Emulation;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Created on 2023-06-19
 *
 * @author mgn
 */
public class SeleniumDevTools implements BrowserDevTools, Loggable {

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
    }

    @Override
    public void setBasicAuthentication(WebDriver webDriver, Supplier<Credentials> credentials) {
        if (!isSupported(webDriver)) {
            throw new RuntimeException("The current browser does not support DevTools");
        }
        if (isRemoteDriver(webDriver)) {
            WebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();
            AtomicReference<DevTools> devToolsAtomicReference = new AtomicReference<>();
            remoteWebDriver = new Augmenter()
                    .addDriverAugmentation(
                            "chrome",
                            HasAuthentication.class,
                            (caps, exec) -> (whenThisMatches, useTheseCredentials) -> {
                                devToolsAtomicReference.get().createSessionIfThereIsNotOne();
                                devToolsAtomicReference.get().getDomains()
                                        .network()
                                        .addAuthHandler(whenThisMatches, useTheseCredentials);
                            })
                    .augment(remoteWebDriver);
            DevTools devTools = ((HasDevTools) remoteWebDriver).getDevTools();
            devTools.createSession();
            devToolsAtomicReference.set(devTools);
            ((HasAuthentication) remoteWebDriver).register(credentials);
        } else {
            // TODO Implement me
        }



    }


}
