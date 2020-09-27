package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import eu.tsystems.mms.tic.testframework.webdriver.IWebDriverFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverFactory;

public class ConfigureDriverUiDesktop extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<IWebDriverFactory> uriBinder = Multibinder.newSetBinder(binder(), IWebDriverFactory.class);
        uriBinder.addBinding().to(DesktopWebDriverFactory.class).in(Scopes.SINGLETON);
    }
}
