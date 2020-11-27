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

package eu.tsystems.mms.tic.testframework.testing;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssertion;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.Sequence;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Default implementation of {@link TestController}
 * @author Mike Reiche
 */
public class DefaultTestController implements TestController, Loggable {

    private final TestController.Overrides overrides;

    @Inject
    protected DefaultTestController(TestController.Overrides overrides) {
        this.overrides = overrides;
    }

    @Override
    public void collectAssertions(Runnable runnable) {
        Class<? extends Assertion> prevClass = overrides.setAssertionClass(CollectedAssertion.class);
        runnable.run();
        overrides.setAssertionClass(prevClass);
    }

    @Override
    public void optionalAssertions(Runnable runnable) {
        Class<? extends Assertion> prevClass = overrides.setAssertionClass(OptionalAssertion.class);
        runnable.run();
        overrides.setAssertionClass(prevClass);
    }

    @Override
    public void withTimeout(int seconds, Runnable runnable) {
        int prevTimeout = overrides.setTimeout(seconds);
        runnable.run();
        overrides.setTimeout(prevTimeout);
    }

    @Override
    public void retryFor(int seconds, Runnable runnable, Runnable whenFail) {
        Sequence sequence = new Sequence()
                .setTimeoutMs(seconds * 1000);

        AtomicReference<Throwable> atomicThrowable = new AtomicReference<>();
        AtomicBoolean atomicSuccess = new AtomicBoolean();
        sequence.run(() -> {
            try {
                runnable.run();
                atomicSuccess.set(true);
            } catch (Throwable throwable) {
                log().info("Retry after " + sequence.getDurationMs() + "ms because of: " + throwable.getMessage());
                atomicThrowable.set(throwable);
                atomicSuccess.set(false);
                if (whenFail != null) {
                    whenFail.run();
                }
            }
            return atomicSuccess.get();
        });

        if (!atomicSuccess.get()) {
            throw new TimeoutException("Retry sequence timed out after " + sequence.getDurationMs() / 1000 + "s", atomicThrowable.get());
        }
    }
}
