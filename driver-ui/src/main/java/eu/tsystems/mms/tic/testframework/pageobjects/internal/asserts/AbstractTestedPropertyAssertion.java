package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.IAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.PageConfig;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;

import java.util.function.Function;

public abstract class AbstractTestedPropertyAssertion<T> extends AbstractPropertyAssertion<T> {
    private static final PageConfig pageConfig = Testerra.ioc().getInstance(PageConfig.class);
    private static final AssertionFactory assertionFactory = Testerra.ioc().getInstance(AssertionFactory.class);
    protected final IAssertion assertion = Testerra.ioc().getInstance(InstantAssertion.class);

    public AbstractTestedPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    public T actual() {
        return provider.actual();
    }

    protected void testTimer(Function<T, Boolean> testFunction) {
        Timer timer = new Timer(Testerra.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong(), pageConfig.getElementTimeoutInSeconds() * 1000);
        ThrowablePackedResponse throwablePackedResponse = timer.executeSequence(new Timer.Sequence<Object>() {
            @Override
            public void run() {
                setSkipThrowingException(true);
                setPassState(testFunction.apply(null));
            }
        });
        if (!throwablePackedResponse.isSuccessful()) {
            failedRecursive();
            IAssertion finalAssertion = assertionFactory.create();
            finalAssertion.fail(throwablePackedResponse.getTimeoutException().getCause());
        }
    }
}
