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

import eu.tsystems.mms.tic.testframework.internal.asserts.AbstractPropertyAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.internal.asserts.DefaultQuantityAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import org.openqa.selenium.Rectangle;

/**
 * Default implementation of {@link VerticalDistanceAssertion}
 * @author Mike Reiche
 */
public class DefaultVerticalDistanceAssertion extends AbstractPropertyAssertion<Integer> implements VerticalDistanceAssertion {

    public DefaultVerticalDistanceAssertion(AbstractPropertyAssertion<Integer> parentAssertion, AssertionProvider<Integer> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public QuantityAssertion<Integer> toTopOf(TestableUiElement uiElement) {
        return propertyAssertionFactory.createWithParent(DefaultQuantityAssertion.class, this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle referenceRect = uiElement.waitFor().bounds().getActual();
                return provider.getActual()-referenceRect.y;
            }

            @Override
            public String createSubject() {
                return String.format("toTopOf(%s)", uiElement);
            }
        });
    }

    @Override
    public QuantityAssertion<Integer> toBottomOf(TestableUiElement uiElement) {
        return propertyAssertionFactory.createWithParent(DefaultQuantityAssertion.class, this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle referenceRect = uiElement.waitFor().bounds().getActual();
                return provider.getActual()-(referenceRect.y+referenceRect.height);
            }

            @Override
            public String createSubject() {
                return String.format("toBottomOf(%s)", uiElement);
            }
        });
    }
}
