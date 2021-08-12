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
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssertion;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.Sequence;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import org.testng.Assert;

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
        CollectedAssertion assertionImpl = Testerra.getInjector().getInstance(CollectedAssertion.class);
        Assertion previousAssertionImpl = this.overrides.setAssertionImpl(assertionImpl);
        try {
            runnable.run();
        } catch (Throwable t) {
            assertionImpl.fail(new Error(t));
        }
        this.overrides.setAssertionImpl(previousAssertionImpl);
    }

    @Override
    public void optionalAssertions(Runnable runnable) {
        OptionalAssertion assertionImpl = Testerra.getInjector().getInstance(OptionalAssertion.class);
        Assertion previousAssertionImpl = this.overrides.setAssertionImpl(assertionImpl);
        try {
            runnable.run();
        } catch (Throwable t) {
            assertionImpl.fail(new Error(t));
        }
        this.overrides.setAssertionImpl(previousAssertionImpl);
    }

    @Override
    public void withTimeout(int seconds, Runnable runnable) {
        int prevTimeout = overrides.setTimeout(seconds);
        try {
            runnable.run();
            overrides.setTimeout(prevTimeout);
        } catch (Throwable throwable) {
            overrides.setTimeout(prevTimeout);
            throw throwable;
        }
    }

    private String createSequenceLog(int timeoutSeconds, Sequence sequence) {
        return String.format("after %.2fs of %ds", sequence.getDurationMs()/1000f, timeoutSeconds);
    }

    @Override
    public void retryFor(int seconds, Assert.ThrowingRunnable runnable, Runnable whenFail) {
        final Throwable finalThrowable = _waitFor(seconds, runnable, (sequence, throwable) -> {
            if (whenFail != null) {
                whenFail.run();
            }
            if (!sequence.timedOut()) {
                log().info("Retry " + createSequenceLog(seconds, sequence) + " because of: " + throwable.getMessage());
            } else {
                log().warn("Not retrying " + createSequenceLog(seconds, sequence) + " because of: " + throwable.getMessage());
            }
        });
        if (finalThrowable != null) {
            throw new TimeoutException("Retry sequence timed out", finalThrowable);
        }
    }

    @Override
    public void retryTimes(int times, Assert.ThrowingRunnable runnable, Runnable whenFail) {
        final Throwable finalThrowable = _waitTimes(times, runnable, (s, throwable) -> {
            if (whenFail != null) {
                whenFail.run();
            }
            log().info(String.format("Retry attempt %d/%d because of: %s", s, times, throwable.getMessage()));
        });
        if (finalThrowable != null) {
            throw new RuntimeException("Retry sequence failed", finalThrowable);
        }
    }

    @Override
    public boolean waitFor(int seconds, Assert.ThrowingRunnable runnable, Runnable whenFail) {
        return _waitFor(seconds, runnable, (sequence,throwable) -> {
            log().info("Giving up " + createSequenceLog(seconds, sequence) + " because of: " + throwable.getMessage());
            if (whenFail != null) {
                whenFail.run();
            }
        }) == null;
    }

    private Throwable _waitFor(
            int seconds,
            Assert.ThrowingRunnable runnable,
            BiConsumer<Sequence, Throwable> whenFail
    ) {
        Sequence sequence = new Sequence()
                .setTimeoutMs(seconds * 1000L);

        AtomicReference<Throwable> atomicThrowable = new AtomicReference<>();
        sequence.run(() -> {
            try {
                runnable.run();
                // No throwable catched, reset the atomic value
                atomicThrowable.set(null);
            } catch (Throwable throwable) {
                atomicThrowable.set(throwable);
                whenFail.accept(sequence, throwable);
            }
            // Sequence ends when throwable is empty
            return atomicThrowable.get()==null;
        });
        return atomicThrowable.get();
    }

    @Override
    public boolean waitTimes(int times, Assert.ThrowingRunnable runnable, Runnable whenFail) {
        return _waitTimes(times, runnable, (s, throwable) -> {
            log().info(String.format("Giving up attempt %d/%d because of: %s", s, times, throwable.getMessage()));
            if (whenFail != null) {
                whenFail.run();
            }
        }) == null;
    }

    private Throwable _waitTimes(int times, Assert.ThrowingRunnable runnable, BiConsumer<Integer, Throwable> whenFail) {
        Throwable catchedThrowable = null;
        for (int s = 0; s < times; ++s) {
            try {
                runnable.run();
                catchedThrowable = null;
                break;
            } catch (Throwable throwable) {
                catchedThrowable = throwable;
                whenFail.accept(s+1, throwable);
            }
        }
        return catchedThrowable;
    }
}
