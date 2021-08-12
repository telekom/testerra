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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;

/**
 * Default implementation of {@link ThreadLocal} {@link TestController.Overrides}
 * @author Mike Reiche
 */
public class DefaultTestControllerOverrides implements TestController.Overrides {

    private final ThreadLocal<Integer> threadLocalTimeout = new ThreadLocal<>();
    private final ThreadLocal<Assertion> threadLocalAssertionImpl = new ThreadLocal<>();

    DefaultTestControllerOverrides() {
    }

    @Override
    public boolean hasTimeout() {
        return threadLocalTimeout.get()!=null;
    }

    @Override
    public int getTimeoutInSeconds() {
        Integer integer = threadLocalTimeout.get();
        if (integer == null) return -1;
        else return integer;
    }

    @Override
    public int setTimeout(int seconds) {
        Integer prevTimeout = getTimeoutInSeconds();
        if (seconds < 0) {
            // Back to default
            threadLocalTimeout.remove();
        } else {
            threadLocalTimeout.set(seconds);
        }
        return prevTimeout;
    }

    @Override
    public Assertion getAssertionImpl() {
        Assertion assertionImpl = this.threadLocalAssertionImpl.get();
        if (assertionImpl == null) {
            assertionImpl = Testerra.getInjector().getInstance(InstantAssertion.class);
        }
        return assertionImpl;
    }

    @Override
    public Assertion setAssertionImpl(Assertion newAssertionImpl) {
        Assertion assertionImpl = this.threadLocalAssertionImpl.get();
        if (newAssertionImpl == null) {
            this.threadLocalAssertionImpl.remove();
        } else {
            this.threadLocalAssertionImpl.set(newAssertionImpl);
        }
        return assertionImpl;
    }
}
