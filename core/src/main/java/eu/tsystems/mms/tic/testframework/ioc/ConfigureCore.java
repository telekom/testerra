package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultAssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultCollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultNonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.SilentAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.TestAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.ThrowingAssertion;
import eu.tsystems.mms.tic.testframework.internal.AssertionsCollector;
import eu.tsystems.mms.tic.testframework.internal.CollectedAssertions;
import eu.tsystems.mms.tic.testframework.report.IReport;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;

public class ConfigureCore extends AbstractModule {
    protected static boolean assertionsCollectorConfigured = false;
    protected static boolean assertionsConfigured = false;
    protected void configure() {
        if (!assertionsConfigured) {
            configureAssertions();
        }
        if (!assertionsCollectorConfigured) {
            configureAssertionsCollector();
        }
        bind(IReport.class).to(Report.class).in(Scopes.SINGLETON);
    }

    protected void configureAssertions() {
        bind(AssertionFactory.class).to(DefaultAssertionFactory.class).in(Scopes.SINGLETON);
        bind(CollectedAssertion.class).to(DefaultCollectedAssertion.class).in(Scopes.SINGLETON);
        bind(NonFunctionalAssertion.class).to(DefaultNonFunctionalAssertion.class).in(Scopes.SINGLETON);
        bind(InstantAssertion.class).to(ThrowingAssertion.class).in(Scopes.SINGLETON);
        bind(TestAssertion.class).to(SilentAssertion.class).in(Scopes.SINGLETON);
        assertionsConfigured = true;
    }

    protected void configureAssertionsCollector() {
        bind(AssertionsCollector.class).to(CollectedAssertions.class).in(Scopes.SINGLETON);
        assertionsCollectorConfigured = true;
    }
}
