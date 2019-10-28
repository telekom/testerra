package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.execution.testng.IAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.internal.AssertionsCollector;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;

import java.util.function.Function;

public abstract class AbstractTestedPropertyAssertion<T> extends AbstractPropertyAssertion<T> implements INonFunctionalPropertyAssertion {

    private static final int sleepTimeInMsShortInterval = 200;
    private static final int timeoutInSecondsShortInterval = 1;

    private static AssertionsCollector assertionsCollector = TesterraCommons.ioc().getInstance(AssertionsCollector.class);

    protected IAssertion assertion = TesterraCommons.ioc().getInstance(InstantAssertion.class);

    public AbstractTestedPropertyAssertion(AssertionProvider<T> provider) {
        super(provider);
    }

    public T actual() {
        return provider.actual();
    }

    protected void testTimer(Function<T, Boolean> testFunction) {
        final Timer timer = new Timer(sleepTimeInMsShortInterval, timeoutInSecondsShortInterval * 1000);
        final ThrowablePackedResponse throwablePackedResponse = timer.executeSequence(new Timer.Sequence<Object>() {
            @Override
            public void run() {
                setSkipThrowingException(true);
                setPassState(testFunction.apply(null));
            }
        });
        if (!throwablePackedResponse.isSuccessful()) {
            final TimeoutException timeoutException = throwablePackedResponse.getTimeoutException();
            provider.failedRecursive();
            if (!assertionsCollector.store(timeoutException)) {
                throw timeoutException;
            }
        }
    }

    @Override
    public INonFunctionalPropertyAssertion nonFunctional() {
        assertion = TesterraCommons.ioc().getInstance(NonFunctionalAssertion.class);
        return this;
    }
}
