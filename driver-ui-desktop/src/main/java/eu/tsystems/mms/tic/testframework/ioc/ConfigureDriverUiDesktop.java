package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DesktopGuiElementCoreFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementCoreFactory;

public class ConfigureDriverUiDesktop extends ConfigureDriverUi {

    @Override
    protected void configureWebDriver() {
        bind(GuiElementCoreFactory.class).to(DesktopGuiElementCoreFactory.class).in(Scopes.SINGLETON);
        webDriverConfigured = true;
    }
}
