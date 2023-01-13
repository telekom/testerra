package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v106.emulation.Emulation;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.annotations.Test;

import java.util.Optional;

/**
 * Created on 2023-01-13
 *
 * @author mgn
 */
public class ChromeDevToolsTests extends AbstractWebDriverTest {

    /**
     * See here for examples:
     * https://www.selenium.dev/documentation/webdriver/bidirectional/chrome_devtools/
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
        WebDriver webDriver = this.getWebDriver();
        webDriver = new Augmenter().augment(webDriver);

        DevTools devTools = ((HasDevTools) webDriver).getDevTools();
        devTools.createSession();

        devTools.send(Emulation.setGeolocationOverride(Optional.of(52.5043),
                Optional.of(13.4501),
                Optional.of(1)));

        webDriver.get("https://my-location.org/");
    }
}
