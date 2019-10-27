package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultGuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultGuiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultGuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.IPageFactory;

public class TesterraDriverUiModule extends AbstractModule {

    protected void configure() {
        bind(GuiElementAssertFactory.class).to(DefaultGuiElementAssertFactory.class).in(Scopes.SINGLETON);
        bind(GuiElementWaitFactory.class).to(DefaultGuiElementWaitFactory.class).in(Scopes.SINGLETON);
        bind(GuiElementFactory.class).to(DefaultGuiElementFactory.class).in(Scopes.SINGLETON);
        bind(IPageFactory.class).to(DefaultPageFactory.class).in(Scopes.SINGLETON);
    }
}
