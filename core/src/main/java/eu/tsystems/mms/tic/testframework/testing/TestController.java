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

import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import org.testng.Assert;

/**
 * Allows to run blocks of code in a {@link Runnable} with {@link Overrides}
 *
 * @author Mike Reiche
 */
public interface TestController {
    /**
     * Overrides for {@link ThreadLocal} test controlling
     */
    interface Overrides {
        /**
         * Determines if a timeout has been configured
         */
        boolean hasTimeout();

        /**
         * Determines if a delay after actions has been configured
         */
        boolean hasDelayAfterAction();

        /**
         * @return Configured or default timeout for any actions
         */
        int getTimeoutInSeconds();

        /**
         * @return Configured or default delay after actions
         */
        int getDelayAfterAction();

        /**
         * Sets a timeout for any action
         *
         * @param seconds If < 0, the timeout configuration will be removed
         * @return Returns the previously configured timeout
         */
        int setTimeout(int seconds);

        /**
         * Sets a delay after actions on a UiElement
         *
         * @param millis If < 0, the delay configuration will be removed
         * @return Returns the previously configured delay
         */
        int setDelayAfterAction(int millis);

        Assertion getAssertionImpl();

        Assertion setAssertionImpl(Assertion newAssertionImpl);
    }

    /**
     * Runs a {@link Runnable} with collected assertions
     */
    void collectAssertions(Runnable runnable);

    /**
     * Runs a {@link Runnable} with non-functional assertions
     */
    void optionalAssertions(Runnable runnable);

    /**
     * Runs a {@link Runnable} with a specified timeout
     */
    void withTimeout(int seconds, Runnable runnable);

    /**
     * Runs a {@link Runnable} with a specified delay after actions
     */
    void withDelayAfterAction(int millis, Runnable runnable);

    /**
     * Runs a {@link Runnable} while {@link Throwable} occurs for a specified period.
     *
     * @param seconds Period in seconds
     * @param runnable Runnable
     * @param whenFail Runnable which gets called when a throwable occurred
     */
    void retryFor(int seconds, Assert.ThrowingRunnable runnable, Runnable whenFail);

    /**
     * See {@link #retryFor(int, Assert.ThrowingRunnable, Runnable)}
     */
    default void retryFor(int seconds, Assert.ThrowingRunnable runnable) {
        retryFor(seconds, runnable, null);
    }

    /**
     * Runs a {@link Runnable} while {@link Throwable} occurs n times.
     *
     * @param times Retry n times
     * @param runnable Runnable
     * @param whenFail Runnable which gets called when a throwable occurred
     */
    void retryTimes(int times, Assert.ThrowingRunnable runnable, Runnable whenFail);

    /**
     * See {@link #retryTimes(int, Assert.ThrowingRunnable, Runnable)}
     */
    default void retryTimes(int times, Assert.ThrowingRunnable runnable) {
        retryTimes(times, runnable, null);
    }

    /**
     * Does the same as {@link #retryFor(int, Assert.ThrowingRunnable, Runnable)} but without throwing any exception
     */
    boolean waitFor(int seconds, Assert.ThrowingRunnable runnable, Runnable whenFail);

    /**
     * See {@link #waitFor(int, Assert.ThrowingRunnable, Runnable)}
     */
    default boolean waitFor(int seconds, Assert.ThrowingRunnable runnable) {
        return waitFor(seconds, runnable, null);
    }

    /**
     * Does the same as {@link #retryTimes(int, Assert.ThrowingRunnable, Runnable)} but without throwing any exception
     */
    boolean waitTimes(int times, Assert.ThrowingRunnable runnable, Runnable whenFail);

    /**
     * See {@link #waitTimes(int, Assert.ThrowingRunnable, Runnable)}
     */
    default boolean waitTimes(int times, Assert.ThrowingRunnable runnable) {
        return waitTimes(times, runnable, null);
    }
}
