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
 * Allows changes of the {@link ThreadLocal} test flow
 * @author Mike Reiche
 */
public interface TestController {
    /**
     * Overrides for {@link ThreadLocal} test controlling
     */
    interface Overrides {
        boolean hasElementTimeout();
        /**
         * @return Configured or default element timeout
         */
        int getElementTimeoutInSeconds();

        /**
         * Sets a new element timeout and returns the previously configured
         * @param seconds If < 0, the timeout configuration will be removed
         */
        int setElementTimeout(int seconds);

        /**
         * Sets a new default assertion class
         * @return Returns the previously configured assertion class
         */
        Class<? extends Assertion> setAssertionClass(Class<? extends Assertion> newClass);

        /**
         * Gets the current assertion class
         */
        Class<? extends Assertion> getAssertionClass();
    }
    void collectAssertions(Runnable runnable);
    TestController collectAssertions();
    void nonFunctionalAssertions(Runnable runnable);
    TestController nonFunctionalAssertions();
    void withTimeout(int seconds, Runnable runnable);
    TestController withTimeout(int seconds);
    void retryFor(int seconds, Runnable runnable);
    TestController retryFor(int seconds);
}
