package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultInstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultNonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.FunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;

public class TesterraCoreModule extends AbstractModule {

    /**
     * I know that this is best practice,
     * but I currently found no other way than this
     * to override already bind interfaces,
     * even with {@link Modules#override(Module...)}
     * The Modules needs to be sorted reverse in order to get this work.
     */
    protected static boolean assertionsConfigured = false;

    protected void configure() {
        if (!assertionsConfigured) {
            configureAssertions();
        }
    }

    protected void configureAssertions() {
        bind(FunctionalAssertion.class).to(DefaultFunctionalAssertion.class).in(Scopes.SINGLETON);
        bind(NonFunctionalAssertion.class).to(DefaultNonFunctionalAssertion.class).in(Scopes.SINGLETON);
        bind(InstantAssertion.class).to(DefaultInstantAssertion.class).in(Scopes.SINGLETON);
        assertionsConfigured = true;
    }
}
