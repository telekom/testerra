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

package eu.tsystems.mms.tic.testframework.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * An abstract property assertion without any test implementations.
 * Extend this class if you don't need to perform assertions, but compose others.
 * @author Mike Reiche
 */
public abstract class AbstractPropertyAssertion<T> implements ActualProperty<T> {

    protected final static PropertyAssertionFactory propertyAssertionFactory = Testerra.getInjector().getInstance(PropertyAssertionFactory.class);
    protected final AssertionProvider<T> provider;
    protected final AbstractPropertyAssertion<T> parent;
    protected PropertyAssertionConfig config;

    public AbstractPropertyAssertion(AbstractPropertyAssertion<T> parentAssertion, AssertionProvider<T> provider) {
        this.parent = parentAssertion;
        this.provider = provider;
    }

    @Override
    public T getActual() {
        return provider.getActual();
    }

    protected String createFailMessage(String givenSubject) {
        if (givenSubject != null && givenSubject.length() > 0) {
            return givenSubject;
        }

        StringBuilder sb = new StringBuilder();
        traceAncestors(propertyAssertion -> {
            String subject = propertyAssertion.provider.createSubject();
            if (subject != null) sb.append(subject).append(AssertionProvider.Format.SEPARATOR);
        });

        String subject = this.provider.createSubject();
        if (subject != null) sb.append(subject);

        return sb.toString();
    }

    private void traceAncestors(Consumer<AbstractPropertyAssertion> consumer) {
        AbstractPropertyAssertion parent = this.parent;
        if (parent != null) {
            parent.traceAncestors(consumer);
            consumer.accept(parent);
        }
    }

    protected void failedRecursive() {
        provider.failed(this);
        AbstractPropertyAssertion parentAssertion = parent;
        while (parentAssertion != null) {
            parentAssertion.provider.failed(this);
            parentAssertion = parentAssertion.parent;
        }
    }

    protected void failedFinallyRecursive() {
        provider.failedFinally(this);
        AbstractPropertyAssertion parentAssertion = parent;
        while (parentAssertion != null) {
            parentAssertion.provider.failedFinally(this);
            parentAssertion = parentAssertion.parent;
        }
    }

    protected void passedRecursive() {
        provider.passed(this);
        AbstractPropertyAssertion parentAssertion = parent;
        while (parentAssertion != null) {
            parentAssertion.provider.passed(this);
            parentAssertion = parentAssertion.parent;
        }
    }

    protected AssertionError wrapAssertionErrorRecursive(AssertionError assertionError) {
        assertionError = provider.wrapAssertionError(assertionError);
        AbstractPropertyAssertion parentAssertion = parent;
        while (parentAssertion != null) {
            parentAssertion.provider.wrapAssertionError(assertionError);
            parentAssertion = parentAssertion.parent;
        }
        return assertionError;
    }

}
