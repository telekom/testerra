package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultInstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultNonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.FunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.FunctionalTestAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;

public class TesterraIntegrationTestsModule extends TesterraCoreModule {
    @Override
    protected void configureAssertions() {
        bind(FunctionalAssertion.class).to(FunctionalTestAssertion.class).in(Scopes.SINGLETON);
        bind(NonFunctionalAssertion.class).to(DefaultNonFunctionalAssertion.class).in(Scopes.SINGLETON);
        bind(InstantAssertion.class).to(DefaultInstantAssertion.class).in(Scopes.SINGLETON);
        assertionsConfigured = true;
    }
}
