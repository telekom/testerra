package io.testerra.test.browser;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testing.UiElementFinderFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Objects;

/**
 * Created on 2023-04-19
 *
 * @author mgn
 */
public class BrowserTests extends TesterraTest implements
        Loggable,
        WebDriverManagerProvider,
        UiElementFinderFactoryProvider {

    @Test
    public void testT01_ChromeExtensions() throws MalformedURLException {

        File chromeExtensionFile = new File(
                Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("testfiles/Simple_Translate_2.8.1.0.crx")).getFile());

        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chrome, (ChromeConfig) options -> {
            options.setAcceptInsecureCerts(true);
        });


        ChromeOptions options = new ChromeOptions();
        options.addExtensions(chromeExtensionFile);
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
//        request.setBaseUrl("chrome://extensions-internals/");
        request.getDesiredCapabilities().merge(options);

        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        webDriver.get("chrome://extensions-internals/");
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);
        UiElement chromeExtensionJson = uiElementFinder.find(By.xpath("//pre"));
        String content = chromeExtensionJson.waitFor().text().getActual();
        Assert.assertTrue(content.contains("Simple Translate"));

    }

}
