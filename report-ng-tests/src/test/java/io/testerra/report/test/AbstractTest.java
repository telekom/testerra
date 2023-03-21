package io.testerra.report.test;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import org.testng.annotations.BeforeSuite;

public abstract class AbstractTest extends TesterraTest {

    @BeforeSuite(alwaysRun = true)
    public void configureChromeOptions() {
        WebDriverManagerProvider.WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chromeHeadless, (ChromeConfig) options -> options.addArguments("--disable-dev-shm-usage"));
    }
}
