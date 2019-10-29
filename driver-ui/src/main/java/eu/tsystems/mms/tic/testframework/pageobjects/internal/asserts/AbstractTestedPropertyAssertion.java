package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.IAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;

import java.util.function.Function;

public abstract class AbstractTestedPropertyAssertion<T> extends AbstractPropertyAssertion<T> {

    /**
     * @todo Make configurable by PropertyManager
     */
    private static final int sleepTimeInMsShortInterval = 200;
    private static final int timeoutInSecondsShortInterval = 1;

    private static final AssertionFactory assertionFactory = Testerra.ioc().getInstance(AssertionFactory.class);
    protected final IAssertion assertion = Testerra.ioc().getInstance(InstantAssertion.class);

    public AbstractTestedPropertyAssertion(AssertionProvider<T> provider) {
        super(provider);
    }

    public T actual() {
        return provider.actual();
    }

    protected void testTimer(Function<T, Boolean> testFunction) {
        Timer timer = new Timer(sleepTimeInMsShortInterval, timeoutInSecondsShortInterval * 1000);
        ThrowablePackedResponse throwablePackedResponse = timer.executeSequence(new Timer.Sequence<Object>() {
            @Override
            public void run() {
                setSkipThrowingException(true);
                setPassState(testFunction.apply(null));
            }
        });
        if (!throwablePackedResponse.isSuccessful()) {
            provider.failedRecursive();
            IAssertion finalAssertion = assertionFactory.create();
            finalAssertion.fail(throwablePackedResponse.getTimeoutException().getCause());
        }
    }
}
