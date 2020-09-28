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
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.Sequence;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Default implementation of {@link TestController}
 * @author Mike Reiche
 */
public class DefaultTestController implements TestController, Loggable {

    private final TestController.Overrides overrides;
    private final ThreadLocal<LinkedList<RunnableConfiguration>> threadLocalConfigurations = new ThreadLocal<>();

    private abstract class RunnableConfiguration {
        Runnable setup(Runnable runnable) {
            return runnable;
        }
        void teardown() {

        }
    }

    @Inject
    protected DefaultTestController(TestController.Overrides overrides) {
        this.overrides = overrides;
    }

    /**
     * Runs all {@link #threadLocalConfigurations}
     * Its important that this method is synchronized!
     */
    private synchronized void run(Runnable runnable) {
        LinkedList<RunnableConfiguration> configurations = threadLocalConfigurations.get();
        for (RunnableConfiguration configuration : configurations) {
            runnable = configuration.setup(runnable);
        }
        runnable.run();
        configurations.forEach(configuration -> configuration.teardown());
        configurations.clear();
    }

    /**
     * Adds a {@link RunnableConfiguration} to the {@link #threadLocalConfigurations}
     * Its important that this method is synchronized!
     */
    private synchronized void addConfiguration(RunnableConfiguration configuration) {
        LinkedList<RunnableConfiguration> configurations = threadLocalConfigurations.get();
        if (configurations==null) {
            configurations = new LinkedList<>();
            threadLocalConfigurations.set(configurations);
        }
        configurations.add(configuration);
    }

    @Override
    public void collectAssertions(Runnable runnable) {
        collectAssertions().run(runnable);
    }

    @Override
    public DefaultTestController collectAssertions() {
        addConfiguration(new RunnableConfiguration() {
            Class<? extends Assertion> prevClass;
            @Override
            Runnable setup(Runnable runnable) {
                prevClass = overrides.setAssertionClass(CollectedAssertion.class);
                return runnable;
            }

            @Override
            void teardown() {
                overrides.setAssertionClass(prevClass);
            }
        });
        return this;
    }

    @Override
    public void nonFunctionalAssertions(Runnable runnable) {
        nonFunctionalAssertions().run(runnable);
    }

    @Override
    public DefaultTestController nonFunctionalAssertions() {
        addConfiguration(new RunnableConfiguration() {
            Class<? extends Assertion> prevClass;
            @Override
            Runnable setup(Runnable runnable) {
                prevClass = overrides.setAssertionClass(NonFunctionalAssertion.class);
                return runnable;
            }

            @Override
            void teardown() {
                overrides.setAssertionClass(prevClass);
            }
        });
        return this;
    }

    @Override
    public void withTimeout(int seconds, Runnable runnable) {
        withTimeout(seconds).run(runnable);
    }

    @Override
    public DefaultTestController withTimeout(int seconds) {
        addConfiguration(new RunnableConfiguration() {
            int prevTimeout;
            @Override
            Runnable setup(Runnable runnable) {
                log().info("Set timeout: "+ seconds);
                prevTimeout = overrides.setTimeout(seconds);
                return runnable;
            }

            @Override
            void teardown() {
                overrides.setTimeout(prevTimeout);
            }
        });
        return this;
    }

    @Override
    public void retryFor(int seconds, Runnable runnable) {
        retryFor(seconds).run(runnable);
    }

    @Override
    public DefaultTestController retryFor(int seconds) {
        addConfiguration(new RunnableConfiguration() {
            @Override
            Runnable setup(Runnable runnable) {
                return () -> {
                    log().info("Run retry sequence with " + seconds + " seconds");
                    Sequence sequence = new Sequence()
                            .setTimeoutMs(seconds * 1000);

                    AtomicReference<Throwable> atomicThrowable = new AtomicReference<>();
                    AtomicBoolean atomicSuccess = new AtomicBoolean();
                    sequence.run(() -> {
                        try {
                            runnable.run();
                            atomicSuccess.set(true);
                        } catch (Throwable throwable) {
                            atomicThrowable.set(throwable);
                            atomicSuccess.set(false);
                        }
                        return atomicSuccess.get();
                    });

                    if (!atomicSuccess.get()) {
                        throw new TimeoutException("Retry sequence timed out after " + sequence.getDurationMs() / 1000 + "s", atomicThrowable.get());
                    }
                };
            }
        });
        return this;
    }
}
