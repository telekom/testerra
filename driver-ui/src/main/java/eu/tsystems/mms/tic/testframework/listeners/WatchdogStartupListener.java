package eu.tsystems.mms.tic.testframework.listeners;

import eu.tsystems.mms.tic.testframework.watchdog.WebDriverWatchDog;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import java.util.function.Consumer;
import org.openqa.selenium.WebDriver;

public class WatchdogStartupListener implements Consumer<WebDriver> {
    @Override
    public void accept(WebDriver webDriver) {
        WebDriverWatchDog.start();

        // unregister myself
        WebDriverSessionsManager.unregisterWebDriverAfterStartupHandler(this);
    }
}
