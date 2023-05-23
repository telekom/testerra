package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v112.emulation.Emulation;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created on 2023-01-13
 *
 * @author mgn
 */
public class BrowserDevToolsTests extends AbstractWebDriverTest {

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
     *
     *
     * Known issues:
     * - Issue with Selenoid: https://github.com/aerokube/selenoid/issues/1063
     */

    @Test
    public void testT01_GeoLocation_localDriver() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);

        ChromeDriver chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, ChromeDriver.class).get();

        DevTools devTools = chromeDriver.getDevTools();
        devTools.createSession();
        devTools.send(Emulation.setGeolocationOverride(Optional.of(52.5043),
                Optional.of(13.4501),
                Optional.of(1)));
        webDriver.get("https://my-location.org/");
    }

    @Test
    public void testT02_GeoLocation_remoteDriver() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
//        request.setBrowser(Browsers.firefox);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        RemoteWebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();

        webDriver = new Augmenter().augment(remoteWebDriver);

        DevTools devTools = ((HasDevTools) webDriver).getDevTools();
        devTools.createSession();

        devTools.send(Emulation.setGeolocationOverride(Optional.of(52.5043),
                Optional.of(13.4501),
                Optional.of(1)));

        webDriver.get("https://my-location.org/");
    }

    @Test
    public void testT04_BasicAuth_remoteDriver() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        request.setBrowserVersion("106");
//        request.getDesiredCapabilities().setCapability("se:cdp", "http://");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        WebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();

        AtomicReference<DevTools> devToolsAtomicReference = new AtomicReference<>();

        remoteWebDriver = new Augmenter()
                .addDriverAugmentation("chrome",
                        HasAuthentication.class,
                        (caps, exec) -> (whenThisMatches, useTheseCredentials) -> {

                            devToolsAtomicReference.get()
                                    .createSessionIfThereIsNotOne();
                            devToolsAtomicReference.get().getDomains()
                                    .network()
                                    .addAuthHandler(whenThisMatches, useTheseCredentials);

                        })
                .augment(remoteWebDriver);

        DevTools devTools = ((HasDevTools) remoteWebDriver).getDevTools();
        devTools.createSession();
        devToolsAtomicReference.set(devTools);
        ((HasAuthentication) remoteWebDriver).register(UsernameAndPassword.of("admin", "admin"));

        remoteWebDriver.get("https://the-internet.herokuapp.com/basic_auth");
    }
}
