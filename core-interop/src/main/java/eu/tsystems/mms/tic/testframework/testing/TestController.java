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

/**
 * Allows to run blocks of code in a {@link Runnable} with {@link Overrides}
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
         * @return Configured or default timeout for any actions
         */
        int getTimeoutInSeconds();

        /**
         * Sets a timeout for any action
         * @param seconds If < 0, the timeout configuration will be removed
         * @return Returns the previously configured timeout
         */
        int setTimeout(int seconds);

        /**
         * Determines if a assertion class has been configured
         */
        boolean hasAssertionClass();

        /**
         * Sets a new default assertion class for any action
         * @return Returns the previously configured assertion class
         */
        Class<? extends Assertion> setAssertionClass(Class<? extends Assertion> newClass);

        /**
         * @return Configured or default assertion class for any actions
         */
        Class<? extends Assertion> getAssertionClass();
    }

    /**
     * Runs a {@link Runnable} with collected assertions
     */
    void collectAssertions(Runnable runnable);

    /**
     * Configures the next {@link Runnable} with collection assertions
     */
    TestController collectAssertions();

    /**
     * Runs a {@link Runnable} with non-functional assertions
     */
    void nonFunctionalAssertions(Runnable runnable);

    /**
     * Configures the next {@link Runnable} with non-functional assertions
     */
    TestController nonFunctionalAssertions();

    /**
     * Runs a {@link Runnable} with a specified timeout
     */
    void withTimeout(int seconds, Runnable runnable);

    /**
     * Configures the next {@link Runnable} with a specified timeout
     */
    TestController withTimeout(int seconds);

    /**
     * Runs a {@link Runnable} while {@link Throwable} occurs for a specified period
     * @param seconds Period in seconds
     */
    void retryFor(int seconds, Runnable runnable);

    /**
     * Configures the next {@link Runnable} to run while {@link Throwable} occurs for a specified period
     * @param seconds Period in seconds
     */
    TestController retryFor(int seconds);
}
