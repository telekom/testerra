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

package eu.tsystems.mms.tic.testframework.execution.testng;

import com.google.inject.Inject;
import com.google.inject.Injector;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;

public class DefaultAssertionFactory implements AssertionFactory {
    private ThreadLocal<Class<? extends Assertion>> threadLocalAssertionClass = new ThreadLocal<>();

    private final Injector injector;

    @Inject
    DefaultAssertionFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Class<? extends Assertion> setDefault(Class<? extends Assertion> newClass) {
        Class<? extends Assertion> prevClass = threadLocalAssertionClass.get();
        threadLocalAssertionClass.set(newClass);
        return prevClass;
    }

    @Override
    public Assertion create() {
        Class<? extends Assertion> assertionClass = threadLocalAssertionClass.get();
        if (assertionClass==null) {
            if (UiElement.Properties.DEFAULT_ASSERT_IS_COLLECTOR.asBool()) {
                setDefault(CollectedAssertion.class);
            } else {
                setDefault(InstantAssertion.class);
            }
        }
        return injector.getInstance(threadLocalAssertionClass.get());
    }
}
