package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.DesktopGuiElementCoreFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreFactory;

public class ConfigureDriverUiDesktop extends AbstractModule {
    @Override
    protected void configure() {
        bind(GuiElementCoreFactory.class).to(DesktopGuiElementCoreFactory.class).in(Scopes.SINGLETON);
    }
}
