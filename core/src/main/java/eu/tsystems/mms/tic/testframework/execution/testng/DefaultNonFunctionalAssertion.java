package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.internal.AssertionChecker;

public class DefaultNonFunctionalAssertion extends AbstractAssertion implements NonFunctionalAssertion {
    @Override
    public void fail(AssertionError error) {
        AssertionChecker.storeNonFunctionalInfo(error);
    }
}
