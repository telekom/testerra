package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.ExecutionUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

/**
 * Created on 2023-12-15
 *
 * @author mgn
 */
public class EventLoggingDriverListener implements WebDriverListener, Loggable {

    private static final ExecutionUtils executionUtils = Testerra.getInjector().getInstance(ExecutionUtils.class);

    @Override
    public void beforeGet(WebDriver driver, String url) {
        log().info("Open {} on {}", url, driver);
    }

    @Override
    public void beforeTo(WebDriver.Navigation navigation, String url) {
        log().info("Navigate to {}", url);
    }

    @Override
    public void beforeBack(WebDriver.Navigation navigation) {
        log().info("Back");
    }

    @Override
    public void beforeForward(WebDriver.Navigation navigation) {
        log().info("Forward");
    }

    @Override
    public void beforeRefresh(WebDriver.Navigation navigation) {
        log().info("Refresh");
    }

    // TODO: Wait for Selenium 4.18: https://github.com/SeleniumHQ/selenium/pull/13210
//    @Override
//    public void afterSwitchToWindow(String s, WebDriver webDriver) {
//        log().info(
//                String.format("Switched to window \"%s\" (%s)", executionUtils.getFailsafe(webDriver::getTitle).orElse("(na)"), executionUtils.getFailsafe(webDriver::getCurrentUrl).orElse("(na)")));
//    }

}
