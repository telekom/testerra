package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.PageOverrides;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.Timer;

import java.util.function.Function;

/**
 * An abstract Property Assertion which performs a test
 * and informs the Assertion Provider about it.
 * @author Mike Reiche
 */
public abstract class AbstractTestedPropertyAssertion<T> extends AbstractPropertyAssertion<T> {
    private static final PageOverrides pageOverrides = Testerra.injector.getInstance(PageOverrides.class);
    private static final AssertionFactory assertionFactory = Testerra.injector.getInstance(AssertionFactory.class);
    protected final Assertion instantAssertion = Testerra.injector.getInstance(InstantAssertion.class);

    public AbstractTestedPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    public T getActual() {
        return provider.getActual();
    }

    protected boolean testTimer(Function<T, Boolean> testFunction) {
        int useTimeoutSeconds = timeoutSeconds;
        if (pageOverrides.hasTimeoutSeconds()) useTimeoutSeconds = pageOverrides.getTimeoutSeconds();

        Timer timer = new Timer(
            IGuiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong(),
            useTimeoutSeconds * 1000
        );
        ThrowablePackedResponse<AssertionError> packedResponse = timer.executeSequence(new Timer.Sequence<AssertionError>() {
            @Override
            public void run() {
                // Prevent TimeoutException on any other exception
                setSkipThrowingException(true);
                try {
                    setPassState(testFunction.apply(null));
                } catch (AssertionError e) {
                    setReturningObject(e);
                    failedRecursive();
                    setPassState(false);
                } catch (Throwable throwable) {
                    setReturningObject(new AssertionError(throwable));
                    failedRecursive();
                    setPassState(false);
                }
            }
        });
        if (!packedResponse.isSuccessful()) {
            failedFinallyRecursive();
            // Dont handle exceptions when it should only wait
            if (!shouldWait) {
                Assertion finalAssertion = assertionFactory.create();
                finalAssertion.fail(packedResponse.getResponse());
            }
            return false;
        } else {
            return true;
        }
    }
}
