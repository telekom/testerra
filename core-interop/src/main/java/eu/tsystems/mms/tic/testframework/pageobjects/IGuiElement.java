/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.InteractiveGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

/**
 * GuiElement with new fluent API support
 * @author Mike Reiche
 */
public interface IGuiElement extends
    InteractiveGuiElement,
    Nameable<IGuiElement>,
    WebDriverRetainer
{
    enum Properties implements IProperties {
        @Deprecated
        DEFAULT_ASSERT_IS_COLLECTOR("tt.guielement.default.assertcollector", false),
        USE_JS_ALTERNATIVES("tt.guielement.use.js.alternatives", true),
        DELAY_AFTER_FIND_MILLIS("tt.delay.after.guielement.find.millis", 0),
        DELAY_BEFORE_ACTION_MILLIS("tt.delay.before.guielement.action.millis",0),
        DELAY_AFTER_ACTION_MILLIS("tt.delay.after.guielement.action.millis", 0),
        CHECK_RULE("tt.guielement.checkrule", CheckRule.IS_DISPLAYED.name()),
        ELEMENT_TIMEOUT_SECONDS("tt.element.timeout.seconds", 8),
        ELEMENT_WAIT_INTERVAL_MS("tt.element.wait.ms", 200),
        ;
        private final String property;
        private Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return property;
        }

        @Override
        public IProperties newDefault(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public Double asDouble() {
            return PropertyManager.parser.getDoubleProperty(toString(), defaultValue);
        }
        @Override
        public Long asLong() { return PropertyManager.parser.getLongProperty(toString(), defaultValue); }
        @Override
        public Boolean asBool() { return PropertyManager.parser.getBooleanProperty(toString(), defaultValue); }
        @Override
        public String asString() { return PropertyManager.parser.getProperty(toString(), defaultValue); }
    }

    IFrameLogic getFrameLogic();

    /**
     * Deprecated API
     */
    @Deprecated
    By getBy();

    @Deprecated
    Point getLocation();

    @Deprecated
    Dimension getSize();
}
