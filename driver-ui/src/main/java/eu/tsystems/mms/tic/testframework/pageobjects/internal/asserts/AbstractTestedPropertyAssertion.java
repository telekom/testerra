package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.utils.Sequence;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * An abstract Property Assertion which performs a test
 * and informs the Assertion Provider about it.
 * @author Mike Reiche
 */
public abstract class AbstractTestedPropertyAssertion<T> extends AbstractPropertyAssertion<T> {
    private static final TestController.Overrides overrides = Testerra.injector.getInstance(TestController.Overrides.class);
    private static final AssertionFactory assertionFactory = Testerra.injector.getInstance(AssertionFactory.class);
    protected final Assertion instantAssertion = Testerra.injector.getInstance(InstantAssertion.class);

    public AbstractTestedPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    public T getActual() {
        return provider.getActual();
    }

    protected boolean testTimer(Supplier<Boolean> testFunction, Supplier<String> failMessageSupplier) {
        long useTimeout = timeout;
        if (overrides.hasTimeout()) useTimeout = overrides.getTimeoutInSeconds();
        if (useTimeout < 0) useTimeout = UiElement.Properties.ELEMENT_TIMEOUT_SECONDS.asLong();

        Sequence sequence = new Sequence()
                .setPauseMs(UiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong())
                .setPeriodMs(useTimeout*1000);

        AtomicBoolean atomicPassed = new AtomicBoolean(false);
        AtomicReference<Throwable> atomicThrowable = new AtomicReference<>();

        sequence.run(() -> {
            try {
                atomicPassed.set(testFunction.get());
            } catch (Throwable throwable) {
                failedRecursive();
                atomicThrowable.set(throwable);
            }
            return atomicPassed.get();
        });

        if (!atomicPassed.get()) {
            failedFinallyRecursive();
            // Dont handle exceptions when it should only wait
            if (!shouldWait) {
                Assertion finalAssertion = assertionFactory.create();
                try {
                    finalAssertion.fail(failMessageSupplier.get(), atomicThrowable.get());
                } catch (Throwable throwable) {
                    finalAssertion.fail(new AssertionError(throwable));
                }
            }
        }
        return atomicPassed.get();
    }
}
