package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DesktopGuiElementCoreFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementCoreFactory;

public class TesterraDriverUiDesktopModule extends AbstractModule {

    protected void configure() {
        bind(GuiElementCoreFactory.class).to(DesktopGuiElementCoreFactory.class).in(Scopes.SINGLETON);
    }
}
