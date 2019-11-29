package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.hooks.UIDriverHook;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverFactory;

public class ConfigureDriverUiDesktop extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<IWebDriverFactory> uriBinder = Multibinder.newSetBinder(binder(), IWebDriverFactory.class);
        uriBinder.addBinding().to(DesktopWebDriverFactory.class).in(Scopes.SINGLETON);

        Multibinder<ModuleHook> hookBinder = Multibinder.newSetBinder(binder(), ModuleHook.class);
        hookBinder.addBinding().to(UIDriverHook.class).in(Scopes.SINGLETON);
    }
}
