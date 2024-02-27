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

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.UiElementBase;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverRetainer;

/**
 * GuiElement with new fluent API support
 *
 * @author Mike Reiche
 */
public interface UiElement extends
        UiElementBase,
        InteractiveUiElement,
        PageObject<UiElement>,
        WebDriverRetainer,
        UiElementFinder {
    enum Properties implements IProperties {
        @Deprecated
        DEFAULT_ASSERT_IS_COLLECTOR("tt.guielement.default.assertcollector", false),
        DELAY_BEFORE_ACTION_MILLIS("tt.delay.before.guielement.action.millis", 0),
        DELAY_AFTER_ACTION_MILLIS("tt.delay.after.guielement.action.millis", 0),
        CHECK_RULE("tt.guielement.checkrule", CheckRule.IS_DISPLAYED.name()),
        ELEMENT_TIMEOUT_SECONDS("tt.element.timeout.seconds", 8),
        ELEMENT_WAIT_INTERVAL_MS("tt.element.wait.ms", 200),
        QA_ATTRIBUTE("tt.element.qa.attribute", "data-qa"),
        ;
        private final String property;
        private final Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return property;
        }

        @Override
        public Object getDefault() {
            return defaultValue;
        }
    }

    UiElementList<UiElement> list();

    UiElement shadowRoot();

    /**
     * @deprecated Use {@link UiElement#sensitiveData()}} instead
     */
    @Deprecated
    UiElement sensibleData();

    UiElement sensitiveData();
}
