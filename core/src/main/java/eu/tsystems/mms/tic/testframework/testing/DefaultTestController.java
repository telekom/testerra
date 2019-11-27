package eu.tsystems.mms.tic.testframework.testing;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.PageOverrides;

public class DefaultTestController implements TestController {

    private final AssertionFactory assertionFactory;
    private final PageOverrides pageOverrides;

    @Inject
    protected DefaultTestController(AssertionFactory assertionFactory, PageOverrides pageOverrides) {
        this.assertionFactory = assertionFactory;
        this.pageOverrides = pageOverrides;
    }

    public void collectAssertions(Runnable runnable) {
        Class<? extends Assertion> prevClass = assertionFactory.setDefault(CollectedAssertion.class);
        runnable.run();
        assertionFactory.setDefault(prevClass);
    }

    public void nonFunctionalAssertions(Runnable runnable) {
        Class<? extends Assertion> prevClass = assertionFactory.setDefault(NonFunctionalAssertion.class);
        runnable.run();
        assertionFactory.setDefault(prevClass);
    }

    public void withElementTimeout(int seconds, Runnable runnable) {
        int prevTimeout = pageOverrides.setTimeoutSeconds(seconds);
        runnable.run();
        pageOverrides.setTimeoutSeconds(prevTimeout);
    }
}
