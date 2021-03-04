package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.ResponsivePageFactory;

public class DriverUi_IntegrationTests extends AbstractModule {
    protected void configure() {
        bind(PageFactory.class).to(ResponsivePageFactory.class).in(Scopes.SINGLETON);
    }
}
