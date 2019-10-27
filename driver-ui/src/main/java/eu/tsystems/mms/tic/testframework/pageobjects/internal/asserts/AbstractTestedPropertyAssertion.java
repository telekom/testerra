package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.execution.testng.FunctionalAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.IAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;

import java.util.function.Function;

public abstract class AbstractTestedPropertyAssertion<T> extends AbstractPropertyAssertion<T> implements INonFunctionalPropertyAssertion {
    private final int sleepTimeInMsShortInterval = 200;
    private final int timeoutInSecondsShortInterval = 1;
    protected IAssertion configuredAssert = TesterraCommons.ioc().getInstance(FunctionalAssertion.class);

    public AbstractTestedPropertyAssertion(AssertionProvider<T> provider) {
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
    public INonFunctionalPropertyAssertion nonFunctional() {
        configuredAssert = TesterraCommons.ioc().getInstance(NonFunctionalAssertion.class);
        return this;
    }
}
