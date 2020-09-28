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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Default implementation of {@link PropertyAssertionFactory}
 * @author Mike Reiche
 */
public class DefaultPropertyAssertionFactory implements PropertyAssertionFactory, Loggable {

    public <ASSERTION extends AbstractPropertyAssertion, TYPE> ASSERTION createAssertion(
            Class<ASSERTION> assertionClass,
            AbstractPropertyAssertion parentAssertion,
            AssertionProvider<TYPE> provider
    ) {
        ASSERTION assertion;
        try {
            Constructor<ASSERTION> constructor = assertionClass.getDeclaredConstructor(AbstractPropertyAssertion.class, AssertionProvider.class);
            assertion = constructor.newInstance(parentAssertion, provider);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log().error(String.format("Unable to create assertion: %s", e.getMessage()), e);
            assertion = null;
        }
        return assertion;
    }

    @Override
    public <ASSERTION extends AbstractPropertyAssertion, TYPE> ASSERTION createWithParent(
        Class<ASSERTION> assertionClass,
        AbstractPropertyAssertion parentAssertion,
        AssertionProvider<TYPE> provider
    ) {
        ASSERTION assertion = createAssertion(assertionClass, parentAssertion, provider);
        assertion.config = parentAssertion.config;
        return assertion;
    }

    @Override
    public <ASSERTION extends AbstractPropertyAssertion, TYPE> ASSERTION createWithConfig(
            Class<ASSERTION> assertionClass,
            PropertyAssertionConfig config,
            AssertionProvider<TYPE> provider
    ) {
        ASSERTION assertion = createAssertion(assertionClass, null, provider);
        assertion.config = config;
        return assertion;
    }
}
