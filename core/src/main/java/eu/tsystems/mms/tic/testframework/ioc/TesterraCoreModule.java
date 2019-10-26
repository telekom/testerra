package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollectorFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultAssertCollectorFactory;

public class TesterraCoreModule extends AbstractModule {

    protected void configure() {
        bind(AssertCollectorFactory.class).to(DefaultAssertCollectorFactory.class);
    }
}
