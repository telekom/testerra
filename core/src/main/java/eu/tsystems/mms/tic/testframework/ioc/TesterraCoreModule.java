package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import eu.tsystems.mms.tic.testframework.execution.testng.FunctionalAssertFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultFunctionalAssertFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.INonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultNonFunctionalAssert;

public class TesterraCoreModule extends AbstractModule {

    protected void configure() {
        bind(FunctionalAssertFactory.class).to(DefaultFunctionalAssertFactory.class);
        bind(INonFunctionalAssert.class).to(DefaultNonFunctionalAssert.class);
    }
}
