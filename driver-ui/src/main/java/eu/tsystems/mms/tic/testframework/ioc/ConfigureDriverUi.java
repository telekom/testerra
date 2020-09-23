package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import eu.tsystems.mms.tic.testframework.hooks.DriverUiHook;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultTestControllerOverrides;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultUiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.PageObjectFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultGuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.DefaultPropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssertFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.PropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.DefaultGuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWaitFactory;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DefaultWebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverManager;

public class ConfigureDriverUi extends AbstractModule {
    protected void configure() {
        bind(GuiElementAssertFactory.class).to(DefaultGuiElementAssertFactory.class).in(Scopes.SINGLETON);
        bind(GuiElementWaitFactory.class).to(DefaultGuiElementWaitFactory.class).in(Scopes.SINGLETON);
        bind(UiElementFactory.class).to(DefaultUiElementFactory.class).in(Scopes.SINGLETON);
        bind(PageObjectFactory.class).to(DefaultPageFactory.class).in(Scopes.SINGLETON);
        bind(TestController.Overrides.class).to(DefaultTestControllerOverrides.class).in(Scopes.SINGLETON);
        bind(PropertyAssertionFactory.class).to(DefaultPropertyAssertionFactory.class).in(Scopes.SINGLETON);
        bind(IWebDriverManager.class).to(DefaultWebDriverManager.class).in(Scopes.SINGLETON);

        Multibinder<ModuleHook> hookBinder = Multibinder.newSetBinder(binder(), ModuleHook.class);
        hookBinder.addBinding().to(DriverUiHook.class).in(Scopes.SINGLETON);
    }
}
