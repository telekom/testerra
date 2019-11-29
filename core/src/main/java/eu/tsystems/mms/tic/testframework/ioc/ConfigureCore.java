package eu.tsystems.mms.tic.testframework.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultAssertionWrapper;
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
import eu.tsystems.mms.tic.testframework.logging.LogAppender;
import eu.tsystems.mms.tic.testframework.report.IReport;
import eu.tsystems.mms.tic.testframework.report.ReportLogAppender;
import eu.tsystems.mms.tic.testframework.report.model.BrowserInformation;
import eu.tsystems.mms.tic.testframework.report.model.UapBrowserInformation;
import eu.tsystems.mms.tic.testframework.report.model.YauaaBrowserInformation;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.testing.DefaultTestController;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.utils.DefaultFormatter;
import eu.tsystems.mms.tic.testframework.utils.Formatter;

public class ConfigureCore extends AbstractModule {
    protected void configure() {
        bind(IReport.class).to(Report.class).in(Scopes.SINGLETON);
        bind(Formatter.class).to(DefaultFormatter.class).in(Scopes.SINGLETON);
        bind(AssertionFactory.class).to(DefaultAssertionFactory.class).in(Scopes.SINGLETON);
        bind(CollectedAssertion.class).to(DefaultCollectedAssertion.class).in(Scopes.SINGLETON);
        bind(NonFunctionalAssertion.class).to(DefaultNonFunctionalAssertion.class).in(Scopes.SINGLETON);
        bind(InstantAssertion.class).to(ThrowingAssertion.class).in(Scopes.SINGLETON);
        bind(TestAssertion.class).to(SilentAssertion.class).in(Scopes.SINGLETON);
        bind(AssertionsCollector.class).to(CollectedAssertions.class).in(Scopes.SINGLETON);
        bind(Assertion.class).to(DefaultAssertionWrapper.class).in(Scopes.SINGLETON);
        bind(TestController.class).to(DefaultTestController.class);
        bind(LogAppender.class).to(ReportLogAppender.class).in(Scopes.SINGLETON);
        bind(BrowserInformation.class).to(UapBrowserInformation.class);
        //bind(BrowserInformation.class).to(YauaaBrowserInformation.class);
    }
}
