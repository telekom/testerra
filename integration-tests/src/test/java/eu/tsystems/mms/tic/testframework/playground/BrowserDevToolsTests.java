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
package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.testing.BrowserDevToolsProvider;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v112.emulation.Emulation;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * Created on 2023-01-13
 *
 * @author mgn
 */
public class BrowserDevToolsTests extends AbstractWebDriverTest implements BrowserDevToolsProvider {

    /**
     * See here for examples:
     * https://www.selenium.dev/documentation/webdriver/bidirectional/bidi_api_remotewebdriver/
     * https://www.selenium.dev/documentation/webdriver/bidirectional/chrome_devtools/
     * <p>
     * <p>
     * Important for local selenium server:
     * - Use --host parameter, otherwise it could return a wrong IP address from another network interface (e.g. with active VPN)
     * <p>
     * Example
     * - java -Dwebdriver.chrome.driver=driver\chromedriver.exe  -jar selenium4\selenium-server-4.7.2.jar standalone --port 4444 --host localhost
     * <p>
     * <p>
     * Known issues:
     * - Issue with Selenoid: https://github.com/aerokube/selenoid/issues/1063
     */

    private Optional<Number> latitude = Optional.of(52.52084);
    private Optional<Number> longitude = Optional.of(13.40943);

    @Test
    public void testT01_GeoLocation_localDriver() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        ChromeDriver chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, ChromeDriver.class).get();

        DevTools devTools = chromeDriver.getDevTools();
        devTools.createSession();
        devTools.send(Emulation.setGeolocationOverride(
                latitude,
                longitude,
                Optional.of(1)));
        webDriver.get("https://my-location.org/");
        uiElementFinder.find(By.id("latitude")).assertThat().text().isContaining(latitude.get().toString());
        uiElementFinder.find(By.id("longitude")).assertThat().text().isContaining(longitude.get().toString());

    }

    @Test
    public void testT02_GeoLocation_remoteDriver() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        RemoteWebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();
        webDriver = new Augmenter().augment(remoteWebDriver);

        DevTools devTools = ((HasDevTools) webDriver).getDevTools();
        devTools.createSession();

        devTools.send(Emulation.setGeolocationOverride(
                latitude,
                longitude,
                Optional.of(1)));

        webDriver.get("https://my-location.org/");
        uiElementFinder.find(By.id("latitude")).assertThat().text().isContaining(latitude.get().toString());
        uiElementFinder.find(By.id("longitude")).assertThat().text().isContaining(longitude.get().toString());
    }

    @Test
    public void testT03_GeoLocation_generic() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

//        DevTools devTools = BROWSER_DEV_TOOLS.getRawDevTools(webDriver);
//
//        devTools.send(Emulation.setGeolocationOverride(
//                latitude,
//                longitude,
//                Optional.of(1)));
        BROWSER_DEV_TOOLS.setGeoLocation(webDriver, latitude.get().doubleValue(), longitude.get().doubleValue(), 1);

        webDriver.get("https://my-location.org/");
        uiElementFinder.find(By.id("latitude")).assertThat().text().isContaining(latitude.get().toString());
        uiElementFinder.find(By.id("longitude")).assertThat().text().isContaining(longitude.get().toString());
    }

    @Test
    public void testT04_BasicAuth_remoteDriver() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
//        request.setBrowserVersion("106");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);

        WebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

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
        ((HasAuthentication) remoteWebDriver).register(UsernameAndPassword.of("admin", "admin"));

        webDriver.get("https://the-internet.herokuapp.com/basic_auth");
        uiElementFinder.find(By.tagName("p")).assertThat().text().isContaining("Congratulations");

    }

    @Test
    public void testT05_BasicAuth_DevTools() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
//        request.setBrowserVersion("106");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        BROWSER_DEV_TOOLS.setBasicAuthentication(webDriver, UsernameAndPassword.of("admin", "admin"));

        webDriver.get("https://the-internet.herokuapp.com/basic_auth");
        uiElementFinder.find(By.tagName("p")).assertThat().text().isContaining("Congratulations");

    }

    /**
     * The following example uses the BiDi implementation of Chrome to add basic authentication information
     *
     * Works only with local ChromeDriver, RemoteWebDriver is not supported
     */
    @Test
    public void testT06_BasicAuth_ChromeBiDiAPI() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
//        request.setBrowserVersion("106");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);
        Predicate<URI> uriPredicate = uri -> uri.getHost().contains("the-internet.herokuapp.com");

         ChromeDriver chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, ChromeDriver.class).get();
        ((HasAuthentication) chromeDriver).register(uriPredicate, UsernameAndPassword.of("admin", "admin"));

        webDriver.get("https://the-internet.herokuapp.com/basic_auth");
        uiElementFinder.find(By.tagName("p")).assertThat().text().isContaining("Congratulations");
    }

}
