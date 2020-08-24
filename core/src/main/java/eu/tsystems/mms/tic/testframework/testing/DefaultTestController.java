package eu.tsystems.mms.tic.testframework.testing;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertionFactory;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.PageOverrides;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import java.util.HashSet;

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
                prevTimeout = pageOverrides.setTimeoutSeconds(seconds);
                return runnable;
            }

            @Override
            void teardown() {
                pageOverrides.setTimeoutSeconds(prevTimeout);
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
                    Timer retryTimer = new Timer(UiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong(), seconds * 1000);
                    retryTimer.executeSequence(new Timer.Sequence() {
                        @Override
                        public void run() {
                            runnable.run();
                        }
                    });
                };
            }
        });
        return this;
    }
}
