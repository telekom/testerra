package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
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
    protected static final AssertionFactory assertionFactory = Testerra.injector.getInstance(AssertionFactory.class);
    protected static final Assertion assertion = Testerra.injector.getInstance(InstantAssertion.class);

    public AbstractTestedPropertyAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    protected boolean testTimer(Supplier<Boolean> testFunction, Supplier<String> failMessageSupplier) {
        Sequence sequence = new Sequence()
                .setPauseMs(config.pauseIntervalMs)
                .setTimeoutMs(config.timeoutInSeconds * 1000);

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
            if (!config.shouldWait) {
                Assertion finalAssertion = assertionFactory.create();
                MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();
                try {
                    String message = failMessageSupplier.get();
                    Throwable throwable = atomicThrowable.get();
                    finalAssertion.fail(message, throwable);
                    if (currentMethodContext != null) {
                        currentMethodContext.errorContext().setThrowable(message, throwable);
                    }
                } catch (Throwable throwable) {
                    finalAssertion.fail(new AssertionError(throwable));
                    if (currentMethodContext != null) {
                        currentMethodContext.errorContext().setThrowable(throwable);
                    }
                }
            }
        }
        return atomicPassed.get();
    }
}
