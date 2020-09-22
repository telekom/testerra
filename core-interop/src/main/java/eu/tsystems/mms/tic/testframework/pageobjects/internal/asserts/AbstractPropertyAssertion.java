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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract property assertion without any test implementations.
 * Extend this class if you don't need to perform assertions, but compose others.
 * @author Mike Reiche
 */
public abstract class AbstractPropertyAssertion<T> implements PropertyAssertion<T> {

    protected final PropertyAssertionFactory propertyAssertionFactory = Testerra.injector.getInstance(PropertyAssertionFactory.class);
    protected final AssertionProvider<T> provider;
    protected final AbstractPropertyAssertion<T> parent;
    protected boolean shouldWait;
    protected int timeout = -1;

    public AbstractPropertyAssertion(PropertyAssertion<T> parentAssertion, AssertionProvider<T> provider) {
        this.parent = (AbstractPropertyAssertion<T>)parentAssertion;
        this.provider = provider;
    }

    @Override
    public AbstractPropertyAssertion<T> shouldWait(boolean wait) {
        shouldWait = wait;
        return this;
    }

    @Override
    public AbstractPropertyAssertion<T> setTimeout(int seconds) {
        this.timeout = seconds;
        return this;
    }

    public T getActual() {
        return provider.getActual();
    }

    protected String createFailMessage(String prefixMessage) {
        StringBuilder sb = new StringBuilder();
        if (prefixMessage != null) {
            sb.append(prefixMessage).append(": ");
        }
        sb.append(traceSubjectString());
        return sb.toString();
    }

    private String traceSubjectString() {
        final List<String> subjects = new ArrayList<>();
        String subject = provider.getSubject();
        if (subject!=null) subjects.add(subject);

        AbstractPropertyAssertion assertion = parent;
        while (assertion != null) {
            subject = assertion.provider.getSubject();
            if (subject!=null) subjects.add(subject);
            assertion = assertion.parent;
        }
        Collections.reverse(subjects);
        return String.join(".", subjects);
    }

    public void failedRecursive() {
        provider.failed(this);
        AbstractPropertyAssertion parentAssertion = parent;
        while (parentAssertion != null) {
            parentAssertion.provider.failed(this);
            parentAssertion = parentAssertion.parent;
        }
    }

    public void failedFinallyRecursive() {
        provider.failedFinally(this);
        AbstractPropertyAssertion parentAssertion = parent;
        while (parentAssertion != null) {
            parentAssertion.provider.failedFinally(this);
            parentAssertion = parentAssertion.parent;
        }
    }
}
