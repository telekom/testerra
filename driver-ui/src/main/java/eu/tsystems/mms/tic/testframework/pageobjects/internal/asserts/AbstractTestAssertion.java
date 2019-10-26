package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;

import java.util.function.Function;

public abstract class AbstractTestAssertion<T> extends AbstractAssertion<T> {

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
        if (throwablePackedResponse.isSuccessful()) {
            provider.passed();
        } else {
            provider.failed();
        }
        return throwablePackedResponse;
    }
}
