package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.pageobjects.WebDriverRetainer;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TimerWrapper;
import org.openqa.selenium.WebDriver;

/**
 * @todo Rename to {@link WebDriverManager}
 */
public interface IWebDriverManager extends WebDriverRetainer {
    TimerWrapper getTimerWrapper(WebDriver webDriver);
}
