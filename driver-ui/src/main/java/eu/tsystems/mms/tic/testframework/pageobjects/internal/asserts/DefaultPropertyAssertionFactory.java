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

package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElement;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Default implementation of {@link PropertyAssertionFactory}
 * @author Mike Reiche
 */
public class DefaultPropertyAssertionFactory implements PropertyAssertionFactory, Loggable {
    private boolean throwErrors = true;
    private final TestController.Overrides overrides;

    @Inject
    DefaultPropertyAssertionFactory(TestController.Overrides overrides) {
        this.overrides = overrides;
    }

    @Override
    public <ASSERTION extends AbstractPropertyAssertion, TYPE> ASSERTION create(
        Class<ASSERTION> assertionClass,
        AbstractPropertyAssertion parentAssertion,
        AssertionProvider<TYPE> provider
    ) {
        ASSERTION assertion;
        try {
            Constructor<ASSERTION> constructor = assertionClass.getDeclaredConstructor(AbstractPropertyAssertion.class, AssertionProvider.class);
            assertion = constructor.newInstance(parentAssertion, provider);

            if (parentAssertion != null) {
                assertion.config = parentAssertion.config;
            } else {
                assertion.config = new PropertyAssertionConfig();
                assertion.config.throwErrors = throwErrors;
                assertion.config.timeoutInSeconds = overrides.getTimeoutInSeconds();
                assertion.config.pauseIntervalMs = UiElement.Properties.ELEMENT_WAIT_INTERVAL_MS.asLong();
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log().error(String.format("Unable to create assertion: %s", e.getMessage()), e);
            assertion = null;
        }
        return assertion;
    }

    @Override
    public PropertyAssertionFactory setThrowErrors(boolean throwErrors) {
        this.throwErrors = throwErrors;
        return this;
    }
}
