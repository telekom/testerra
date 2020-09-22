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
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.PageOverrides;
import eu.tsystems.mms.tic.testframework.utils.TinyTimer;
import java.util.HashSet;

/**
 * Default implementation of {@link TestController}
 * @author Mike Reiche
 */
public class DefaultTestController implements TestController {

    private final AssertionFactory assertionFactory;
    private final PageOverrides pageOverrides;
    private final ThreadLocal<HashSet<RunnableConfiguration>> threadLocalConfigurations = new ThreadLocal<>();

    private abstract class RunnableConfiguration {
        Runnable setup(Runnable runnable) {
            return runnable;
        }
        void teardown() {

        }
    }

    @Inject
    protected DefaultTestController(AssertionFactory assertionFactory, PageOverrides pageOverrides) {
        this.assertionFactory = assertionFactory;
        this.pageOverrides = pageOverrides;
    }


    private void run(Runnable runnable) {
        HashSet<RunnableConfiguration> configurations = threadLocalConfigurations.get();
        for (RunnableConfiguration configuration : configurations) {
            runnable = configuration.setup(runnable);
        }
        runnable.run();
        configurations.forEach(configuration -> configuration.teardown());
        configurations.clear();
    }

    @Override
    public void collectAssertions(Runnable runnable) {
        collectAssertions().run(runnable);
    }

    private void addConfiguration(RunnableConfiguration configuration) {
        HashSet<RunnableConfiguration> configurations = threadLocalConfigurations.get();
        if (configurations==null) {
            configurations = new HashSet<>();
            threadLocalConfigurations.set(configurations);
        }
        configurations.add(configuration);
    }

    @Override
    public DefaultTestController collectAssertions() {
        addConfiguration(new RunnableConfiguration() {
            Class<? extends Assertion> prevClass;
            @Override
            Runnable setup(Runnable runnable) {
                prevClass = assertionFactory.setDefault(CollectedAssertion.class);
                return runnable;
            }

            @Override
            void teardown() {
                assertionFactory.setDefault(prevClass);
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
                prevClass = assertionFactory.setDefault(NonFunctionalAssertion.class);
                return runnable;
            }

            @Override
            void teardown() {
                assertionFactory.setDefault(prevClass);
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
                prevTimeout = pageOverrides.setTimeout(seconds);
                return runnable;
            }

            @Override
            void teardown() {
                pageOverrides.setTimeout(prevTimeout);
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
                    TinyTimer timer = new TinyTimer()
                            .setPeriodMs(seconds * 1000);

                    timer.run(() -> {
                        try {
                            runnable.run();
                            return true;
                        } catch (Throwable e) {
                            return false;
                        }
                    });
                };
            }
        });
        return this;
    }
}
