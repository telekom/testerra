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

package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.CollectedAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.testing.TestController;

/**
 * Default implementation of {@link TestController.Overrides}
 * @author Mike Reiche
 */
public class DefaultTestControllerOverrides implements TestController.Overrides {

    private final ThreadLocal<Integer> threadLocalTimeout = new ThreadLocal<>();
    private final ThreadLocal<Class<? extends Assertion>> threadLocalAssertionClass = new ThreadLocal<>();

    DefaultTestControllerOverrides() {
    }

    @Override
    public boolean hasElementTimeout() {
        return threadLocalTimeout.get()!=null;
    }

    @Override
    public int getElementTimeoutInSeconds() {
        Integer timeout = threadLocalTimeout.get();
        if (timeout == null) {
            timeout = UiElement.Properties.ELEMENT_TIMEOUT_SECONDS.asLong().intValue();
        }
        return timeout;
    }

    @Override
    public int setElementTimeout(int seconds) {
        int prevTimeout = -1;
        if (hasElementTimeout()) {
            prevTimeout = getElementTimeoutInSeconds();
        }
        if (seconds < 0) {
            threadLocalTimeout.remove();
        } else {
            threadLocalTimeout.set(seconds);
        }
        return prevTimeout;
    }

    @Override
    public Class<? extends Assertion> setAssertionClass(Class<? extends Assertion> newClass) {
        Class<? extends Assertion> prevClass = threadLocalAssertionClass.get();
        threadLocalAssertionClass.set(newClass);
        return prevClass;
    }

    @Override
    public Class<? extends Assertion> getAssertionClass() {
        Class<? extends Assertion> assertionClass = threadLocalAssertionClass.get();
        if (assertionClass==null) {
            if (UiElement.Properties.DEFAULT_ASSERT_IS_COLLECTOR.asBool()) {
                setAssertionClass(CollectedAssertion.class);
            } else {
                setAssertionClass(InstantAssertion.class);
            }
        }
        return assertionClass;
    }
}
