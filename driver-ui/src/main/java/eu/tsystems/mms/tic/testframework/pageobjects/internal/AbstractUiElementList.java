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

package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementList;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.UiElementBase;
import java.util.Iterator;

public abstract class AbstractUiElementList<SELF extends UiElementBase> implements UiElementList<SELF> {
    private final SELF uiElement;
    private int iteratorIndex = 0;
    private int iteratorSize = 0;
    private int size = 0;

    public AbstractUiElementList(SELF uiElement) {
        this.uiElement = uiElement;

    }

    @Override
    public int size() {
        QuantityAssertion<Integer> assertion = uiElement.waitFor().foundElements();
        if (assertion.isGreaterThan(0)) {
            this.size = assertion.getActual();
        }
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size==0;
    }
    @Override
    public Iterator<SELF> iterator() {
        iteratorIndex = 0;
        iteratorSize = this.size();
        return this;
    }

    @Override
    abstract public SELF get(int i);

    @Override
    public boolean hasNext() {
        return iteratorIndex < iteratorSize;
    }

    @Override
    public SELF next() {
        return get(iteratorIndex++);
    }
}
