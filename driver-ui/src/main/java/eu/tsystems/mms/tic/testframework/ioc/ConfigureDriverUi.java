package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultPageOverrides;
import eu.tsystems.mms.tic.testframework.pageobjects.PageOverrides;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultGuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultGuiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.DefaultGuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.PageObjectFactory;

public class ConfigureDriverUi extends AbstractModule {
    protected void configure() {
        bind(GuiElementAssertFactory.class).to(DefaultGuiElementAssertFactory.class).in(Scopes.SINGLETON);
        bind(GuiElementWaitFactory.class).to(DefaultGuiElementWaitFactory.class).in(Scopes.SINGLETON);
        bind(GuiElementFactory.class).to(DefaultGuiElementFactory.class).in(Scopes.SINGLETON);
        bind(PageObjectFactory.class).to(DefaultPageFactory.class).in(Scopes.SINGLETON);
        bind(PageOverrides.class).to(DefaultPageOverrides.class).in(Scopes.SINGLETON);
    }
}
