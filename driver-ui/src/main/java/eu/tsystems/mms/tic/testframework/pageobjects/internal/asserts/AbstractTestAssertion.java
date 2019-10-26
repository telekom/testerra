package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.internal.AssertionChecker;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import org.testng.Assert;

import java.util.function.Function;

public abstract class AbstractTestAssertion<T> extends AbstractAssertion<T> implements INonFunctionalAssertion {
    protected boolean nonFunctional = false;
    private final int sleepTimeInMsShortInterval = 200;
    private final int timeoutInSecondsShortInterval = 1;

    public AbstractTestAssertion(AssertionProvider<T> provider) {
        super(provider);
    }

    public T actual() {
        return provider.actual();
    }

    protected ThrowablePackedResponse testTimer(Function<T, Boolean> testFunction) {
        Timer timer = new Timer(sleepTimeInMsShortInterval, timeoutInSecondsShortInterval * 1000);
        final ThrowablePackedResponse throwablePackedResponse = timer.executeSequence(new Timer.Sequence<Object>() {
            @Override
            public void run() {
                setPassState(testFunction.apply(null));
            }
        });
        if (!throwablePackedResponse.isSuccessful()) {
            provider.failedRecursive();
        }
        return throwablePackedResponse;
    }

    protected void fail(final String assertionSuffix) {
        final String message = String.format("%s [%s] %s", provider.traceSubjectString(), provider.actual(), assertionSuffix);
        if (nonFunctional) {
            AssertionError assertionError = new AssertionError(message);
            AssertionChecker.storeNonFunctionalInfo(assertionError);
        } else {
            Assert.fail(message);
        }
    }

    @Override
    public INonFunctionalAssertion nonFunctional() {
        nonFunctional = true;
        return this;
    }
}
