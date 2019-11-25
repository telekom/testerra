package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.util.List;

public interface IWebDriverFactory {
    @Deprecated
    EventFiringWebDriver getWebDriver(WebDriverRequest r, SessionContext sessionContext);
    default EventFiringWebDriver createWebDriver(WebDriverRequest request, SessionContext sessionContext) {
        return getWebDriver(request, sessionContext);
    }

    List<String> getSupportedBrowsers();
    default boolean isBrowserSupported(String browser) {
        return getSupportedBrowsers().indexOf(browser) >= 0;
    }
    GuiElementCore createGuiElementAdapter(GuiElementData guiElementData);
}
