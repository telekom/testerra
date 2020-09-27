package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.pageobjects.ResponsivePageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory;

public class ConfigureIntegrationTests extends AbstractModule {
    protected void configure() {
        bind(PageFactory.class).to(ResponsivePageFactory.class).in(Scopes.SINGLETON);
    }
}
