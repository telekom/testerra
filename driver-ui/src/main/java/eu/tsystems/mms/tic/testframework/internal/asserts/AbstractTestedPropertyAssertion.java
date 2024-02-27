/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package eu.tsystems.mms.tic.testframework.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.DefaultAssertionWrapper;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.utils.Sequence;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An abstract Property Assertion which performs a test
 * and informs the Assertion Provider about it.
 * @author Mike Reiche
 */
public abstract class AbstractTestedPropertyAssertion<T> extends AbstractPropertyAssertion<T> implements Loggable {
    /**
     * This instance should always be an {@link DefaultAssertionWrapper}
     */
    protected static final Assertion assertionImpl = Testerra.getInjector().getInstance(Assertion.class);
    private static final TestController.Overrides overrides = Testerra.getInjector().getInstance(TestController.Overrides.class);

    public AbstractTestedPropertyAssertion(AbstractPropertyAssertion<T> parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    /**
     * This is the general test sequence
     * @param actualProperty The {@link ActualProperty} to test
     * @param testFunction The test {@link Predicate}
     * @param failMessageSupplier The fail message {@link Supplier} when the test finally fails.
     * @return True if the test passed
     */
    protected boolean testSequence(
            ActualProperty<T> actualProperty,
            Predicate<T> testFunction,
            Function<T, String> failMessageSupplier
    ) {
        int useTime = config.useTimeout;
        if (useTime < 0) {
            useTime = overrides.getTimeoutInSeconds();
        }
        Sequence sequence = new Sequence()
                .setWaitMsAfterRun(UiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong())
                .setTimeoutMs(useTime * 1000L);

        AtomicBoolean atomicPassed = new AtomicBoolean(false);
        AtomicReference<Throwable> atomicThrowable = new AtomicReference<>();
        AtomicReference<T> atomicActual = new AtomicReference<>();
        sequence.run(() -> {
            try {
                // Get the actual value
                T actual = actualProperty.getActual();

                // Pass the actual value to the test function
                atomicPassed.set(testFunction.test(actual));

                // Set the actual to the atomic actual for later use
                atomicActual.set(actual);

            } catch (Throwable throwable) {
                failedRecursive();
                atomicThrowable.set(throwable);
            }
            return atomicPassed.get();
        });

        boolean passed = atomicPassed.get();

        if (!passed) {
            failedFinallyRecursive();

            // Dont handle exceptions when it should only wait
            if (config.throwErrors) {

                // Take assertion from config or global overrides
                Assertion useAssertion;
                if (config.useAssertion != null) {
                    useAssertion = config.useAssertion;
                } else {
                    useAssertion = assertionImpl;
                }

                String message = null;
                Throwable finalThrowable;
                try {
                    message = failMessageSupplier.apply(atomicActual.get());
                    finalThrowable = atomicThrowable.get();
                // When something happens during message retrieval
                } catch (Throwable throwable) {
                    finalThrowable = throwable;
                }

                useAssertion.fail(wrapAssertionErrorRecursive(new AssertionError(message, finalThrowable)));
            }
        } else {
            passedRecursive();
        }
        return passed;
    }
}
