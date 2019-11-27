package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.TimerWrapper;
import org.openqa.selenium.WebDriver;

public interface IWebDriverManager {
    TimerWrapper getTimerWrapper(WebDriver webDriver);
}
