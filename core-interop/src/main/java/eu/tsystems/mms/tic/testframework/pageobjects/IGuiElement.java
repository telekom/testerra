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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IBinaryPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IStringPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * GuiElement with new fluent API support
 * @author Mike Reiche
 */
public interface IGuiElement extends
    Nameable<IGuiElement>,
    WebDriverRetainer
{
    enum Properties implements IProperties {
        DEFAULT_ASSERT_IS_COLLECTOR("tt.guielement.default.assertcollector", false),
        USE_JS_ALTERNATIVES("tt.guielement.use.js.alternatives", true),
        DELAY_AFTER_FIND_MILLIS("tt.delay.after.guielement.find.millis", 0),
        DELAY_BEFORE_ACTION_MILLIS("tt.delay.before.guielement.action.millis",0),
        DELAY_AFTER_ACTION_MILLIS("tt.delay.after.guielement.action.millis", 0),
        CHECK_RULE("tt.guielement.checkrule", CheckRule.IS_DISPLAYED.name()),
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
    /**
     * New Features
     */
    IStringPropertyAssertion<String> tagName();
    IStringPropertyAssertion<String> text();
    IStringPropertyAssertion<String> value();
    IStringPropertyAssertion<String> value(Attribute attribute);
    IStringPropertyAssertion<String> value(String attribute);
    IBinaryPropertyAssertion<Boolean> present();
    IBinaryPropertyAssertion<Boolean> visible(boolean complete);
    IBinaryPropertyAssertion<Boolean> displayed();
    IBinaryPropertyAssertion<Boolean> enabled();
    IBinaryPropertyAssertion<Boolean> selected();

    /**
     * Takes a screenshot of the current element
     */
    IImageAssertion screenshot();
    IGuiElement scrollTo();
    Locate getLocator();
    WebElement getWebElement();

    /**
     * Deprecated API
     */
    @Deprecated
    By getBy();

    @Deprecated
    Point getLocation();

    @Deprecated
    Dimension getSize();

    /**
     * This method scrolls to the element with an given offset.
     */
    IGuiElement scrollTo(int yOffset);
    IFrameLogic getFrameLogic();

    /**
     * Fluent {@link IGuiElement} overrides
     */
    IGuiElement select(Boolean select);

    IGuiElement click();

    IGuiElement clickJS();

    IGuiElement doubleClick();

    IGuiElement doubleClickJS();

    IGuiElement rightClick();

    IGuiElement rightClickJS();

    IGuiElement highlight();

    IGuiElement swipe(int offsetX, int offSetY);

    IGuiElement select();

    IGuiElement deselect();

    IGuiElement type(String text);

    IGuiElement sendKeys(CharSequence... charSequences);

    IGuiElement clear();

    IGuiElement mouseOver();

    IGuiElement mouseOverJS();
}
