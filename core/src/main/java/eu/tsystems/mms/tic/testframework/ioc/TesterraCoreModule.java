package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultInstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultNonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.FunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;

public class TesterraCoreModule extends AbstractModule {

    protected void configure() {
        bind(NonFunctionalAssertion.class).to(DefaultNonFunctionalAssertion.class).in(Scopes.SINGLETON);
        bind(FunctionalAssertion.class).to(DefaultFunctionalAssertion.class).in(Scopes.SINGLETON);
        bind(InstantAssertion.class).to(DefaultInstantAssertion.class).in(Scopes.SINGLETON);
    }
}
