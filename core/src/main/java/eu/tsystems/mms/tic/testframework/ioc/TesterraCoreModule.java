package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultFunctionalAssert;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultFunctionalAssertFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultNonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.execution.testng.FunctionalAssert;
import eu.tsystems.mms.tic.testframework.execution.testng.FunctionalAssertFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.INonFunctionalAssert;

public class TesterraCoreModule extends AbstractModule {

    protected void configure() {
        bind(FunctionalAssertFactory.class).to(DefaultFunctionalAssertFactory.class).in(Scopes.SINGLETON);
        bind(INonFunctionalAssert.class).to(DefaultNonFunctionalAssert.class).in(Scopes.SINGLETON);
        bind(FunctionalAssert.class).to(DefaultFunctionalAssert.class).in(Scopes.SINGLETON);
    }
}
