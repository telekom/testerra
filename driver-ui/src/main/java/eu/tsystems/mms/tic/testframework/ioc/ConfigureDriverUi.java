package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultGuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultGuiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultGuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.DefaultPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.GuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.IPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultPropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertionFactory;

public class ConfigureDriverUi extends AbstractModule {

    protected static boolean pageObjectsConfigured = false;
    protected static boolean webDriverConfigured = false;

    protected void configure() {
        if (!pageObjectsConfigured) configurePageObjects();
        if (!webDriverConfigured) configureWebDriver();
        bind(PropertyAssertionFactory.class).to(DefaultPropertyAssertionFactory.class).in(Scopes.SINGLETON);

    }

    protected void configurePageObjects() {
        bind(GuiElementAssertFactory.class).to(DefaultGuiElementAssertFactory.class).in(Scopes.SINGLETON);
        bind(GuiElementWaitFactory.class).to(DefaultGuiElementWaitFactory.class).in(Scopes.SINGLETON);
        bind(GuiElementFactory.class).to(DefaultGuiElementFactory.class).in(Scopes.SINGLETON);
        bind(IPageFactory.class).to(DefaultPageFactory.class).in(Scopes.SINGLETON);
        pageObjectsConfigured = true;
    }

    protected void configureWebDriver() {
        webDriverConfigured = true;
    }
}
