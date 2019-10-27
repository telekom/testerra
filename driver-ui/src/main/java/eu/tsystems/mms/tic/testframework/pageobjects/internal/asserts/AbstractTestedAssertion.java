package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.execution.testng.FunctionalAssert;
import eu.tsystems.mms.tic.testframework.execution.testng.IAssert;
import eu.tsystems.mms.tic.testframework.execution.testng.INonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;

import java.util.function.Function;

public abstract class AbstractTestedAssertion<T> extends AbstractAssertion<T> implements INonFunctionalAssertion {
    private final int sleepTimeInMsShortInterval = 200;
    private final int timeoutInSecondsShortInterval = 1;
    protected IAssert configuredAssert = TesterraCommons.ioc().getInstance(FunctionalAssert.class);

    public AbstractTestedAssertion(AssertionProvider<T> provider) {
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
        configuredAssert.fail(message);
    }

    @Override
    public INonFunctionalAssertion nonFunctional() {
        configuredAssert = TesterraCommons.ioc().getInstance(INonFunctionalAssert.class);
        return this;
    }
}
